/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub hook.
 *
 * @see <a href="https://developer.github.com/v3/repos/hooks/">Hooks API</a>
 * @since 0.8
 */
@Immutable
public interface Hook extends JsonReadable {

    /**
     * Repository we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     * @return Hook number
     */
    int number();

    /**
     * Smart Hook with extra features.
     * @since 0.8
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "hook", "jsn" })
    final class Smart implements Hook {
        /**
         * Encapsulated Hook.
         */
        private final transient Hook hook;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param hoo Hook
         */
        public Smart(final Hook hoo) {
            this.hook = hoo;
            this.jsn = new SmartJson(hoo);
        }
        /**
         * Get its name.
         * @return Name of hook
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        @Override
        public Repo repo() {
            return this.hook.repo();
        }

        @Override
        public int number() {
            return this.hook.number();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.hook.json();
        }
    }
}
