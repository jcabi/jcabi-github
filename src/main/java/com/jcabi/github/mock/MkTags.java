/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Tag;
import com.jcabi.github.Tags;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of GitHub Tags.
 * @since 0.15
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings({"PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
final class MkTags implements Tags {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo's name.
     */
    private final transient Coordinates coords;

    /**
     * Public constructor.
     * @param stg The storage.
     * @param login The login name.
     * @param rep Repo's coordinates.
     * @throws IOException If something goes wrong.
     */
    MkTags(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/git",
                    this.coords
                )
            ).addIf("tags")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Tag create(
        final JsonObject params
    ) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath()).add("tag");
        for (final Map.Entry<String, JsonValue> entry : params.entrySet()) {
            dirs.add(entry.getKey()).set(entry.getValue().toString()).up();
        }
        this.storage.apply(dirs);
        new MkReferences(this.storage, this.self, this.coords).create(
            new StringBuilder().append("refs/tags/").append(
                params.getString("name")
            ).toString(),
            params.getString("sha")
        );
        return this.get(params.getString("sha"));
    }

    @Override
    public Tag get(final String sha) {
        return new MkTag(this.storage, this.self, this.coords, sha);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/tags",
            this.coords
        );
    }

}
