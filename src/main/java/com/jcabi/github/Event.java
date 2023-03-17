/**
 * Copyright (c) 2013-2023, jcabi.com
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

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github event.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 * @see <a href="http://developer.github.com/v3/issues/events/">Issue Events API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Event extends Comparable<Event>, JsonReadable {

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String CLOSED = "closed";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String REOPENED = "reopened";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String SUBSCRIBED = "subscribed";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MERGED = "merged";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String REFERENCED = "referenced";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MENTIONED = "mentioned";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String ASSIGNED = "assigned";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNASSIGNED = "unassigned";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String LABELED = "labeled";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNLABELED = "unlabeled";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String MILESTONED = "milestoned";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String DEMILESTONED = "demilestoned";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String RENAMED = "renamed";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String LOCKED = "locked";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String UNLOCKED = "unlocked";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
     */
    String HEAD_REF_DELETED = "head_ref_deleted";

    /**
     * Event type.
     * @see <a href="http://developer.github.com/v3/issues/events/">Event Types</a>
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
            return new URL(this.jsn.text("url"));
        }
        /**
         * When this issue was created.
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
