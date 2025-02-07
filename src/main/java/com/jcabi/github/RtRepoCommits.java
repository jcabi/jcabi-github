/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import lombok.EqualsAndHashCode;

/**
 * Commits of a Github repository.
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "entry" })
final class RtRepoCommits implements RepoCommits {

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;

    /**
     * RESTful request for the comparison.
     */
    private final transient Request comp;

    /**
     * Parent repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository
     */
    RtRepoCommits(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        final RequestURI rep = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo());
        this.request = rep
            .path("/commits")
            .back();
        this.comp = rep
            .path("/compare")
            .back();
    }

    @Override
    public  Iterable<RepoCommit> iterate(
        final Map<String, String> params
    ) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            value -> this.get(value.getString("sha"))
        );
    }

    @Override
    public RepoCommit get(
        final String sha
    ) {
        return new RtRepoCommit(this.entry, this.owner, sha);
    }

    @Override
    public CommitsComparison compare(
        final String base,
        final String head) {
        return new RtCommitsComparison(this.entry, this.owner, base, head);
    }

    @Override
    public String diff(
        final String base,
        final String head)
        throws IOException {
        return this.comp.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.diff")
            .uri()
            .path(String.format("%s...%s", base, head))
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public String patch(
        final String base,
        final String head)
        throws IOException {
        return this.comp.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.patch")
            .uri()
            .path(String.format("%s...%s", base, head))
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
