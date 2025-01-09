/**
 * Copyright (c) 2013-2025, jcabi.com
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
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github labels of a repo.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "owner" })
final class RtLabels implements Labels {

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Github.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repo we're in
     */
    RtLabels(final Request req, final Repo repo) {
        this.owner = repo;
        final Coordinates coords = repo.coordinates();
        this.entry = req;
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/labels")
            .back();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Label create(
        final String name,
        final String color)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (1 line)
            .add("name", name)
            .add("color", color)
            .build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json();
        return new RtLabel(this.entry, this.owner, name);
    }

    @Override
    public Label get(final String name) {
        return new RtLabel(this.entry, this.owner, name);
    }

    @Override
    public void delete(final String name) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Label> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtLabel(
                this.entry,
                this.owner,
                object.getString("name")
            )
        );
    }

}
