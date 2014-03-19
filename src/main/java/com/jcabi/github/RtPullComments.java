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
import com.jcabi.http.RequestURI;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github pull comment.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
public final class RtPullComments implements PullComments {

    /**
     * Path element that retrieves repository details.
     */
    private static final String REPOS_PATH = "/repos";

    /**
     * Path element that retrieves pull requests.
     */
    private static final String PULLS_PATH = "/pulls";

    /**
     * Path element that retrieves comments.
     */
    private static final String COMMENTS_PATH = "/comments";

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Owner of comments.
     */
    private final transient Pull owner;

    /**
     * Public ctor.
     * @param req Request
     * @param pull Pull
     */
    RtPullComments(final Request req, final Pull pull) {
        this.entry = req;
        this.owner = pull;
        this.request = constructRequest(null);
    }

    /**
     * Constructs a request against the GitHub API.
     * @param pullrequest A identified of a Pull request.  Can be null
     * @return A <code>Request</code> object
     */
    private Request constructRequest(final Integer pullrequest) {
        final Coordinates coords = this.owner.repo().coordinates();
        RequestURI requesturi;
        if (pullrequest == null) {
            requesturi = this.entry.uri()
                .path(REPOS_PATH)
                .path(coords.user())
                .path(coords.repo())
                .path(PULLS_PATH)
                .path(COMMENTS_PATH);
        } else {
            requesturi = this.entry.uri()
                .path(REPOS_PATH)
                .path(coords.user())
                .path(coords.repo())
                .path(PULLS_PATH)
                .path(pullrequest.toString())
                .path(COMMENTS_PATH);
        }
        return requesturi.back();
    }

    @Override
    @NotNull(message = "Pull is never NUll")
    public Pull pull() {
        return this.owner;
    }

    @Override
    @NotNull(message = "PullComment is never NULL")
    public PullComment get(final int number) {
        return new RtPullComment(this.entry, this.owner, number);
    }

    @Override
    @NotNull(message = "Iterable of pull comments is never NULL")
    public Iterable<PullComment> iterate(final Map<String, String> params) {
        return new RtPagination<PullComment>(
            this.request.uri().queryParams(params).back(),
            new RtPagination.Mapping<PullComment, JsonObject>() {
                @Override
                public PullComment map(final JsonObject value) {
                    return RtPullComments.this.get(
                        // @checkstyle MultipleStringLiterals (1 line)
                        value.getInt("id")
                    );
                }
            }
        );
    }

    @Override
    public Iterable<PullComment> iterate(final int number,
        final Map<String, String> params) {
        final Request newreq = this.constructRequest(number);
        return new RtPagination<PullComment>(
            newreq.uri().queryParams(params).back(),
            new RtPagination.Mapping<PullComment, JsonObject>() {
                @Override
                public PullComment map(final JsonObject value) {
                    return RtPullComments.this.get(
                        // @checkstyle MultipleStringLiterals (1 line)
                        value.getInt("id")
                    );
                }
            }
        );
    }

    // @checkstyle ParameterNumberCheck (7 lines)
    @Override
    @NotNull(message = "PullComment is never NULL")
    public PullComment post(
        @NotNull(message = "body can't be NULL") final String body,
        @NotNull(message = "commit can't be NULL") final String commit,
        @NotNull(message = "path can't be NULL") final String path,
        final int position
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("body", body)
            .add("commit_id", commit)
            .add("path", path)
            .add("position", position)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                // @checkstyle MultipleStringLiterals (1 line)
                .json().readObject().getInt("id")
        );
    }

    @Override
    @NotNull(message = "pull comment is never NULL")
    public PullComment reply(
        @NotNull(message = "text can't be NULL") final String text,
        @NotNull(message = "comment can't be NULL") final int comment
    ) throws IOException {
        throw new UnsupportedOperationException("Reply not yet implemented.");
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.uri().path(String.valueOf(number)).back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
