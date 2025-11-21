/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Milestone;
import com.jcabi.github.Repo;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * Mock GitHub milestone.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
final class MkMilestone implements Milestone {

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
     * The number of the MkMilestone.
     */
    private final transient int code;

    /**
     * MkMilestone constructor.
     * @param strg The storage
     * @param login The user to login with
     * @param crds The repo
     * @param num The number of the MkMilestone
     * @checkstyle ParameterNumber (5 lines)
     */
    MkMilestone(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final int num
    ) {
        this.self = login;
        this.coords = crds;
        this.storage = strg;
        this.code = num;
    }

    @Override
    public boolean equals(
        final Object obj
    ) {
        return obj instanceof Milestone
            && this.code == Milestone.class.cast(obj).number();
    }

    @Override
    public int hashCode() {
        return this.code;
    }

    @Override
    public int compareTo(
        final Milestone milestone
    ) {
        assert this.self != null;
        assert this.coords != null;
        assert this.storage != null;
        assert this.code != -1;
        int result = this.coords.compareTo(
            milestone.repo().coordinates()
        );
        if (result == 0) {
            result = Integer.compare(this.code, milestone.number());
        }
        return result;
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
        new JsonPatch(
            this.storage
        ).patch(
            this.xpath(),
            json
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(
            this.storage,
            this.self,
            this.coords
        );
    }

    @Override
    public int number() {
        return this.code;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/milestones[number='%d']",
            this.coords,
            this.code
        );
    }

}
