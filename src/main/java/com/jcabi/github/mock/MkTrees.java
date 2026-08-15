/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Tree;
import com.jcabi.github.Trees;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of GitHub Trees.
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkTrees implements Trees {

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
    MkTrees(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this(MkTrees.bootstrap(stg, rep), rep, login);
    }

    private MkTrees(final MkStorage stg, final Coordinates rep, final String login) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Tree create(final JsonObject params) throws IOException {
        final JsonArray trees = params.getJsonArray("tree");
        for (final JsonValue val : trees) {
            final JsonObject tree = (JsonObject) val;
            final String sha = tree.getString("sha");
            final Directives dirs = new Directives().xpath(this.xpath())
                .add("tree");
            for (final Map.Entry<String, JsonValue> entry : tree.entrySet()) {
                dirs.add(entry.getKey()).set(entry.getValue().toString()).up();
            }
            this.storage.apply(dirs);
            final String ref;
            if (tree.containsKey("name")) {
                ref = tree.getString("name");
            } else {
                ref = sha;
            }
            new MkReferences(this.storage, this.self, this.coords).create(
                String.format("refs/trees/%s", ref),
                sha
            );
        }
        return this.get(trees.getJsonObject(0).getString("sha"));
    }

    @Override
    public Tree get(final String sha) {
        return new MkTree(this.storage, this.self, this.coords, sha);
    }

    @Override
    public Tree getRec(final String sha) {
        return new MkTree(this.storage, this.self, this.coords, sha);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/trees",
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
            ).addIf("trees")
        );
        return stg;
    }
}
