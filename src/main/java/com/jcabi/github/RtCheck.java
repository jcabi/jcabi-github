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

import java.util.Arrays;
import java.util.Locale;

/**
 * Github check.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @version $Id$
 * @since 1.5.0
 */
class RtCheck implements Check {

    /**
     * Status.
     */
    private final transient Status status;

    /**
     * Conclusion.
     */
    private final transient Conclusion conclusion;

    /**
     * Ctor.
     * @param stat Status.
     * @param conc Conclusion.
     */
    RtCheck(
        final String stat,
        final String conc
    ) {
        this(Status.fromString(stat), Conclusion.fromString(conc));
    }

    /**
     * Ctor.
     * @param stat Status.
     * @param conc Conclusion.
     */
    RtCheck(
        final Status stat,
        final Conclusion conc
    ) {
        this.status = stat;
        this.conclusion = conc;
    }

    @Override
    public boolean successful() {
        return this.status.finished() && this.conclusion.successful();
    }

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
        COMPLETED("completed");

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
         * Check if check is finished.
         * @return True if check is finished.
         */
        boolean finished() {
            return this == Status.COMPLETED;
        }

        /**
         * Get status from string.
         * @param value String value.
         * @return Status.
         */
        static Status fromString(final String value) {
            return Arrays.stream(Status.values())
                .filter(stat -> stat.same(value))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("Invalid value %s for status", value)
                    )
                );
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
        TIMED_OUT("timed_out");

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
         * Check if check is successful.
         * @return True if check is successful.
         */
        boolean successful() {
            return this == Conclusion.SUCCESS;
        }

        /**
         * Get conclusion from string.
         * @param value String value.
         * @return Conclusion.
         */
        static Conclusion fromString(final String value) {
            return Arrays.stream(Conclusion.values())
                .filter(stat -> stat.same(value))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("Invalid value %s for conclusion", value)
                    )
                );
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
