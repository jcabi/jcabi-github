/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub repositories.
 *
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "entry" })
final class RtRepos implements Repos {

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtRepos(final GitHub github, final Request req) {
        this.ghub = github;
        this.entry = req;
    }

    @Override
    public String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public Repo create(final Repos.RepoCreate settings) throws IOException {
        String path = "user/repos";
        final String org = settings.organization();
        if (org != null && !org.isEmpty()) {
            path = "/orgs/".concat(org).concat("/repos");
        }
        return this.get(
            new Coordinates.Simple(
                this.entry.uri().path(path)
                    .back().method(Request.POST)
                    .body().set(settings.json()).back()
                    .fetch().as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_CREATED)
                    .as(JsonResponse.class)
                    // @checkstyle MultipleStringLiterals (1 line)
                    .json().readObject().getString("full_name")
            )
        );
    }

    @Override
    public Repo get(final Coordinates name) {
        return new RtRepo(this.ghub, this.entry, name);
    }

    @Override
    public void remove(
        final Coordinates coords) throws IOException {
        this.entry.uri().path("/repos")
            .back().method(Request.DELETE)
            .uri().path(coords.toString()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Repo> iterate(
        final String identifier) {
        return new RtPagination<>(
            this.entry.uri().queryParam("since", identifier).back(),
            object -> this.get(
                new Coordinates.Simple(object.getString("full_name"))
            )
        );
    }

    @Override
    public boolean exists(final Coordinates coords) throws IOException {
        final String repo = coords.user().concat("/").concat(coords.repo());
        final RestResponse response = this.entry.uri()
            .path("/repos/".concat(repo)).back()
            .method(Request.GET).fetch().as(RestResponse.class);
        return response.status() == HttpURLConnection.HTTP_OK;
    }
}
