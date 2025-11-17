/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Gist comment.
 *
 * <p>Gist Comment implements {@link JsonReadable}, that's how you can get
 * its full details in JSON format.
 * For example, to get its author's Github login
 * you get the entire JSON and then gets its element:
 *
 * <pre>String login = comment.json()
 *   .getJsonObject("user")
 *   .getString("login");</pre>
 *
 * <p>However, it's better to use a supplementary "smart" decorator, which
 * automates most of these operations:
 *
 * <pre>String login = new GistComment.Smart(comment).author().login();</pre>
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface GistComment
    extends Comparable<GistComment>, JsonReadable, JsonPatchable {
    /**
     * The gist it's in.
     * @return Owner of the comment
     */
    Gist gist();

    /**
     * Number.
     * @return Comment id
     */
    int number();

    /**
     * Delete the comment.
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/gists/comments/#delete-a-comment">Delete a Comment</a>
     */
    void remove() throws IOException;

    /**
     * Smart comment with additional features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "comment", "jsn" })
    final class Smart implements GistComment {
        /**
         * Encapsulated gist comment.
         */
        private final transient GistComment comment;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param cmt Comment
         */
        public Smart(final GistComment cmt) {
            this.comment = cmt;
            this.jsn = new SmartJson(cmt);
        }

        /**
         * Get its author.
         * @return Author of comment
         * @throws IOException If there is any I/O problem
         */
        public User author() throws IOException {
            return this.comment.gist().github().users().get(
                this.comment.json().getJsonObject("user").getString("login")
            );
        }

        /**
         * Get its body.
         * @return Body of comment
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return this.jsn.text("body");
        }

        /**
         * Change comment body.
         * @param text Body of comment
         * @throws IOException If there is any I/O problem
         */
        public void body(final String text) throws IOException {
            this.comment.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of comment
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
         * When this comment was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text("created_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IOException(ex);
            }
        }

        /**
         * When this comment was updated last time.
         * @return Date of update
         * @throws IOException If there is any I/O problem
         */
        public Date updatedAt() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text("updated_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public Gist gist() {
            return this.comment.gist();
        }

        @Override
        public int number() {
            return this.comment.number();
        }

        @Override
        public void remove() throws IOException {
            this.comment.remove();
        }

        @Override
        public int compareTo(final GistComment cmt) {
            return this.comment.compareTo(cmt);
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.comment.patch(json);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.comment.json();
        }
    }
}
