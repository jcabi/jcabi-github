/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import com.jcabi.github.GitHub;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub gists.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
@SuppressWarnings({"PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
final class MkGists implements Gists {

    /**
     * XPath suffix for gist ID text.
     */
    private static final String GIST_ID_TEXT_PATH = "/gist/id/text()";

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkGists(
        final MkStorage stg,
        final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath("/github").addIf("gists")
        );
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public Gist create(
        final Map<String, String> files, final boolean visible
    ) throws IOException {
        this.storage.lock();
        final String number;
        try {
            number = Integer.toString(
                1 + this.storage.xml().xpath(
                    MkGists.xpath().concat(MkGists.GIST_ID_TEXT_PATH)
                ).size()
            );
            final Directives dirs = new Directives().xpath(MkGists.xpath())
                .add("gist")
                .add("id").set(number).up()
                .add("public").set(String.valueOf(visible)).up()
                .add("files");
            for (final Map.Entry<String, String> file : files.entrySet()) {
                dirs.add("file")
                    .add("filename").set(file.getKey()).up()
                    .add("raw_content").set(file.getValue()).up().up();
            }
            this.storage.apply(dirs);
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public Gist get(final String name
    ) {
        return new MkGist(this.storage, this.self, name);
    }

    @Override
    public Iterable<Gist> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/gist", MkGists.xpath()),
            xml -> this.get(xml.xpath("id/text()").get(0))
        );
    }

    @Override
    public void remove(final String identifier
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/gist[id='%s']", MkGists.xpath(), identifier)
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private static String xpath() {
        return "/github/gists";
    }

}
