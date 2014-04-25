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
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github forks.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/forks/">Forks API</a>
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "owner" })
public final class RtForks implements Forks {

    /**
     * Fork's id name in JSON object.
     */
    public static final String ID = "id";

    /**
     * Restful Request.
     */
    private final transient Request request;
    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    public RtForks(final Request req, final Repo repo) {
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("forks")
            .back();
        this.owner = repo;
    }

    @Override
    @NotNull(message = "Repository can't be null")
    public Repo repo() {
        return this.owner;
    }

    @Override
    @NotNull(message = "iterable of Fork can't be NULL")
    public Iterable<Fork> iterate(
        @NotNull(message = "sort can't be NULL") final String sort) {
        return new RtPagination<Fork>(
            this.request.uri().queryParam("sort", sort).back(),
            new RtPagination.Mapping<Fork, JsonObject>() {
                @Override
                public Fork map(final JsonObject object) {
                    return RtForks.this.get(object.getInt(ID));
                }
            }
        );
    }

    @Override
    @NotNull(message = "Fork can't be NULL")
    public Fork create(
        @NotNull(message = "organization can't be NULL")
        final String organization) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("organization", organization)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_ACCEPTED)
                .as(JsonResponse.class)
                .json().readObject().getInt(ID)
        );
    }

    /**
     * Get fork by number.
     * @param number Fork number
     * @return Fork
     */
    private RtFork get(final Integer number) {
        return new RtFork(this.request, this.owner, number);
    }
}
