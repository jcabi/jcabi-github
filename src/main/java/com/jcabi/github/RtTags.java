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
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github Tags.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
final class RtTags implements Tags {
    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public constructor.
     * @param req The entry request.
     * @param repo The owner repo.
     */
    RtTags(
        final Request req,
        final Repo repo
    ) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri().path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo()).path("/git").path("/tags").back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Tag create(
        final JsonObject params
    ) throws IOException {
        final Tag created = this.get(
            this.request.method(Request.POST)
                .body().set(params).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("sha")
        );
        new RtReferences(this.entry, this.owner).create(
            new StringBuilder().append("refs/tags/").append(
                params.getString("tag")
            ).toString(),
            created.key()
        );
        return created;
    }

    @Override
    public Tag get(
        final String sha
    ) {
        return new RtTag(this.entry, this.owner, sha);
    }

}
