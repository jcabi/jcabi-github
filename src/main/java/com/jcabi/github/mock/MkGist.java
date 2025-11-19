/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Gist;
import com.jcabi.github.GistComments;
import com.jcabi.github.GitHub;
import com.jcabi.xml.XML;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * Mock GitHub gist.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "gist" })
@SuppressWarnings("PMD.TooManyMethods")
final class MkGist implements Gist {

    /**
     * XPath for gist IDs.
     */
    private static final String GIST_ID_XPATH = "/github/gists/gist/id/text()";

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Gist name.
     */
    private final transient String gist;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param name Gist name
     * @checkstyle ParameterNumber (5 lines)
     */
    MkGist(
        final MkStorage stg,
        final String login,
        final String name
    ) {
        this.storage = stg;
        this.self = login;
        this.gist = name;
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public String identifier() {
        return this.gist;
    }

    @Override
    public String read(
        final String file
    ) throws IOException {
        final List<XML> files = this.storage.xml().nodes(
            String.format(
                "%s/files/file[filename='%s']",
                this.xpath(), file
            )
        );
        if (files.isEmpty()) {
            throw new IOException(
                String.format("Couldn't find file with the name %s.", file)
            );
        }
        final List<String> contents = files.get(0)
            .xpath("raw_content/text()");
        String content = "";
        if (!contents.isEmpty()) {
            content = contents.get(0);
        }
        return content;
    }

    @Override
    public void write(
        final String file,
        final String content
    )
        throws IOException {
        this.storage.apply(
            // @checkstyle MultipleStringLiterals (3 lines)
            new Directives().xpath(this.xpath()).xpath(
                String.format("files[not(file[filename='%s'])]", file)
            ).add("file").add("filename").set(file).up().add("raw_content")
        );
        this.storage.apply(
            new Directives().xpath(this.xpath()).xpath(
                String.format(
                    "files/file[filename='%s']/raw_content",
                    file
                )
            ).set(content)
        );
    }

    @Override
    public void star() throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath())
                .attr("starred", Boolean.toString(true))
        );
    }

    @Override
    public void unstar() throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath())
                .attr("starred", Boolean.toString(false))
        );
    }

    @Override
    public boolean starred() throws IOException {
        final List<String> xpath = this.storage.xml().xpath(
            String.format("%s/@starred", this.xpath())
        );
        return !xpath.isEmpty() && StringUtils.equalsIgnoreCase(
            Boolean.toString(true),
            xpath.get(0)
        );
    }

    @Override
    public Gist fork() throws IOException {
        this.storage.lock();
        final String number;
        try {
            final XML xml = this.storage.xml();
            number = Integer.toString(
                1 + xml.xpath(MkGist.GIST_ID_XPATH).size()
            );
            final Directives dirs = new Directives().xpath("/github/gists")
                .add("gist")
                .add("id").set(number).up()
                .add("files");
            final List<XML> files = xml.nodes(
                String.format("%s/files/file", this.xpath())
            );
            for (final XML file : files) {
                final String filename = file.xpath("filename/text()").get(0);
                // @checkstyle MultipleStringLiterals (3 lines)
                dirs.add("file")
                    .add("filename").set(filename).up()
                    .add("raw_content").set(this.read(filename)).up().up();
            }
            this.storage.apply(dirs);
        } finally {
            this.storage.unlock();
        }
        return new MkGist(this.storage, this.self, number);
    }

    @Override
    public GistComments comments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/gists/gist[id='%s']",
            this.gist
        );
    }

}
