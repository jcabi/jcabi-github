/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Github Checks.
 *
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @since 1.5.0
 */
class RtChecks implements Checks {

    /**
     * Pull request.
     */
    private final transient Pull pull;

    /**
     * Http Request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     * @param prequest Pull request
     */
    RtChecks(final Request req, final Pull prequest) {
        this.pull = prequest;
        this.request = req;
    }

    /**
     * Get all checks.
     * JSON schema:
     * <p>{@code
     * {
     *   "total_count": 1,
     *   "check_runs": [
     *     {
     *       "id": 4,
     *       "status": "completed",
     *       ...
     *     }
     *   ]
     * }
     * }</p>
     * @return Checks.
     * @throws IOException If there is any I/O problem.
     */
    @Override
    public Collection<? extends Check> all() throws IOException {
        final Coordinates coords = this.pull.repo().coordinates();
        final RestResponse rest = this.request.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/commits")
            .path(this.pull.head().sha())
            .path("/check-runs")
            .back()
            .method(Request.GET).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        final JsonObject object = rest.as(JsonResponse.class)
            .json()
            .readObject();
        return Optional.ofNullable(object.getJsonArray("check_runs"))
            .map(
                obj -> obj.stream()
                    .map(RtChecks::check)
                    .collect(Collectors.toList())
            )
            .orElseGet(Collections::emptyList);
    }

    /**
     * Get check by id.
     * @param value Json value.
     * @return Check.
     */
    private static RtCheck check(final JsonValue value) {
        final JsonObject check = value.asJsonObject();
        return new RtCheck(
            RtChecks.getOrUndefined("status", check),
            RtChecks.getOrUndefined("conclusion", check)
        );
    }

    /**
     * Retrieves String value from JsonObject by key or return "undefined".
     * @param key Json key.
     * @param check Retrieve from
     * @return Json String value or "undefined".
     */
    private static String getOrUndefined(
        final String key,
        final JsonObject check
    ) {
        final String res;
        if (check.containsKey(key) && !check.isNull(key)) {
            res = check.getString(key);
        } else {
            res = Check.UNDEFINED_VALUE;
        }
        return res;
    }
}
