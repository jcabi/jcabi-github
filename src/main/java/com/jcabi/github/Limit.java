/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub Rate Limit API, one resource limit.
 * @since 0.6
 * @see <a href="https://developer.github.com/v3/rate_limit/">Rate Limit API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
public interface Limit extends JsonReadable {

    /**
     * GitHub we're in.
     * @return GitHub
     */
    GitHub github();

    /**
     * Smart limits with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "origin")
    final class Smart implements Limit {
        /**
         * Encapsulated limit.
         */
        private final transient Limit origin;
        /**
         * Public ctor.
         * @param limit Limit
         */
        public Smart(
            final Limit limit
        ) {
            this.origin = limit;
        }
        /**
         * Limit of number of requests.
         * @return Number of requests you can make in total
         * @throws IOException If it fails
         */
        public int limit() throws IOException {
            return new SmartJson(this.origin).number("limit");
        }
        /**
         * Remaining number of requests.
         * @return Number of requests you can still make
         * @throws IOException If it fails
         */
        public int remaining() throws IOException {
            return new SmartJson(this.origin).number("remaining");
        }
        /**
         * When will the limit be reset.
         * @return Date when this will happen
         * @throws IOException If it fails
         */
        public Date reset() throws IOException {
            return new Date(
                TimeUnit.MILLISECONDS.convert(
                    (long) new SmartJson(this.origin).number("reset"),
                    TimeUnit.SECONDS
                )
            );
        }

        @Override
        public JsonObject json() throws IOException {
            return this.origin.json();
        }

        @Override
        public GitHub github() {
            return this.origin.github();
        }
    }

    /**
     * Throttled Limit.
     * @since 0.6
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "origin", "jsn" })
    final class Throttled implements Limit {
        /**
         * Original.
         */
        private final transient Limit origin;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Maximum allowed, instead of default 5000.
         */
        private final transient int max;
        /**
         * Public ctor.
         * @param limit Original limit
         * @param allowed Maximum allowed
         */
        public Throttled(
            final Limit limit,
            final int allowed
        ) {
            this.origin = limit;
            this.max = allowed;
            this.jsn = new SmartJson(limit);
        }

        @Override
        public JsonObject json() throws IOException {
            final int limit = new SmartJson(this.origin).number("limit");
            final int remaining = this.max - (
                limit - new SmartJson(this.origin).number("remaining")
                );
            return Json.createObjectBuilder()
                .add("limit", limit)
                .add("remaining", remaining)
                .add("reset", new SmartJson(this.origin).number("reset"))
                .build();
        }

        @Override
        public GitHub github() {
            return this.origin.github();
        }
    }

}
