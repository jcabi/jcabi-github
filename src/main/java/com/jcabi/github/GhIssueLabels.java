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
import java.io.StringWriter;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github get labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "entry")
final class GhIssueLabels implements Labels {

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param req Request
     * @param issue Issue we're in
     */
    GhIssueLabels(final Request req, final Issue issue) {
        final Coordinates coords = issue.repo().coordinates();
        this.entry = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path(Integer.toString(issue.number()))
            .path("/labels")
            .back();
    }

    @Override
    public String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    public void add(@NotNull(message = "iterable of labels can't be NULL")
        final Iterable<Label> labels) throws IOException {
        final StringWriter post = new StringWriter();
        final JsonGenerator json = Json.createGenerator(post)
            .writeStartArray();
        for (final Label label : labels) {
            json.write(label.name());
        }
        json.writeEnd().close();
        this.entry.method(Request.POST)
            .body().set(post.toString()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray();
    }

    @Override
    public void remove(@NotNull(message = "label name can't be NULL")
        final String name) throws IOException {
        this.entry.method(Request.DELETE)
            .uri().path(name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public void clear() throws IOException {
        this.entry.method(Request.DELETE)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Label> iterate() {
        return new GhPagination<Label>(
            GhIssueLabels.this.entry,
            new GhPagination.Mapping<Label>() {
                @Override
                public Label map(final JsonObject object) {
                    return new Label.Simple(
                        object.getString("name"),
                        object.getString("color")
                    );
                }
            }
        );
    }

}
