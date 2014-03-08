/**
 * Copyright (c) 2013-2014, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (300 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtContents implements Contents {

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
    @NotNull(message = "repository can't be NULL")
    public Repo repo() {
        return this.owner;
    }

    @Override
    @NotNull(message = "Content can't be NULL")
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
    @NotNull(message = "Content can't be NULL")
    public Content readme(
        @NotNull(message = "branch can't be NULL") final String branch
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
    @NotNull(message = "Content can't be NULL")
    public Content create(
        @NotNull(message = "JSON can't be NULL") final JsonObject content
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
    @NotNull(message = "Content can't be NULL")
    public Content get(
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "ref can't be NULL") final String ref
    )
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("path", path)
            .add("ref", ref)
            .build();
        return new RtContent(
            this.entry, this.owner,
            this.request.method(Request.GET)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json().readObject().getString("path")
        );
    }

    // @checkstyle ParameterNumberCheck (9 lines)
    @Override
    @NotNull(message = "RepoCommit can't be NULL")
    public RepoCommit remove(
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "message can't be NULL") final String message,
        @NotNull(message = "sha can't be NULL") final String sha,
        @NotNull(message = "branch can't be NULL") final String branch,
        @NotNull(message = "committer can't be NULL")
        final Map<String, String> committer,
        @NotNull(message = "author can't be NULL")
        final Map<String, String> author)
        throws IOException {
        final JsonObjectBuilder cmtBuilder = Json.createObjectBuilder();
        for (final Map.Entry<String, String> entr : committer.entrySet()) {
            cmtBuilder.add(entr.getKey(), entr.getValue());
        }
        final JsonObjectBuilder atrBuilder = Json.createObjectBuilder();
        for (final Map.Entry<String, String> entr : author.entrySet()) {
            atrBuilder.add(entr.getKey(), entr.getValue());
        }
        final JsonStructure json = Json.createObjectBuilder()
            .add("message", message)
            .add("sha", sha)
            .add("branch", branch)
            .add("committer", cmtBuilder.build())
            .add("author", atrBuilder.build())
            .build();
        return new RtRepoCommit(
            this.entry,
            this.owner,
            this.request.method(Request.DELETE)
                .uri().path(path).back()
                .body().set(json).back().fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class).json()
                .readObject().getJsonObject("commit").getString("sha")
        );
    }

    @Override
    @NotNull(message = "RepoCommit can't be NULL")
    public RepoCommit update(
        @NotNull(message = "path is never NULL") final String path,
        @NotNull(message = "json is never NULL") final JsonObject json)
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

}
