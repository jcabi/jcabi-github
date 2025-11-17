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
 * GitHub Milestone.
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
         * @throws IOException If there is any I/O problem
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
            return this.jsn.text(Milestone.Smart.STATE);
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
                Json.createObjectBuilder().add(Milestone.Smart.STATE, state).build()
            );
        }

        /**
         * Get its title.
         * @return Title of milestone
         * @throws IOException If there is any I/O problem
         */
        public String title() throws IOException {
            return this.jsn.text(Milestone.Smart.TITLE);
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
                Json.createObjectBuilder().add(Milestone.Smart.TITLE, title).build()
            );
        }

        /**
         * Get its description.
         * @return Title of milestone
         * @throws IOException If there is any I/O problem
         */
        public String description() throws IOException {
            return this.jsn.text(Milestone.Smart.DESCRIPTION);
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
                    .add(Milestone.Smart.DESCRIPTION, description).build()
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
                return new GitHub.Time(
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
                return new GitHub.Time(
                    this.jsn.text(Milestone.Smart.DUE_ON)
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
                    .add(Milestone.Smart.DUE_ON, new GitHub.Time(dueon).toString()).build()
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
