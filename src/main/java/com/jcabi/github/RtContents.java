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
import javax.json.JsonValue;
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
        return this.content(path, "master");
    }

    @Override
    public Iterable<Content> iterate(
        final String path,
        final String ref
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
     * @see <a href="http://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
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
