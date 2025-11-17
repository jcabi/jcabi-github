/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * GitHub markdown.
 *
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtMarkdown implements Markdown {

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtMarkdown(final GitHub github, final Request req) {
        this.ghub = github;
        this.request = req.uri().path("markdown").back().method(Request.POST);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String render(
        final JsonObject json)
        throws IOException {
        final StringWriter output = new StringWriter();
        Json.createWriter(output).writeObject(json);
        return this.request
            .body()
            .set(output.toString())
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertHeader(
                HttpHeaders.CONTENT_TYPE,
                (Matcher) Matchers.everyItem(
                    Matchers.startsWith(MediaType.TEXT_HTML)
                )
            )
            .body();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String raw(
        final String text)
        throws IOException {
        return this.request
            .body()
            .set(text)
            .back()
            .uri().path("raw").back()
            .reset(HttpHeaders.CONTENT_TYPE)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertHeader(
                HttpHeaders.CONTENT_TYPE,
                (Matcher) Matchers.everyItem(
                    Matchers.startsWith(MediaType.TEXT_HTML)
                )
            )
            .body();
    }

}
