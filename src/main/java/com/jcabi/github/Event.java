/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
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
 * GitHub event.
 * @since 0.4
 * @see <a href="https://developer.github.com/v3/issues/events/">Issue Events API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Event extends Comparable<Event>, JsonReadable {

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String CLOSED = "closed";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String REOPENED = "reopened";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String SUBSCRIBED = "subscribed";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MERGED = "merged";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String REFERENCED = "referenced";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MENTIONED = "mentioned";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String ASSIGNED = "assigned";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNASSIGNED = "unassigned";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String LABELED = "labeled";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNLABELED = "unlabeled";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MILESTONED = "milestoned";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String DEMILESTONED = "demilestoned";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String RENAMED = "renamed";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String LOCKED = "locked";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNLOCKED = "unlocked";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String HEAD_REF_DELETED = "head_ref_deleted";

    /**
     * Event type.
     * @see <a href="https://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String HEAD_REF_RESTORED = "head_ref_restored";

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
     * Smart event with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "event", "jsn" })
    final class Smart implements Event {
        /**
         * Encapsulated event.
         */
        private final transient Event event;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param evt Event
         */
        public Smart(final Event evt) {
            this.event = evt;
            this.jsn = new SmartJson(evt);
        }
        /**
         * Does it have an author?
         * @return TRUE if the author exists
         * @throws IOException If there is any I/O problem
         */
        public boolean hasAuthor() throws IOException {
            return !this.event.json().isNull("actor");
        }
        /**
         * Get its author.
         * @return Author of comment
         * @throws IOException If there is any I/O problem
         */
        public User author() throws IOException {
            return this.event.repo().github().users().get(
                this.event.json().getJsonObject("actor").getString("login")
            );
        }
        /**
         * Get its type.
         * @return State of issue
         * @throws IOException If there is any I/O problem
         */
        public String type() throws IOException {
            return this.jsn.text("event");
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
         * Label that was added or removed in this event (if any).
         * @return Label that was added or removed
         * @throws IOException If there is any I/O problem
         * @since 0.24
         */
        public Optional<Label> label() throws IOException {
            Optional<Label> lab = Optional.absent();
            final JsonObject lbl = this.jsn.json().getJsonObject("label");
            if (lbl != null) {
                lab = Optional.of(
                    this.event.repo()
                        .labels()
                        .get(lbl.getString("name"))
                );
            }
            return lab;
        }

        @Override
        public Repo repo() {
            return this.event.repo();
        }

        @Override
        public int number() {
            return this.event.number();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.event.json();
        }

        @Override
        public int compareTo(final Event obj) {
            return this.event.compareTo(obj);
        }
    }

}
