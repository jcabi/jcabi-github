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
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Mock of GitHub Tag.
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
     * @param strg The storage.
     * @param login The login name
     * @param crds Credential
     * @param identifier Tag's sha.
     * @checkstyle ParameterNumber (5 lines)
     */
    MkTag(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final String identifier
    ) {
        this.storage = strg;
        this.self = login;
        this.coords = crds;
        this.sha = new StringBuilder().append('"').append(identifier)
            .append('"').toString();
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

    /**
     * XPath of this element in XML tree.
     *
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords = '%s']/git/tags/tag[sha = '%s']",
            this.coords, this.sha
        );
    }

}
