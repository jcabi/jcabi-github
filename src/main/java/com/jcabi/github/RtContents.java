/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.rexsl.test.Request;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
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
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Content readme() {
        throw new UnsupportedOperationException("Create not yet implemented.");
    }

    @Override
    public Content create(
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "message can't be NULL") final String message,
        @NotNull(message = "content can't be NULL") final String content)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("message", message)
            // @checkstyle MultipleStringLiterals (1 line)
            .add("content", content)
            .build();
        return new RtContent(this.entry, this.owner,
            this.request.method(Request.PUT)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getJsonObject("content").getString("path")
        );
    }

}
