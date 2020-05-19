/**
 * Copyright (c) 2013-2020, jcabi.com
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
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub commit status.
 * @author Marcin Cylke (marcin.cylke+github@gmail.com)
 * @version $Id$
 * @since 0.23
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Status extends JsonReadable {
    /**
     * Associated commit.
     * @return Commit
     */
    Commit commit();

    /**
     * Get its ID number.
     * @return ID number
     */
    int identifier();

    /**
     * Get its URL.
     * @return URL
     */
    String url();

    /**
     * States of Status API.
     * @author Marcin Cylke(marcin.cylke+github@gmail.com)
     * @version $Id$
     */
    enum State implements StringEnum {
        /**
         * Pending state.
         */
        PENDING("pending"),
        /**
         * Success state.
         */
        SUCCESS("success"),
        /**
         * Error state.
         */
        ERROR("error"),
        /**
         * Failure state.
         */
        FAILURE("failure");

        /**
         * Commit status state identifier string.
         */
        private final transient String state;

        /**
         * Private ctor.
         * @param stat Commit status state identifier string
         */
        State(
            final String stat
        ) {
            this.state = stat;
        }

        @Override
        public String identifier() {
            return this.state;
        }

        /**
         * Get enum value from identifier string.
         * @param ident Commit status state string
         * @return Corresponding State
         */
        public static State forValue(
            final String ident
        ) {
            return State.valueOf(ident.toUpperCase(Locale.ENGLISH));
        }
    }

    /**
     * Smart Status with extra features.
     * @author Chris Rebert (github@chrisrebert.com)
     * @version $Id$
     * @since 0.24
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "status", "jsn" })
    final class Smart implements Status {
        /**
         * Encapsulated status.
         */
        private final transient Status status;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param stat Status
         */
        public Smart(
            final Status stat
        ) {
            this.status = stat;
            this.jsn = new SmartJson(stat);
        }

        /**
         * Get state.
         * @return State as enum
         * @throws IOException If there is an I/O problem
         */
        public State state() throws IOException {
            return State.forValue(this.jsn.text("state"));
        }

        /**
         * Get URL.
         * @return URL as string.
         * @throws IOException If there is an I/O problem
         */
        public Optional<String> targetUrl() throws IOException {
            return Optional.fromNullable(
                this.json().getString("target_url", null)
            );
        }

        /**
         * Get description.
         * @return Description as string.
         * @throws IOException If there is an I/O problem
         */
        public String description() throws IOException {
            return this.json().getString("description", "");
        }

        /**
         * Get context.
         * @return Context as string
         * @throws IOException If there is an I/O problem
         */
        public String context() throws IOException {
            return this.jsn.text("context");
        }

        /**
         * When this commit status was created.
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
         * When this commit status was updated.
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
         * Get its creator.
         * @return Creator of the commit status
         * @throws IOException If there is any I/O problem
         */
        public User creator() throws IOException {
            return this.status.commit().repo().github()
                .users()
                .get(
                    this.status.json()
                        .getJsonObject("creator")
                        .getString("login")
            );
        }

        @Override
        public int identifier() {
            return this.status.identifier();
        }

        @Override
        public String url() {
            return this.status.url();
        }

        @Override
        public Commit commit() {
            return this.status.commit();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.status.json();
        }
    }
}
