/**
 * Copyright (c) 2013-2025, jcabi.com
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Milestone.
 *
 * <p>Use a supplementary "smart" decorator to get other properties
 * from an milestone, for example:
 *
 * <pre> Milestone.Smart milestone = new Milestone.Smart(origin);
 * if (milestone.isOpen()) {
 *   milestone.close();
 * }
 * </pre>
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="https://developer.github.com/v3/issues/milestones/">Milestones API</a>
 * @since 0.7
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Milestone extends Comparable<Milestone>,
    JsonReadable, JsonPatchable {

    /**
     * Milestone state.
     */
    String OPEN_STATE = "open";

    /**
     * Milestone state.
     */
    String CLOSED_STATE = "closed";

    /**
     * Repository we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     * @return Milestone number
     */
    int number();

    /**
     * Smart Milestone with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "milestone", "jsn" })
    final class Smart implements Milestone {

        /**
         * Name of mailestone state attribute.
         */
        private static final String STATE = "state";

        /**
         * Name of mailestone description attribute.
         */
        private static final String DESCRIPTION = "description";

        /**
         * Name of mailestone title attribute.
         */
        private static final String TITLE = "title";

        /**
         * Name of mailestone due_on attribute.
         */
        private static final String DUE_ON = "due_on";

        /**
         * Encapsulated milestone.
         */
        private final transient Milestone milestone;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param mls Issue
         */
        public Smart(
            final Milestone mls
        ) {
            this.milestone = mls;
            this.jsn = new SmartJson(mls);
        }

        /**
         * Get its creator.
         * @return Creator of milestone (who submitted it)
         * @throws java.io.IOException If there is any I/O problem
         */
        public User creator() throws IOException {
            return this.milestone.repo().github().users().get(
                this.jsn.value(
                    "creator", JsonObject.class
                ).getString("login")
            );
        }

        /**
         * Is it open?
         * @return TRUE if it's open
         * @throws IOException If there is any I/O problem
         */
        public boolean isOpen() throws IOException {
            return Milestone.OPEN_STATE.equals(this.state());
        }

        /**
         * Open it (make sure it's open).
         * @throws IOException If there is any I/O problem
         */
        public void open() throws IOException {
            this.state(Milestone.OPEN_STATE);
        }

        /**
         * Close it (make sure it's closed).
         * @throws IOException If there is any I/O problem
         */
        public void close() throws IOException {
            this.state(Milestone.CLOSED_STATE);
        }

        /**
         * Get its state.
         * @return State of milestone
         * @throws IOException If there is any I/O problem
         */
        public String state() throws IOException {
            return this.jsn.text(STATE);
        }

        /**
         * Change its state.
         * @param state State of milestone
         * @throws IOException If there is any I/O problem
         */
        public void state(
            final String state
        ) throws IOException {
            this.milestone.patch(
                Json.createObjectBuilder().add(STATE, state).build()
            );
        }

        /**
         * Get its title.
         * @return Title of milestone
         * @throws IOException If there is any I/O problem
         */
        public String title() throws IOException {
            return this.jsn.text(TITLE);
        }

        /**
         * Change its title.
         * @param title Title of milestone
         * @throws IOException If there is any I/O problem
         */
        public void title(
            final String title
        ) throws IOException {
            this.milestone.patch(
                Json.createObjectBuilder().add(TITLE, title).build()
            );
        }

        /**
         * Get its description.
         * @return Title of milestone
         * @throws IOException If there is any I/O problem
         */
        public String description() throws IOException {
            return this.jsn.text(DESCRIPTION);
        }

        /**
         * Change its description.
         * @param description Description of milestone
         * @throws IOException If there is any I/O problem
         */
        public void description(
            final String description
        ) throws IOException {
            this.milestone.patch(
                Json.createObjectBuilder()
                    .add(DESCRIPTION, description).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of milestone
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
         * When this milestone was created.
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
         * The milestone due date.
         * @return The milestone due date
         * @throws IOException If there is any I/O problem
         */
        public Date dueOn() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text(DUE_ON)
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * Change milestone due date.
         * @param dueon New milestone due date
         * @throws IOException If there is any I/O problem
         */
        public void dueOn(
            final Date dueon
        ) throws IOException {
            this.milestone.patch(
                Json.createObjectBuilder()
                    .add(DUE_ON, new Github.Time(dueon).toString()).build()
            );
        }

        /**
         * Get number of open issues.
         * @return Number of open issues
         * @throws IOException If there is any I/O problem
         */
        public int openIssues() throws IOException {
            return this.jsn.number("open_issues");
        }

        /**
         * Get number of closed issues.
         * @return Number of closed issues
         * @throws IOException If there is any I/O problem
         */
        public int closedIssues() throws IOException {
            return this.jsn.number("closed_issues");
        }

        @Override
        public Repo repo() {
            return this.milestone.repo();
        }

        @Override
        public int number() {
            return this.milestone.number();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.milestone.json();
        }

        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.milestone.patch(json);
        }

        @Override
        public int compareTo(
            final Milestone obj
        ) {
            return this.milestone.compareTo(obj);
        }

    }
}
