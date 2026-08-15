/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub Rate Limit API, one resource limit.
 * @see <a href="https://developer.github.com/v3/rate_limit/">Rate Limit API</a>
 * @since 0.6
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
     * @since 0.6
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
        public Smart(final Limit limit) {
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
        public Instant reset() throws IOException {
            return Instant.ofEpochMilli(
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
    @EqualsAndHashCode(of = "origin")
    final class Throttled implements Limit {

        /**
         * Original.
         */
        private final transient Limit origin;

        /**
         * Maximum allowed, instead of default 5000.
         */
        private final transient int max;

        /**
         * Public ctor.
         * @param limit Original limit
         * @param allowed Maximum allowed
         */
        public Throttled(final Limit limit, final int allowed) {
            this.origin = limit;
            this.max = allowed;
        }

        @Override
        public JsonObject json() throws IOException {
            final int limit = new SmartJson(this.origin).number("limit");
            return Json.createObjectBuilder()
                .add("limit", limit).add(
                    "remaining",
                    this.max - (
                        limit - new SmartJson(this.origin).number("remaining")
                    )
                )
                .add("reset", new SmartJson(this.origin).number("reset"))
                .build();
        }

        @Override
        public GitHub github() {
            return this.origin.github();
        }
    }
}
