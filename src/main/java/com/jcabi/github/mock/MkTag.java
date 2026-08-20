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
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Mock of GitHub Tag.
 * @since 0.15
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords", "sha" })
final class MkTag implements Tag {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * The Tag's sha.
     */
    private final transient String sha;

    /**
     * Public constructor.
     * @param strg The storage
     * @param login The login name
     * @param crds Credential
     * @param identifier Tag's sha
     */
    MkTag(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final String identifier
    ) {
        this(
            strg,
            login,
            String.format("\"%s\"", identifier),
            crds
        );
    }

    private MkTag(
        final MkStorage storage,
        final String self,
        final String sha,
        final Coordinates coords
    ) {
        this.storage = storage;
        this.self = self;
        this.sha = sha;
        this.coords = coords;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String key() {
        return this.sha;
    }

    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords = '%s']/git/tags/tag[sha = '%s']",
            this.coords, this.sha
        );
    }
}
