/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi;

import com.jcabi.github.GitHub;
import com.jcabi.github.RtGitHub;
import com.jcabi.http.response.JsonResponse;
import java.util.List;
import javax.json.JsonObject;

/**
 * Search repositories.
 *
 * @since 0.8
 */
public final class Main {

    /**
     * Main entry point.
     * @param args Command line arguments
     */
    public static void main(final String[] args) throws Exception {
        final GitHub github = new RtGitHub();
        final JsonResponse resp = github.entry()
            .uri().path("/search/repositories")
            .queryParam("q", "java").back()
            .fetch()
            .as(JsonResponse.class);
        final List<JsonObject> items = resp.json().readObject()
            .getJsonArray("items")
            .getValuesAs(JsonObject.class);
        for (final JsonObject item : items) {
            System.out.println(
                String.format(
                    "repository found: %s",
                    item.get("full_name").toString()
                )
            );
        }
    }

}
