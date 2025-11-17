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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub pull request.
 *
 * @since 0.3
 * @see <a href="https://developer.github.com/v3/pulls/">Pull Request API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 *
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Pull extends Comparable<Pull>, JsonReadable, JsonPatchable {

    /**
     * Repo we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     * @return Pull request number
     */
    int number();

    /**
     * Get its base ref.
     * @return Base ref
     * @throws IOException If there is any I/O problem
     */
    PullRef base() throws IOException;

    /**
     * Get its head ref.
     * @return Head ref
     * @throws IOException If there is any I/O problem
     */
    PullRef head() throws IOException;

    /**
     * Get all commits of the pull request.
     * @return Commits
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/#list-commits-on-a-pull-request">List Commits on a Pull Request</a>
     */
    Iterable<Commit> commits() throws IOException;

    /**
     * List all files of the pull request.
     * @return Files
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/#list-pull-requests-files">List Pull Request Files</a>
     */
    Iterable<JsonObject> files() throws IOException;

    /**
     * Merge it.
     * @param msg Commit message
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/#merge-a-pull-request-merge-buttontrade">Merge a Pull Request</a>
     */
    void merge(String msg)
        throws IOException;

    /**
     * Merge it.
     * @param msg Commit message
     * @param sha Optional SHA hash for head comparison
     * @return State of the Merge
     * @throws IOException IOException If there is any I/O problem
     */
    MergeState merge(String msg,
        String sha
    ) throws IOException;

    /**
     * Get Pull Comments.
     * @return Comments.
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/#link-relations">Link Relations - Review Comments</a>
     */
    PullComments comments() throws IOException;

    /**
     * Get Pull Checks.
     * @return Checks.
     * @throws IOException If there is any I/O problem.
     * @see <a href="https://developer.github.com/v3/checks/runs/">Checks API</a>
     * @since 1.6.0
     */
    Checks checks() throws IOException;

    /**
     * Smart pull request with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"pull", "jsn"})
    final class Smart implements Pull {
        /**
         * Encapsulated pull request.
         */
        private final transient Pull pull;
        /**
         * SmartJson object for convenient JSON parsing.
         */

        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param pll Pull request
         */
        public Smart(
            final Pull pll
        ) {
            this.pull = pll;
            this.jsn = new SmartJson(pll);
        }

        /**
         * Is it open?
         * @return TRUE if it's open
         * @throws IOException If there is any I/O problem
         */
        public boolean isOpen() throws IOException {
            return Issue.OPEN_STATE.equals(this.state());
        }

        /**
         * Get its state.
         * @return State of pull request
         * @throws IOException If there is any I/O problem
         */
        public String state() throws IOException {
            return this.jsn.text("state");
        }

        /**
         * Change its state.
         * @param state State of pull request
         * @throws IOException If there is any I/O problem
         */
        public void state(
            final String state
        ) throws IOException {
            this.pull.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }

        /**
         * Get its title.
         * @return Title of pull request
         * @throws IOException If there is any I/O problem
         */
        public String title() throws IOException {
            return this.jsn.text("title");
        }

        /**
         * Change its title.
         * @param text Title of pull request
         * @throws IOException If there is any I/O problem
         */
        public void title(
            final String text
        ) throws IOException {
            this.pull.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }

        /**
         * Get its body.
         * @return Body of pull request
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return this.jsn.text("body");
        }

        /**
         * Change its body.
         * @param text Body of pull request
         * @throws IOException If there is any I/O problem
         */
        public void body(
            final String text
        ) throws IOException {
            this.pull.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of pull request
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
         * @return URL of pull request
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
         * When this pull request was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new GitHub.Time(
                    this.jsn.text("created_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * When this pull request was updated.
         * @return Date of update
         * @throws IOException If there is any I/O problem
         */
        public Date updatedAt() throws IOException {
            try {
                return new GitHub.Time(
                    this.jsn.text("updated_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * When this pull request was closed.
         * @return Date of closing
         * @throws IOException If there is any I/O problem
         */
        public Date closedAt() throws IOException {
            try {
                return new GitHub.Time(
                    this.jsn.text("closed_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * When this pull request was merged.
         * @return Date of merging
         * @throws IOException If there is any I/O problem
         */
        public Date mergedAt() throws IOException {
            try {
                return new GitHub.Time(
                    this.jsn.text("merged_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * Get its author.
         * @return Author of pull request (who submitted it)
         * @throws IOException If there is any I/O problem
         */
        public User author() throws IOException {
            return this.pull.repo().github().users().get(
                this.jsn.value(
                    "user", JsonObject.class
                ).getString("login")
            );
        }

        /**
         * Get an issue where the pull request is submitted.
         * @return Issue
         */
        public Issue issue() {
            return this.pull.repo().issues().get(this.pull.number());
        }

        /**
         * Get comments count.
         * @return Count of comments
         * @throws IOException If there is any I/O problem
         * @since 0.8
         */
        public int commentsCount() throws IOException {
            return this.jsn.number("comments");
        }

        @Override
        public Repo repo() {
            return this.pull.repo();
        }

        @Override
        public int number() {
            return this.pull.number();
        }

        @Override
        public Iterable<Commit> commits() throws IOException {
            return this.pull.commits();
        }

        @Override
        public Iterable<JsonObject> files() throws IOException {
            return this.pull.files();
        }

        @Override
        public void merge(
            final String msg
        ) throws IOException {
            this.pull.merge(msg);
        }

        @Override
        public MergeState merge(
            final String msg,
            final String sha
        )
            throws IOException {
            return this.pull.merge(msg, sha);
        }

        @Override
        public PullComments comments() throws IOException {
            return this.pull.comments();
        }

        @Override
        public Checks checks() throws IOException {
            return this.pull.checks();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.pull.json();
        }

        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.pull.patch(json);
        }

        @Override
        public int compareTo(
            final Pull obj
        ) {
            return this.pull.compareTo(obj);
        }

        @Override
        public PullRef base() throws IOException {
            return this.pull.base();
        }

        @Override
        public PullRef head() throws IOException {
            return this.pull.head();
        }
    }
}
