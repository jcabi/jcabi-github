/**
 * Copyright (c) 2013-2015, jcabi.com
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
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github contents.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
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
    ) throws IOException {
        return this.content(path, ref);
    }

    @Override
    @NotNull(message = "Content can't be NULL")
    public Content get(
        @NotNull(message = "path can't be NULL") final String path
    ) throws IOException {
        return this.content(path, "master");
    }

    @Override
    @NotNull(message = "Iterable of Content can't be NULL")
    public Iterable<Content> iterate(
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "ref can't be NULL") final String ref
    ) {
        return new RtPagination<Content>(
            this.request.method(Request.GET)
                .uri().path(path).queryParam("ref", ref).back(),
            new RtValuePagination.Mapping<Content, JsonObject>() {
                @Override
                public Content map(final JsonObject object) {
                    return new RtContent(
                        RtContents.this.entry, RtContents.this.owner,
                        object.getString("path")
                    );
                };
            }
        );
    }

    @Override
    @NotNull(message = "Repo commit is never NULL")
    public RepoCommit remove(@NotNull(message = "content can't be NULL")
        final JsonObject content
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
     * @see <a href="http://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    private Content content(
        final String path, final String ref
    ) throws IOException {
        final String name = "ref";
        return new RtContent(
            this.entry.uri().queryParam(name, ref).back(), this.owner,
            this.request.method(Request.GET)
                .uri().path(path).queryParam(name, ref).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json().readObject().getString("path")
        );
    }

}
