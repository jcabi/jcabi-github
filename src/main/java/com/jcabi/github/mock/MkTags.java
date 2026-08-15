/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
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
     * @param stg The storage
     * @param login The login name
     * @param rep Repo's coordinates
     * @throws IOException If something goes wrong.
     */
    MkTags(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this(MkTags.bootstrap(stg, rep), rep, login);
    }

    private MkTags(final MkStorage stg, final Coordinates rep, final String login) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Tag create(final JsonObject params) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath()).add("tag");
        for (final Map.Entry<String, JsonValue> entry : params.entrySet()) {
            dirs.add(entry.getKey()).set(entry.getValue().toString()).up();
        }
        this.storage.apply(dirs);
        new MkReferences(this.storage, this.self, this.coords).create(
            String.format("refs/tags/%s", params.getString("name")),
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

    /**
     * Prepare the storage.
     * @param stg Storage
     * @param rep Coordinates
     * @return The same storage
     * @throws IOException If fails
     */
    private static MkStorage bootstrap(final MkStorage stg, final Coordinates rep)
        throws IOException {
        stg.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/git",
                    rep
                )
            ).addIf("tags")
        );
        return stg;
    }
}
