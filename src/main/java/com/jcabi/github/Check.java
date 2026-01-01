/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * GitHub check.
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @since 1.5.0
 */
public interface Check {

    /**
     * Undefined status or conclusion.
     */
    String UNDEFINED_VALUE = "undefined";

    /**
     * Checks whether Check was successful.
     * @return True if Check was successful.
     * @throws IOException If there is any I/O problem.
     */
    boolean successful() throws IOException;

    /**
     * Check status.
     * You can read more about it
     * @checkstyle LineLengthCheck (1 lines)
     * <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28#list-check-runs-for-a-git-reference"> here </a>
     */
    enum Status {
        /**
         * Queued.
         */
        QUEUED("queued"),

        /**
         * In progress.
         */
        IN_PROGRESS("in_progress"),

        /**
         * Completed.
         */
        COMPLETED("completed"),

        /**
         * Undefined. If GitHub response doesn't contain the Status value.
         */
        UNDEFINED(Check.UNDEFINED_VALUE);

        /**
         * Status.
         */
        private final String status;

        /**
         * Ctor.
         * @param stat Status.
         */
        Status(final String stat) {
            this.status = stat;
        }

        /**
         * Status.
         * @return Status.
         */
        public String value() {
            return this.status;
        }

        /**
         * Get status from string.
         * @param value String value.
         * @return Status.
         */
        @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
        public static Check.Status fromString(final String value) {
            return Arrays.stream(Check.Status.values())
                .filter(stat -> stat.same(value))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("Invalid value %s for status", value)
                    )
                );
        }

        /**
         * Check if check is finished.
         * @return True if check is finished.
         */
        boolean finished() {
            return this == Check.Status.COMPLETED;
        }

        /**
         * Check if status is the same as value.
         * @param value Value.
         * @return True if status is the same as value.
         */
        boolean same(final String value) {
            return this.status.equals(value.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * Check conclusion.
     * You can read more about it
     * @checkstyle LineLengthCheck (1 lines)
     * <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28#list-check-runs-for-a-git-reference"> here </a>
     */
    enum Conclusion {

        /**
         * Action required.
         */
        ACTION_REQUIRED("action_required"),

        /**
         * Cancelled.
         */
        CANCELLED("cancelled"),

        /**
         * Failure.
         */
        FAILURE("failure"),

        /**
         * Neutral.
         */
        NEUTRAL("neutral"),

        /**
         * Success.
         */
        SUCCESS("success"),

        /**
         * Skipped.
         */
        SKIPPED("skipped"),

        /**
         * Stale.
         */
        STALE("stale"),

        /**
         * Timed out.
         */
        TIMED_OUT("timed_out"),

        /**
         * Undefined. If GitHub response doesn't contain the Conclusion value.
         */
        UNDEFINED(Check.UNDEFINED_VALUE);

        /**
         * Conclusion.
         */
        private final String conclusion;

        /**
         * Ctor.
         * @param con Conclusion.
         */
        Conclusion(final String con) {
            this.conclusion = con;
        }

        /**
         * Get conclusion from string.
         * @param value String value.
         * @return Conclusion.
         */
        @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
        public static Check.Conclusion fromString(final String value) {
            return Arrays.stream(Check.Conclusion.values())
                .filter(stat -> stat.same(value))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("Invalid value %s for conclusion", value)
                    )
                );
        }

        /**
         * Conclusion.
         * @return Conclusion.
         */
        public String value() {
            return this.conclusion;
        }

        /**
         * Check if check is successful.
         * @return True if check is successful.
         */
        boolean successful() {
            return this == Check.Conclusion.SUCCESS;
        }

        /**
         * Check if conclusion is the same as value.
         * @param value Value to compare.
         * @return True if conclusion is the same as value.
         */
        boolean same(final String value) {
            return this.conclusion.equals(value.toLowerCase(Locale.ROOT));
        }
    }
}
