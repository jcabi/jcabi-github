/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github JSON item.
 *
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtJson {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req Request
     */
    RtJson(final Request req) {
        this.request = req;
    }

    /**
     * Fetch JSON object.
     * @return JSON object
     * @throws IOException If fails
     */
    public JsonObject fetch() throws IOException {
        return this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject();
    }

    /**
     * Patch it.
     * @param json JSON to use for patching
     * @throws IOException If fails
     */
    public void patch(
        final JsonObject json
    ) throws IOException {
        final StringWriter post = new StringWriter();
        Json.createWriter(post).writeObject(json);
        this.request.body().set(post.toString()).back()
            .method(Request.PATCH)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

}
