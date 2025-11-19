/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub Rate Limit API.
 * @since 0.6
 * @see <a href="https://developer.github.com/v3/rate_limit/">Rate Limit API</a>
 */
@Immutable
public interface Limits {

    /**
     * Resource name.
     */
    String CORE = "core";

    /**
     * Resource name.
     */
    String SEARCH = "search";

    /**
     * GitHub we're in.
     * @return GitHub
     */
    GitHub github();

    /**
     * Get limit for the given resource.
     * @param resource Name of resource
     * @return Limit
     */
    Limit get(String resource);

    /**
     * Throttled Limits.
     * @since 0.6
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "origin")
    final class Throttled implements Limits {
        /**
         * Original.
         */
        private final transient Limits origin;
        /**
         * Maximum allowed, instead of default 5000.
         */
        private final transient int max;
        /**
         * Public ctor.
         * @param limits Original limits
         * @param allowed Maximum allowed
         */
        public Throttled(
            final Limits limits,
            final int allowed
        ) {
            this.origin = limits;
            this.max = allowed;
        }

        @Override
        public GitHub github() {
            return this.origin.github();
        }

        @Override
        public Limit get(
            final String resource
        ) {
            return new Limit.Throttled(this.origin.get(resource), this.max);
        }
    }

}
