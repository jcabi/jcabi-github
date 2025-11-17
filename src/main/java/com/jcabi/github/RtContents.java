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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub contents.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (300 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class RtContents implements Contents {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param repo Repository
     */
    public RtContents(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/contents")
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Content readme() throws IOException {
        return new RtContent(
            this.entry, this.owner,
            this.entry.uri()
                .path("/repos")
                .path(this.owner.coordinates().user())
                .path(this.owner.coordinates().repo())
                .path("/readme")
                .back()
                .method(Request.GET)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json().readObject().getString("path")
        );
    }

    @Override
    public Content readme(
        final String branch
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("ref", branch)
            .build();
        return new RtContent(
            this.entry, this.owner,
            this.entry.uri()
                .path("/repos")
                .path(this.owner.coordinates().user())
                .path(this.owner.coordinates().repo())
                .path("/readme")
                .back()
                .method(Request.GET)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json().readObject().getString("path")
        );
    }

    @Override
    public Content create(
        final JsonObject content
    )
        throws IOException {
        if (!content.containsKey("path")) {
            throw new IllegalStateException(
                "Content should have path parameter"
            );
        }
        final String path = content.getString("path");
        return new RtContent(
            this.entry, this.owner,
            this.request.method(Request.PUT)
                .uri().path(path).back()
                .body().set(content).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getJsonObject("content").getString("path")
        );
    }

    @Override
    public Content get(
        final String path,
        final String ref
    ) throws IOException {
        return this.content(path, ref);
    }

    @Override
    public Content get(
        final String path
    ) throws IOException {
        return this.content(path, this.repo().defaultBranch().name());
    }

    @Override
    public Iterable<Content> iterate(
        final String path,
        final String ref
    ) {
        return new RtPagination<>(
            this.request.method(Request.GET)
                .uri().path(path).queryParam("ref", ref).back(),
            object -> new RtContent(
                this.entry, this.owner,
                object.getString("path")
            )
        );
    }

    @Override
    public RepoCommit remove(final JsonObject content
    )
        throws IOException {
        if (!content.containsKey("path")) {
            throw new IllegalStateException(
                "Content should have path parameter"
            );
        }
        final String path = content.getString("path");
        return new RtRepoCommit(
            this.entry,
            this.owner,
            this.request.method(Request.DELETE)
                .uri().path(path).back()
                .body().set(content).back().fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json()
                .readObject().getJsonObject("commit").getString("sha")
        );
    }

    @Override
    public RepoCommit update(
        final String path,
        final JsonObject json)
        throws IOException {
        return new RtRepoCommit(
            this.entry,
            this.owner,
            this.request.uri().path(path).back()
                .method(Request.PUT)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json()
                .readObject().getJsonObject("commit").getString("sha")
        );
    }

    @Override
    public boolean exists(final String path, final String ref)
        throws IOException {
        final RestResponse response = this.request.method(Request.GET)
            .uri().path(path).queryParam("ref", ref).back()
            .fetch().as(RestResponse.class);
        return response.status() == HttpURLConnection.HTTP_OK;
    }

    /**
     * Get the contents of a file or symbolic link in a repository.
     * @param path The content path
     * @param ref The name of the commit/branch/tag.
     * @return Content fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    private Content content(
        final String path, final String ref
    ) throws IOException {
        final String name = "ref";
        RtContent content = null;
        final JsonStructure structure = this.request.method(Request.GET)
                .uri().path(path).queryParam(name, ref).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json().read();
        if (JsonValue.ValueType.OBJECT.equals(structure.getValueType())) {
            content = new RtContent(
                    this.entry.uri().queryParam(name, ref).back(), this.owner,
                    ((JsonObject) structure).getString("path")
            );
        }
        return content;
    }

}
