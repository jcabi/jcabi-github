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
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub commit status.
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
        public static Status.State forValue(
            final String ident
        ) {
            return Status.State.valueOf(ident.toUpperCase(Locale.ENGLISH));
        }
    }

    /**
     * Smart Status with extra features.
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
        public Status.State state() throws IOException {
            return Status.State.forValue(this.jsn.text("state"));
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
                return new GitHub.Time(
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
                return new GitHub.Time(
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
