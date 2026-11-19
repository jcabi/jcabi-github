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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub commit comment.
 *
 * <p>RepoComment implements {@link JsonReadable}, that's how you can
 * get its full details in JSON format. However, it's better to use
 * a supplementary "smart" decorator, which automates most of these
 * operations:</p>
 *
 * <pre> RepoComment.Smart comment = new RepoComment.Smart(
 *     repo.comments().get(1L)
 * );
 * if (comment.hasPath()) {
 *     System.out.println(comment.path() + ": " + comment.body());
 * }</pre>
 *
 * @see <a href="https://docs.github.com/en/rest/commits/comments">Commit Comments API</a>
 * @since 2.0
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface RepoComment extends JsonReadable, JsonPatchable,
    Comparable<RepoComment> {

    /**
     * The repository we're in.
     *
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     *
     * @return Comment number
     */
    long number();

    /**
     * Delete the comment.
     *
     * @throws IOException If there is any I/O problem
     * @see <a href="https://docs.github.com/en/rest/commits/comments#delete-a-commit-comment">Delete a commit comment</a>
     */
    void remove() throws IOException;

    /**
     * Smart RepoComment with extra features.
     *
     * @since 2.0
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "comment", "jsn" })
    final class Smart implements RepoComment {

        /**
         * Body field's name in JSON.
         */
        private static final String BODY = "body";

        /**
         * Path field's name in JSON.
         */
        private static final String PATH = "path";

        /**
         * Position field's name in JSON.
         */
        private static final String POSITION = "position";

        /**
         * Encapsulated commit comment.
         */
        private final transient RepoComment comment;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         *
         * @param cmt Commit comment
         */
        public Smart(final RepoComment cmt) {
            this.comment = cmt;
            this.jsn = new SmartJson(cmt);
        }

        /**
         * Get its author.
         *
         * @return Author of the comment
         * @throws IOException If there is any I/O problem
         */
        public User author() throws IOException {
            return this.comment.repo().github().users().get(
                this.comment.json().getJsonObject("user").getString("login")
            );
        }

        /**
         * Get its body.
         *
         * @return Body of the comment
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return this.jsn.text(RepoComment.Smart.BODY);
        }

        /**
         * Change its body.
         *
         * @param text Body of the comment
         * @throws IOException If there is any I/O problem
         */
        public void body(final String text) throws IOException {
            this.comment.patch(
                Json.createObjectBuilder()
                    .add(RepoComment.Smart.BODY, text)
                    .build()
            );
        }

        /**
         * Get SHA of the commit it was made on.
         *
         * @return SHA of the commit
         * @throws IOException If there is any I/O problem
         */
        public String commitId() throws IOException {
            return this.jsn.text("commit_id");
        }

        /**
         * Is it an in-line comment made on a file of the commit?
         *
         * @return TRUE if the comment refers to a file
         * @throws IOException If there is any I/O problem
         */
        public boolean hasPath() throws IOException {
            return this.jsn.hasNotNull(RepoComment.Smart.PATH);
        }

        /**
         * Get relative path of the file it was made on.
         *
         * @return Path of the file
         * @throws IOException If there is any I/O problem
         * @see #hasPath()
         */
        public String path() throws IOException {
            return this.jsn.text(RepoComment.Smart.PATH);
        }

        /**
         * Does it refer to a line of the commit diff?
         *
         * @return TRUE if the comment has a position in the diff
         * @throws IOException If there is any I/O problem
         */
        public boolean hasPosition() throws IOException {
            return this.jsn.hasNotNull(RepoComment.Smart.POSITION);
        }

        /**
         * Get line index in the commit diff it was made on.
         *
         * @return Position in the diff
         * @throws IOException If there is any I/O problem
         * @see #hasPosition()
         */
        public int position() throws IOException {
            return this.jsn.number(RepoComment.Smart.POSITION);
        }

        /**
         * Get its URL.
         *
         * @return URL of the comment
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
         *
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Instant createdAt() throws IOException {
            return new GitHub.Time(this.jsn.text("created_at")).date();
        }

        /**
         * When this comment was updated last time.
         *
         * @return Date of update
         * @throws IOException If there is any I/O problem
         */
        public Instant updatedAt() throws IOException {
            return new GitHub.Time(this.jsn.text("updated_at")).date();
        }

        @Override
        public Repo repo() {
            return this.comment.repo();
        }

        @Override
        public long number() {
            return this.comment.number();
        }

        @Override
        public void remove() throws IOException {
            this.comment.remove();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.comment.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.comment.patch(json);
        }

        @Override
        public int compareTo(final RepoComment other) {
            return this.comment.compareTo(other);
        }
    }
}
