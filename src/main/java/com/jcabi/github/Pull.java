/**
 * Copyright (c) 2013-2018, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github pull request.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 * @see <a href="http://developer.github.com/v3/pulls/">Pull Request API</a>
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
     * @see <a href="http://developer.github.com/v3/pulls/#list-commits-on-a-pull-request">List Commits on a Pull Request</a>
     */
    Iterable<Commit> commits() throws IOException;

    /**
     * List all files of the pull request.
     * @return Files
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/pulls/#list-pull-requests-files">List Pull Request Files</a>
     */
    Iterable<JsonObject> files() throws IOException;

    /**
     * Merge it.
     * @param msg Commit message
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/pulls/#merge-a-pull-request-merge-buttontrade">Merge a Pull Request</a>
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
        String sha) throws IOException;

    /**
     * Get Pull Comments.
     * @return Comments.
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/pulls/#link-relations">Link Relations - Review Comments</a>
     */
    PullComments comments() throws IOException;

    /**
     * Smart pull request with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "pull", "jsn" })
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
            return new URL(this.jsn.text("url"));
        }
        /**
         * Get its HTML URL.
         * @return URL of pull request
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            return new URL(this.jsn.text("html_url"));
        }
        /**
         * When this pull request was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(
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
                return new Github.Time(
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
                return new Github.Time(
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
                return new Github.Time(
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
        /**
         * Is it a draft pull request?
         * @return TRUE if it's a draft pull request
         * @throws IOException If there is any I/O problem
         */
        public boolean isDraft() throws IOException {
            return this.jsn.json().getBoolean("draft");
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
            final String sha)
            throws IOException {
            return this.pull.merge(msg, sha);
        }

        @Override
        public PullComments comments() throws IOException {
            return this.pull.comments();
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
