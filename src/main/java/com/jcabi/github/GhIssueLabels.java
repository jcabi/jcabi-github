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
import com.rexsl.test.JsonResponse;
import com.rexsl.test.Request;
import com.rexsl.test.RestResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github get labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = "entry")
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
    public void add(final Iterable<Label> labels) throws IOException {
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
    public void remove(final String name) throws IOException {
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
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Iterable<Label> iterate() throws IOException {
        final JsonArray array = this.entry.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray();
        final Collection<Label> labels = new ArrayList<Label>(array.size());
        for (final JsonValue item : array) {
            final JsonObject obj = JsonObject.class.cast(item);
            labels.add(
                new Label.Simple(
                    obj.getString("name"),
                    obj.getString("color")
                )
            );
        }
        return labels;
    }

}
