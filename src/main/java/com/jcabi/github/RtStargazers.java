/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.IOException;

/**
 * GitHub stargazers.
 * @since 1.7.1
 */
public final class RtStargazers implements Stargazers {

    /**
     * Entry request.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param req Entry request
     */
    RtStargazers(final Request req) {
        this.entry = req;
    }

    @Override
    public Iterable<JsonValue> iterable() throws IOException {
        final Iterable<JsonValue> res;
        try (
            JsonReader json = new JsonResponse(
                this.request().method(Request.GET).fetch()
            ).json()
        ) {
            res = json.readArray();
        }
        return res;
    }

    /**
     * RESTful request for stargazers.
     * @return Request
     */
    private Request request() {
        return this.entry.uri().path("stargazers").back();
    }
}
