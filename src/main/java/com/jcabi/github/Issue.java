/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub issue.
 *
 * <p>Use a supplementary "smart" decorator to get other properties
 * from an issue, for example:
 *
 * <pre> Issue.Smart issue = new Issue.Smart(origin);
 * if (issue.isOpen()) {
 *   issue.close();
 * }</pre>
 *
 * @since 0.1
 * @see <a href="https://developer.github.com/v3/issues/">Issues API</a>
 * @todo #1462:30min Implement lock reason validation. According to
 *  documentation lock reason must belong to a specific value domain. This
 *  validation must be performed in lock method and tests must be added to
 *  ensure that the class is accepting the correct values and rejecting the
 *  wrong ones.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings
    (
        {
            "PMD.TooManyMethods", "PMD.GodClass", "PMD.ExcessivePublicCount"
        }
    )
public interface Issue extends Comparable<Issue>, JsonReadable, JsonPatchable {

    /**
     * Issue state.
     */
    String OPEN_STATE = "open";

    /**
     * Issue state.
     */
    String CLOSED_STATE = "closed";

    /**
     * Repository we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     * @return Issue number
     */
    int number();

    /**
     * Get all comments of the issue.
     * @return Comments
     * @see <a href="https://developer.github.com/v3/issues/comments/">Issue Comments API</a>
     */
    Comments comments();

    /**
     * Get all labels of the issue.
     * @return Labels
     * @see <a href="https://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    IssueLabels labels();

    /**
     * Get all events of the issue.
     * @return Events
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/events/#list-events-for-an-issue">List Events for an Issue</a>
     */
    Iterable<Event> events() throws IOException;

    /**
     * Does this issue exist in GitHub?
     * @return TRUE if this issue exists
     * @throws IOException If there is any I/O problem
     */
    boolean exists() throws IOException;

    /**
     * Adds the reaction to the issue.
     * @param reaction Reaction to be added.
     * @throws IOException If there is any I/O problem
     */
    void react(Reaction reaction) throws IOException;

    /**
     * List the reactions of the issue.
     * @return Issue reactions.
     */
    Iterable<Reaction> reactions();

    /**
     * Locks the issue.
     * @param reason Lock reason
     */
    void lock(String reason);

    /**
     * Unlocks the issue.
     */
    void unlock();

    /**
     * The issue conversation is locked?
     * @return If the issue is locked.
     */
    boolean isLocked();

    /**
     * Smart Issue with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"issue", "jsn"})
    final class Smart implements Issue {
        /**
         * Encapsulated issue.
         */
        private final transient Issue issue;
        /**
         * SmartJson object for convenient JSON parsing.
         */

        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param iss Issue
         */

        public Smart(final Issue iss) {
            this.issue = iss;
            this.jsn = new SmartJson(iss);
        }
        /**
         * Get its author.
         * @return Author of issue (who submitted it)
         * @throws IOException If there is any I/O problem
         */

        public User author() throws IOException {
            return this.issue.repo().github().users().get(
                this.jsn.value(
                    "user", JsonObject.class
                ).getString("login")
            );
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
         * Open it (make sure it's open).
         * @throws IOException If there is any I/O problem
         */

        public void open() throws IOException {
            this.state(Issue.OPEN_STATE);
        }
        /**
         * Close it (make sure it's closed).
         * @throws IOException If there is any I/O problem
         */

        public void close() throws IOException {
            this.state(Issue.CLOSED_STATE);
        }
        /**
         * Get its state.
         * @return State of issue
         * @throws IOException If there is any I/O problem
         */

        public String state() throws IOException {
            return this.jsn.text("state");
        }
        /**
         * Change its state.
         * @param state State of issue
         * @throws IOException If there is any I/O problem
         */

        public void state(final String state) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }
        /**
         * Get its title.
         * @return Title of issue
         * @throws IOException If there is any I/O problem
         */

        public String title() throws IOException {
            return this.jsn.text("title");
        }
        /**
         * Change its title.
         * @param text Title of issue
         * @throws IOException If there is any I/O problem
         */

        public void title(final String text) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }
        /**
         * Get its body.
         * @return Body of issue
         * @throws IOException If there is any I/O problem
         */

        public String body() throws IOException {
            return this.jsn.text("body");
        }
        /**
         * Change its body.
         * @param text Body of issue
         * @throws IOException If there is any I/O problem
         */

