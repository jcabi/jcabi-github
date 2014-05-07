/**
 * Copyright (c) 2013-2014, jcabi.com
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github issue.
 *
 * <p>Use a supplementary "smart" decorator to get other properties
 * from an issue, for example:
 *
 * <pre> Issue.Smart issue = new Issue.Smart(origin);
 * if (issue.isOpen()) {
 *   issue.close();
 * }</pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/">Issues API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
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
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get its number.
     * @return Issue number
     */
    int number();

    /**
     * Get all comments of the issue.
     * @return Comments
     * @see <a href="http://developer.github.com/v3/issues/comments/">Issue Comments API</a>
     */
    @NotNull(message = "comments are never NULL")
    Comments comments();

    /**
     * Get all labels of the issue.
     * @return Labels
     * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    @NotNull(message = "labels are never NULL")
    IssueLabels labels();

    /**
     * Get all events of the issue.
     * @return Events
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/events/#list-events-for-an-issue">List Events for an Issue</a>
     */
    @NotNull(message = "iterable of events is never NULL")
    Iterable<Event> events() throws IOException;

    /**
     * Smart Issue with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "issue", "jsn" })
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
        public Smart(@NotNull(message = "iss can't be NULL") final Issue iss) {
            this.issue = iss;
            this.jsn = new SmartJson(iss);
        }
        /**
         * Get its author.
         * @return Author of issue (who submitted it)
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "user is never NULL")
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
        @NotNull(message = "state is never NULL")
        public String state() throws IOException {
            return this.jsn.text("state");
        }
        /**
         * Change its state.
         * @param state State of issue
         * @throws IOException If there is any I/O problem
         */
        public void state(
            @NotNull(message = "state can't be NULL") final String state
        ) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }
        /**
         * Get its body.
         * @return Body of issue
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "title is never NULL")
        public String title() throws IOException {
            return this.jsn.text("title");
        }
        /**
         * Change its state.
         * @param text Text of issue
         * @throws IOException If there is any I/O problem
         */
        public void title(
            @NotNull(message = "text can't be NULL") final String text
        ) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }
        /**
         * Get its title.
         * @return Title of issue
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "body is never NULL")
        public String body() throws IOException {
            return this.jsn.text("body");
        }
        /**
         * Change its body.
         * @param text Body of issue
         * @throws IOException If there is any I/O problem
         */
        public void body(
            @NotNull(message = "text can't be NULL") final String text
        ) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }

        /**
         * Get its assignee.
         * @return User Assignee of issue
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "user is never NULL")
        public User assignee() throws IOException {
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
        public void assign(
            @NotNull(message = "login can't be NULL") final String login
        ) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("assignee", login).build()
            );
        }
        /**
         * Get its URL.
         * @return URL of issue
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }
        /**
         * Get its HTML URL.
         * @return URL of issue
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public URL htmlUrl() throws IOException {
            return new URL(this.jsn.text("html_url"));
        }
        /**
         * When this issue was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "date is never NULL")
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
         * When this issue was updated.
         * @return Date of update
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "date is never NULL")
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
         * Is it a pull requests?
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
        @NotNull(message = "pull is never NULL")
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
         * @param type Type of event
         * @return Event found (runtime exception if it doesn't exist)
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "event is never NULL")
        public Event latestEvent(
            @NotNull(message = "type can't be NULL") final String type
        ) throws IOException {
            final Iterable<Event.Smart> events = new Smarts<Event.Smart>(
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
        @NotNull(message = "collection is never NULL")
        public IssueLabels roLabels() throws IOException {
            final Collection<JsonObject> array =
                this.jsn.value("labels", JsonArray.class)
                    .getValuesAs(JsonObject.class);
            final Collection<Label> labels = new ArrayList<Label>(array.size());
            for (final JsonObject obj : array) {
                labels.add(
                    new Label.Unmodified(
                        Issue.Smart.this.repo(),
                        obj.toString()
                    )
                );
            }
            // @checkstyle AnonInnerLength (1 line)
            return new IssueLabels() {
                @Override
                @NotNull(message = "issue is never NULL")
                public Issue issue() {
                    return Issue.Smart.this;
                }
                @Override
                public void add(
                    @NotNull(message = "iterable of label names can't be NULL")
                    final Iterable<String> labels) throws IOException {
                    throw new UnsupportedOperationException(
                        "The issue is read-only."
                    );
                }
                @Override
                public void replace(
                    @NotNull(message = "iterable of label names can't be NULL")
                    final Iterable<String> labels) throws IOException {
                    throw new UnsupportedOperationException(
                        "The issue is read-only."
                    );
                }
                @Override
                @NotNull(message = "Iterable of labels is never NULL")
                public Iterable<Label> iterate() {
                    return labels;
                }
                @Override
                public void remove(
                    @NotNull(message = "label name can't be NULL")
                    final String name) throws IOException {
                    throw new UnsupportedOperationException(
                        "This issue is read-only."
                    );
                }
                @Override
                public void clear() throws IOException {
                    throw new UnsupportedOperationException(
                        "This issue is read-only."
                    );
                }
            };
        }
        @Override
        @NotNull(message = "repository is never NULL")
        public Repo repo() {
            return this.issue.repo();
        }
        @Override
        public int number() {
            return this.issue.number();
        }
        @Override
        @NotNull(message = "comments is never NULL")
        public Comments comments() {
            return this.issue.comments();
        }
        @Override
        @NotNull(message = "labels is never NULL")
        public IssueLabels labels() {
            return this.issue.labels();
        }
        @Override
        @NotNull(message = "Iterable of events is never NULL")
        public Iterable<Event> events() throws IOException {
            return this.issue.events();
        }
        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.issue.json();
        }
        @Override
        public void patch(
            @NotNull(message = "json can't be NULL") final JsonObject json
        ) throws IOException {
            this.issue.patch(json);
        }
        @Override
        public int compareTo(
            @NotNull(message = "obj can't be NULL") final Issue obj
        ) {
            return this.issue.compareTo(obj);
        }
    }

}
