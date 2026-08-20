/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import jakarta.json.Json;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Response to return.
 * @since 0.4
 */
@Immutable
final class Hidden implements Response {

    /**
     * Original response.
     */
    private final transient Response response;

    /**
     * Ctor.
     * @param resp Response
     */
    Hidden(final Response resp) {
        this.response = resp;
    }

    @Override
    public Request back() {
        return new SearchRequest(this.response.back());
    }

    @Override
    public int status() {
        return this.response.status();
    }

    @Override
    public String reason() {
        return this.response.reason();
    }

    @Override
    public Map<String, List<String>> headers() {
        return this.response.headers();
    }

    @Override
    public String body() {
        return Json.createReader(new StringReader(this.response.body()))
            .readObject().getJsonArray("items").toString();
    }

    @Override
    public byte[] binary() {
        return this.body().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T extends Response> T as(final Class<T> type) {
        try {
            return type.getDeclaredConstructor(Response.class)
                .newInstance(this);
        } catch (final InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
