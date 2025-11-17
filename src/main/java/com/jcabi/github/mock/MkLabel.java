/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github label.
 *
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "owner", "label" })
final class MkLabel implements Label {

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
    private final transient Coordinates owner;

    /**
     * Name of the label.
     */
    private final transient String label;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param name Label name
     * @checkstyle ParameterNumber (5 lines)
     */
    MkLabel(final MkStorage stg,
        final String login,
        final Coordinates rep,
        final String name
    ) {
        this.storage = stg;
        this.self = login;
        this.owner = rep;
        this.label = name;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.owner);
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public void patch(final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public int compareTo(final Label lbl
    ) {
        return this.label.compareTo(lbl.name());
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/labels/label[name='%s']",
            this.owner, this.label
        );
    }

}
