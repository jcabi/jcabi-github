/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonString;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

/**
 * GitHub Gitignore.
 * <p>Defines storage of .gitignore templates
 *
 * @since 0.8
 */
@Immutable
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtGitignores implements Gitignores {

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public CTOR.
     * @param github GitHub
     */
    RtGitignores(
        final GitHub github) {
        this.ghub = github;
        this.request = this.github().entry().uri()
            .path("/gitignore/templates").back();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public Iterable<String> iterate() throws IOException {
        final RestResponse response = this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        final List<JsonString> list = response.as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonString.class);
        final List<String> templates = new ArrayList<>(list.size());
        for (final JsonString value : list) {
            templates.add(value.getString());
        }
        return templates;
    }

    @Override
    public String template(
        final String name)
        throws IOException {
        return this.request.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.raw")
            .uri().path(name).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }
}
