/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 * <p/>
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
 * <p/>
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
import lombok.EqualsAndHashCode;

import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Github milestones.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
public class GhMilestones implements Milestones {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
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
    GhMilestones(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
                .path("/repos")
                .path(coords.user())
                .path(coords.repo())
                .path("/milestones")
                .back();
        this.owner = repo;
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
    public Milestone create(
            @NotNull(message = "title can't be NULL") final String title)
            throws IOException {
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
                .writeStartObject()
                .write("title", title)
                .writeEnd()
                .close();
        return this.get(
                this.request.method(Request.POST)
                        .body().set(post.toString()).back()
                        .fetch().as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_CREATED)
                        .as(JsonResponse.class)
                        .json().readObject().getInt("number")
        );
    }

    @Override
    public Milestone get(int number) {
        return new GhMilestone(this.entry, this.owner, number);
    }

    @Override
    public Iterable<Milestone> iterate(
        @NotNull(message = "map or params can't be NULL")
        final Map<String, String> params) {
            return new GhPagination<Milestone>(
                    this.request.uri().queryParams(params).back(),
                    new GhPagination.Mapping<Milestone>() {
                        @Override
                        public Milestone map(final JsonObject object) {
                            return GhMilestones.this.get(object.getInt("number"));
                        }
                    }
            );
    }
}
