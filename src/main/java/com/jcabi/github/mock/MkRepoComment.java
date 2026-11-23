/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoComment;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub commit comment.
 *
 * @since 2.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords", "num" })
final class MkRepoComment implements RepoComment {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Comment number.
     */
    private final transient long num;

    /**
     * Public ctor.
     *
     * @param stg Storage
     * @param login User to login
     * @param repo Repository coordinates
     * @param number Comment number
     */
    MkRepoComment(
        final MkStorage stg,
        final String login,
        final Coordinates repo,
        final long number
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
        this.num = number;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).strict(1).remove()
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public int compareTo(final RepoComment other) {
        return Long.compare(this.number(), other.number());
    }

    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/comments/comment[id='%d']",
            this.coords, this.num
        );
    }
}
