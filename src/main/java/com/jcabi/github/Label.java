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
import java.io.StringReader;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * GitHub label.
 * @see <a href="https://developer.github.com/v3/issues/labels/">Labels API</a>
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Label extends Comparable<Label>, JsonReadable, JsonPatchable {

    /**
     * The repo we're in.
     * @return Issue
     * @since 0.6
     */
    Repo repo();

    /**
     * Name of it.
     * @return Name
     */
    String name();

    /**
     * Smart Label with extra features.
     * @since 0.1
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "label", "jsn" })
    final class Smart implements Label {
        /**
         * Encapsulated label.
         */
        private final transient Label label;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param lbl Label
         */
        public Smart(final Label lbl) {
            this.label = lbl;
            this.jsn = new SmartJson(lbl);
        }

        /**
         * Get its color.
         * @return Color of it
         * @throws IOException If there is any I/O problem
         */
        public String color() throws IOException {
            return this.jsn.text("color");
        }

        /**
         * Set its color.
         * @param color Color to set
         * @throws IOException If there is any I/O problem
         */
        public void color(final String color) throws IOException {
            this.label.patch(
                Json.createObjectBuilder().add("color", color).build()
            );
        }

        @Override
        public Repo repo() {
            return this.label.repo();
        }

        @Override
        public String name() {
            return this.label.name();
        }

        @Override
        public int compareTo(final Label lbl) {
            return this.label.compareTo(lbl);
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.label.patch(json);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.label.json();
        }
    }

    /**
     * Unmodified Label with extra features.
     * @since 0.1
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "repo", "obj" })
    final class Unmodified implements Label {
        /**
         * Encapsulated Repo.
         */
        private final transient Repo repo;

        /**
         * Encapsulated String.
         */
        private final transient String obj;

        /**
         * Public ctor.
         * @param rep Repo
         * @param object String
         */
        public Unmodified(
            final Repo rep, final String object
        ) {
            this.repo = rep;
            this.obj = object;
        }

        @Override
        public Repo repo() {
            return this.repo;
        }

        @Override
        public String name() {
            return this.json().getString("name");
        }

        @Override
        public int compareTo(final Label label) {
            return new CompareToBuilder()
                .append(this.repo().coordinates(), label.repo().coordinates())
                .append(this.obj, label.name())
                .build();
        }

        @Override
        public void patch(final JsonObject json) {
            throw new UnsupportedOperationException("#patch()");
        }

        @Override
        public JsonObject json() {
            return Json.createReader(new StringReader(this.obj)).readObject();
        }
    }
}
