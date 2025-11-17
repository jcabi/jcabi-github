/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Mock of Github Commit.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords", "identifier" })
public final class MkCommit implements Commit {

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
     * The Commit's sha.
     */
    private final transient String identifier;

    /**
     * Public constructor.
     * @param strg The storage.
     * @param login The login name
     * @param crds Credential
     * @param commitsha Commit's sha.
     * @checkstyle ParameterNumber (5 lines)
     */
    public MkCommit(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final String commitsha
    ) {
        this.storage = strg;
        this.self = login;
        this.coords = crds;
        this.identifier = commitsha;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String sha() {
        return this.identifier;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public int compareTo(final Commit commit) {
        return this.identifier.compareTo(commit.sha());
    }

    /**
     * XPath of this element in XML tree.
     *
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords = '%s']/git/commits/commit[sha = '%s']",
            this.coords, this.identifier
        );
    }
}
