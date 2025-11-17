/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github Tag.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "owner", "sha" })
final class RtTag implements Tag {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * SHA of the tag.
     */
    private final transient String sha;

    /**
     * Public constructor.
     * @param req The request.
     * @param repo The owner repo.
     * @param key The sha.
     */
    RtTag(
        final Request req,
        final Repo repo,
        final String key
    ) {
        this.sha = key;
        this.owner = repo;
        this.request = req.uri().path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo()).path("/git").path("/tags")
            .path(this.sha).back();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public String key() {
        return this.sha;
    }

}
