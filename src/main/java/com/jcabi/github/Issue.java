/**
 * Copyright (c) 2012-2013, JCabi.com
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github get.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Issue extends Comparable<Issue> {

    /**
     * Repository we're in.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get its number.
     * @return Issue number
     */
    int number();

    /**
     * Get comments by number.
     * @return Comments
     */
    @NotNull(message = "comments are never NULL")
    Comments comments();

    /**
     * Get all get labels.
     * @return Labels
     */
    @NotNull(message = "labels are never NULL")
    Labels labels();

    /**
     * Describe it in a JSON object.
     * @return JSON object
     */
    @NotNull(message = "JSON is never NULL")
    JsonObject json();

    /**
     * Patch using this JSON object.
     * @param json JSON object
     */
    void patch(@NotNull(message = "JSON is never NULL") JsonObject json);

    /**
     * Issue manipulation toolkit.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "issue")
    final class Tool {
        /**
         * Encapsulated issue.
         */
        private final transient Issue issue;
        /**
         * Public ctor.
         * @param iss Issue
         */
        public Tool(final Issue iss) {
            this.issue = iss;
        }
        /**
         * Is it open?
         * @return TRUE if it's open
         */
        public boolean isOpen() {
            // @checkstyle MultipleStringLiterals (1 line)
            return "open".equals(this.state());
        }
        /**
         * Open it (make sure it's open).
         */
        public void open() {
            this.state("open");
        }
        /**
         * Close it (make sure it's closed).
         */
        public void close() {
            this.state("closed");
        }
        /**
         * Get its state.
         * @return State of issue
         */
        public String state() {
            // @checkstyle MultipleStringLiterals (1 line)
            final String state = this.issue.json().getString("state");
            if (state == null) {
                throw new IllegalStateException(
                    String.format(
                        "state is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return state;
        }
        /**
         * Change its state.
         * @param state State of issue
         */
        public void state(final String state) {
            this.issue.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }
        /**
         * Get its body.
         * @return Body of issue
         */
        public String title() {
            // @checkstyle MultipleStringLiterals (1 line)
            final String title = this.issue.json().getString("title");
            if (title == null) {
                throw new IllegalStateException(
                    String.format(
                        "title is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return title;
        }
        /**
         * Change its state.
         * @param text Text of issue
         */
        public void title(final String text) {
            this.issue.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }
        /**
         * Get its title.
         * @return Title of issue
         */
        public String body() {
            // @checkstyle MultipleStringLiterals (1 line)
            final String body = this.issue.json().getString("body");
            if (body == null) {
                throw new IllegalStateException(
                    String.format(
                        "body is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return body;
        }
        /**
         * Change its body.
         * @param text Body of issue
         */
        public void body(final String text) {
            this.issue.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }
        /**
         * Assign this issue to another user.
         * @param login Login of the user to assign to
         */
        public void assign(final String login) {
            this.issue.patch(
                Json.createObjectBuilder().add("assignee", login).build()
            );
        }
        /**
         * Get its URL.
         * @return URL of issue
         */
        public URL url() {
            final String url = this.issue.json().getString("url");
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "url is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            try {
                return new URL(url);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * Get its HTML URL.
         * @return URL of issue
         */
        public URL htmlUrl() {
            final String url = this.issue.json().getString("html_url");
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "html_url is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            try {
                return new URL(url);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * When this issue was created.
         * @return Date of creation
         */
        public Date createdAt() {
            final String date = this.issue.json().getString("created_at");
            if (date == null) {
                throw new IllegalStateException(
                    String.format(
                        "created_at is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return new Time(date).date();
        }
    }

}
