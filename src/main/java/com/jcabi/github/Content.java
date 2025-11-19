/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub content.
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/contents/">Contents API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Content extends Comparable<Content>,
    JsonReadable, JsonPatchable {

    /**
     * Repository we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its path name.
     * @return The path name
     */
    String path();

    /**
     * Get the raw contents.
     * @throws IOException If an IO error occurs
     * @return Input stream of the raw content
     */
    InputStream raw() throws IOException;

    /**
     * Smart Content with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "content", "jsn" })
    final class Smart implements Content {
        /**
         * Encapsulated content.
         */
        private final transient Content content;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param cont Content
         */
        public Smart(
            final Content cont) {
            this.content = cont;
            this.jsn = new SmartJson(cont);
        }

        /**
         * Get its name.
         * @return Name of content
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get its type.
         * @return Type of content
         * @throws IOException If there is any I/O problem
         */
        public String type() throws IOException {
            return this.jsn.text("type");
        }

        /**
         * Get its size.
         * @return Size content
         * @throws IOException If it fails
         */
        public int size() throws IOException {
            return this.jsn.number("size");
        }

        /**
         * Get its sha hash.
         * @return Sha hash of content
         * @throws IOException If there is any I/O problem
         */
        public String sha() throws IOException {
            return this.jsn.text("sha");
        }

        /**
         * Get its URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            try {
                return new URI(this.jsn.text("url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its HTML URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            try {
                return new URI(this.jsn.text("html_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its GIT URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        public URL gitUrl() throws IOException {
            try {
                return new URI(this.jsn.text("git_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its encoded content.
         * @return Base64 encoded content
         * @throws IOException If there is any I/O problem
         */
        public String content() throws IOException {
            return this.jsn.text("content");
        }

        /**
         * Get its decoded content.
         * @return Decoded content
         * @throws IOException If there is any I/O problem
         */
        public byte[] decoded() throws IOException {
            return DatatypeConverter.parseBase64Binary(this.content());
        }

        @Override
        public int compareTo(final Content cont) {
            return this.content.compareTo(cont);
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.content.patch(json);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.content.json();
        }

        @Override
        public Repo repo() {
            return this.content.repo();
        }

        @Override
        public String path() {
            return this.content.path();
        }

        @Override
        public InputStream raw() throws IOException {
            return this.content.raw();
        }
    }
}