        public void body(final String text) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }
        /**
         * Has body?
         * @return TRUE if body exists
         * @throws IOException If there is any I/O problem
         * @since 0.22
         */

        public boolean hasBody() throws IOException {
            return this.jsn.hasNotNull("body");
        }
        /**
         * Has assignee?
         * @return TRUE if assignee exists
         * @throws IOException If there is any I/O problem
         */

        public boolean hasAssignee() throws IOException {
            return this.jsn.hasNotNull("assignee");
        }
        /**
         * Get its assignee.
         * @return User Assignee of issue
         * @throws IOException If there is any I/O problem
         */

        public User assignee() throws IOException {
            if (!this.hasAssignee()) {
                throw new IllegalArgumentException(
                    String.format(
                        "issue #%d doesn't have an assignee, use hasAssignee()",
                        this.number()
                    )
                );
            }
            return this.issue.repo().github().users().get(
                this.jsn.value(
                    "assignee", JsonObject.class
                ).getString("login")
            );
        }
        /**
         * Assign this issue to another user.
         * @param login Login of the user to assign to
         * @throws IOException If there is any I/O problem
         */

        public void assign(final String login) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("assignee", login).build()
            );
        }
        /**
         * Get its URL.
         * @return URL of issue
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
         * @return URL of issue
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
         * When this issue was created.
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
         * When this issue was closed.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         * @since 0.34
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
         * When this issue was updated.
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
         * Is it a pull request?
         * @return TRUE if it is a pull request
         * @throws IOException If there is any I/O problem
         */

        public boolean isPull() throws IOException {
            return this.json().containsKey("pull_request")
                && !this.jsn.value("pull_request", JsonObject.class)
                .isNull("html_url");
        }

        /**
         * Get pull request.
         * @return Pull request
         * @throws IOException If there is any I/O problem
         */
        public Pull pull() throws IOException {
            final String url = this.jsn.value(
                "pull_request", JsonObject.class
            ).getString("html_url");
            return this.issue.repo().pulls().get(
                Integer.parseInt(url.substring(url.lastIndexOf('/') + 1))
            );
        }
        /**
         * Get the latest event of a given type.
         * Throws {@link IllegalStateException} if the issue has no events of
         * the given type.
         * @param type Type of event
         * @return Latest event of the given type
         * @throws IOException If there is any I/O problem
         */

        public Event latestEvent(final String type) throws IOException {
            final Iterable<Event.Smart> events = new Smarts<>(
                this.issue.events()
            );
            Event found = null;
            for (final Event.Smart event : events) {
                if (event.type().equals(type) && (found == null
                    || found.number() < event.number())) {
                    found = event;
                }
            }
            if (found == null) {
                throw new IllegalStateException(
                    String.format(
                        "event of type '%s' not found in issue #%d",
                        type, this.issue.number()
                    )
                );
            }
            return found;
        }
        /**
         * Get read-only labels.
         * @return Collection of labels
         * @throws IOException If there is any I/O problem
         * @since 0.6.2
         */

        public IssueLabels roLabels() throws IOException {
            final Collection<JsonObject> array =
                this.jsn.value("labels", JsonArray.class)
                    .getValuesAs(JsonObject.class);
            final Collection<Label> labels = new ArrayList<>(array.size());
            for (final JsonObject obj : array) {
                labels.add(
                    new Label.Unmodified(
                        this.repo(),
                        obj.toString()
                    )
                );
            }
            // @checkstyle AnonInnerLength (1 line)
            return new IssueLabels() {
                @Override
                public Issue issue() {
                    return Issue.Smart.this;
                }

                @Override
                public void add(
                    final Iterable<String> labels) {
                    throw new UnsupportedOperationException(
                        "The issue is read-only."
                    );
                }

                @Override
                public void replace(
                    final Iterable<String> labels) {
                    throw new UnsupportedOperationException(
                        "The issue is read-only."
                    );
                }

                @Override
                public Iterable<Label> iterate() {
                    return labels;
                }

                @Override
                public void remove(
                    final String name) {
                    throw new UnsupportedOperationException(
                        "This issue is read-only."
                    );
                }

                @Override
                public void clear() {
                    throw new UnsupportedOperationException(
                        "This issue is read-only."
                    );
                }
            };
        }
        /**
         * Does issue have milestone?
         * @return True if has
         * @throws IOException If fails
         */

        public boolean hasMilestone() throws IOException {
            return this.jsn.hasNotNull("milestone");
        }
        /**
         * Get milestone for this issue.
         * @return Milestone
         * @throws IOException If fails
         */

        public Milestone milestone() throws IOException {
            return this.repo().milestones().get(
                this.jsn.value("milestone", JsonObject.class)
                    .getInt("number")
            );
        }
        /**
         * Add issueto milestone.
         * @param milestone Milestone
         * @throws IOException If fails
         */

        public void milestone(final Milestone milestone) throws IOException {
            this.patch(
                Json.createObjectBuilder().add(
                    "milestone", milestone.number()
                ).build()
            );
        }

        @Override
        public Repo repo() {
            return this.issue.repo();
        }

        @Override
        public int number() {
            return this.issue.number();
        }

        @Override
        public Comments comments() {
            return this.issue.comments();
        }

        @Override
        public IssueLabels labels() {
            return this.issue.labels();
        }

        @Override
        public Iterable<Event> events() throws IOException {
            return this.issue.events();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.issue.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.issue.patch(json);
        }

        @Override
        public int compareTo(final Issue obj) {
            return this.issue.compareTo(obj);
        }

        @Override
        public boolean exists() throws IOException {
            return new Existence(this.issue).check();
        }

        @Override
        public void react(final Reaction reaction) {
            throw new UnsupportedOperationException("react() not implemented");
        }

        @Override
        public Collection<Reaction> reactions() {
            throw new UnsupportedOperationException(
                "reactions() not implemented"
            );
        }

        @Override
        public void lock(final String reason) {
            throw new UnsupportedOperationException("lock not implemented");
        }

        @Override
        public void unlock() {
            throw new UnsupportedOperationException("unlock not implemented");
        }

        @Override
        public boolean isLocked() {
            throw new UnsupportedOperationException("isLocked not implemented");
        }
    }

}
