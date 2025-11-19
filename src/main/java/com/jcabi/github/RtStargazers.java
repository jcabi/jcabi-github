/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param entry Entry request.
     */
    RtStargazers(final Request entry) {
        this.request = entry.uri()
            .path("stargazers")
            .back();
    }

    @Override
    public Iterable<JsonValue> iterable() throws IOException {
        final Iterable<JsonValue> res;
        try (
            final JsonReader json = new JsonResponse(
                this.request.method(Request.GET).fetch()
            ).json()
        ) {
            res = json.readArray();
        }
        return res;
    }
}
