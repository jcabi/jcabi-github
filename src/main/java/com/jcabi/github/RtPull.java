/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonStructure;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * GitHub pull request.
 *
 * @since 0.3
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
@SuppressWarnings("PMD.TooManyMethods")
final class RtPull implements Pull {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Pull request number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param number Number of the get
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    RtPull(final Request req, final Repo repo, final int number) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/pulls")
            .path(Integer.toString(number))
            .back();
        this.owner = repo;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public Iterable<Commit> commits() {
        return new RtPagination<>(
            this.request.uri().path("/commits").back(),
            object -> new RtCommit(
                this.entry,
                this.owner,
                object.getString("sha")
            )
        );
    }

    @Override
    public Iterable<JsonObject> files() {
        return new RtPagination<>(
            this.request.uri().path("/files").back(),
            RtPagination.COPYING
        );
    }

    @Override
    public void merge(
        final String msg)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("commit_message", msg)
            .build();
        this.merge(json).assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public MergeState merge(
        final String msg,
        final String sha)
        throws IOException {
        final JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("commit_message", msg).add("sha", sha);
        final RestResponse response = this.merge(builder.build())
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_OK,
                        HttpURLConnection.HTTP_BAD_METHOD,
                        HttpURLConnection.HTTP_CONFLICT
                    )
                )
            );
        final MergeState state;
        switch (response.status()) {
            case HttpURLConnection.HTTP_OK:
                state = MergeState.SUCCESS;
                break;
            case HttpURLConnection.HTTP_BAD_METHOD:
                state = MergeState.NOT_MERGEABLE;
                break;
            default:
                state = MergeState.BAD_HEAD;
                break;
        }
        return state;
    }

    @Override
    public PullComments comments() {
        return new RtPullComments(this.entry, this);
    }

    @Override
    public Checks checks() throws IOException {
        return new RtChecks(this.entry, this);
    }

    @Override
    public PullRef base() throws IOException {
        return new RtPullRef(
            this.owner.github(),
            this.json().getJsonObject("base")
        );
    }

    @Override
    public PullRef head() throws IOException {
        return new RtPullRef(
            this.owner.github(),
            this.json().getJsonObject("head")
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public int compareTo(
        final Pull pull
    ) {
        return this.number() - pull.number();
    }

    /**
     * Helper method for merge operations.
     * @param payload The JSON payload for the merge
     * @return Response received from GitHub
     * @throws IOException If there is any I/O problem
     */
    private RestResponse merge(final JsonStructure payload)
        throws IOException {
        return this.request.uri()
            .path("/merge").back()
            .body().set(payload).back()
            .method(Request.PUT)
            .fetch()
            .as(RestResponse.class);
    }

}
