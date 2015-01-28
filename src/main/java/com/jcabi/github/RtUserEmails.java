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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github user's emails.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtUserEmails implements UserEmails {

    /**
     * RESTful API request for the emails.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req RESTful API entry point
     */
    RtUserEmails(@NotNull(message = "req can't be NULL") final Request req) {
        this.request = req.header("Accept", "application/vnd.github.v3")
            .uri().path("/user/emails").back();
    }

    @Override
    @NotNull(message = "iterable is never NULL")
    public Iterable<String> iterate() throws IOException {
        final List<JsonObject> array = this.request.method(Request.GET)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonObject.class);
        final Collection<String> emails = new ArrayList<String>(array.size());
        for (final JsonObject obj : array) {
            // @checkstyle MultipleStringLiterals (1 line)
            emails.add(obj.getString("email"));
        }
        return emails;
    }

    @Override
    @NotNull(message = "iterable is never NULL")
    public Iterable<String> add(@NotNull(message = "emails is never NULL")
        final Iterable<String> emails) throws IOException {
        final JsonArrayBuilder json = Json.createArrayBuilder();
        for (final String email : emails) {
            json.add(email);
        }
        final List<JsonObject> array = this.request.method(Request.POST)
            .body().set(json.build()).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonObject.class);
        final Collection<String> result = new ArrayList<String>(array.size());
        for (final JsonObject obj : array) {
            result.add(obj.getString("email"));
        }
        return result;
    }

    @Override
    public void remove(@NotNull(message = "emails is never NULL")
        final Iterable<String> emails) throws IOException {
        final JsonArrayBuilder json = Json.createArrayBuilder();
        for (final String email : emails) {
            json.add(email);
        }
        this.request.method(Request.DELETE)
            .body().set(json.build()).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

}
