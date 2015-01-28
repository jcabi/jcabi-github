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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github Commits.
 * @author Ed Hillmann (edhillmann@yahoo.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
public final class RtCommits implements Commits {
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
    RtCommits(
        @NotNull(message = "req can't be NULL") final Request req,
        @NotNull(message = "repo can't be NULL") final Repo repo
    ) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri().path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/git")
            .path("/commits").back();
    }

    @Override
    @NotNull(message = "Repository is never NULL")
    public Repo repo() {
        return this.owner;
    }

    @Override
    @NotNull(message = "tag is never NULL")
    public Commit create(
        @NotNull(message = "params can't be NULL") final JsonObject params
    ) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(params).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("sha")
        );
    }

    @Override
    @NotNull(message = "tag is never NULL")
    public Commit get(
        @NotNull(message = "sha can't be NULL") final String sha
    ) {
        return new RtCommit(this.entry, this.owner, sha);
    }
}
