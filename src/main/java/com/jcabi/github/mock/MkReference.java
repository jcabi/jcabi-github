/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Reference;
import com.jcabi.github.Repo;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Mock of Github Reference.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords", "name" })
final class MkReference implements Reference {

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
     * The Reference's name.
     */
    private final transient String name;

    /**
     * Public constructor.
     * @param strg Storage.
     * @param login Login name.
     * @param crds Repo coordinates.
     * @param reference Name of the reference.
     * @checkstyle ParameterNumber (5 lines)
     */
    MkReference(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final String reference
    ) {
        this.storage = strg;
        this.self = login;
        this.coords = crds;
        this.name = reference;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String ref() {
        return this.name;
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
     *
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/refs/reference[ref='%s']",
            this.coords, this.name
        );
    }
}
