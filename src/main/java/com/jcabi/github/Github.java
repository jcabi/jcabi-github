/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.rexsl.test.Request;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;

/**
 * Github client, starting point to the entire library.
 *
 * <p>This is how you start communicating with Github API:
 *
 * <pre> Github github = new DefaultGithub(oauthKey);
 * Repo repo = github.repo("jcabi/jcabi-github");
 * Issues issues = repo.issues();
 * Issue issue = issues.post("issue title", "issue body");</pre>
 *
 * <p>It is strongly recommended to use
 * {@link com.rexsl.test.wire.RetryWire} to avoid
 * accidental I/O exceptions:
 *
 * <pre> Github github = new DefaultGithub(
 *   new DefaultGithub(oauthKey)
 *     .entry()
 *     .through(RetryWire.class)
 * );</pre>
 *
 * <p>The interfaces in this packages are trying to cover as much
 * as possible of Github API. However, there are parts of API that are
 * rarely used and making Java classes for them is not an effective
 * idea. That's why {@code Github} class has {@link #entry()} method,
 * which returns an entry point to the RESTful API. For example, you
 * want to use
 * <a href="http://developer.github.com/v3/search/#search-repositories">"Search
 * Repositories"</a> feature of Github:
 *
 * <pre> Github github = new DefaultGithub(oauthKey);
 * int found = github.entry()
 *   .uri().path("/search/repositories").back()
 *   .method(Request.GET)
 *   .as(JsonResponse.class)
 *   .getJsonObject()
 *   .getNumber("total_count")
 *   .intValue();</pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Github {

    /**
     * RESTful request, an entry point to the Github API.
     * @return Request
     */
    @NotNull(message = "request is never NULL")
    Request entry();

    /**
     * Get repositories.
     * @return Repositories
     */
    @NotNull(message = "list of repositories is never NULL")
    Repos repos();

    /**
     * Get Gists API entry point.
     * @return Gists API entry point
     */
    @NotNull(message = "gists is never NULL")
    Gists gists();

    /**
     * Get Users API entry point.
     * @return Users API entry point
     * @since 0.4
     */
    @NotNull(message = "users is never NULL")
    Users users();

    /**
     * Get Markdown API entry point.
     * @return Markdown API entry point
     * @since 0.6
     */
    @NotNull(message = "markdown API is never NULL")
    Markdown markdown();

    /**
     * Rate limit API entry point.
     * @return Rate limit API
     * @since 0.6
     */
    @NotNull(message = "rate limit API is never NULL")
    Limits limits();

    /**
     * Get meta information.
     * @return JSON with meta
     * @throws IOException If there is any I/O problem
     * @since 0.6
     * @see <a href="http://developer.github.com/v3/meta/">Meta API</a>
     */
    @NotNull(message = "meta JSON is never NULL")
    JsonObject meta() throws IOException;

    /**
     * Get emojis.
     * @return JSON with emojis
     * @throws IOException If there is any I/O problem
     * @since 0.6
     * @see <a href="http://developer.github.com/v3/emojis/">Emojis API</a>
     */
    @NotNull(message = "emojis JSON is never NULL")
    JsonObject emojis() throws IOException;

    /**
     * Time in Github JSON.
     *
     * @since 0.2
     * @see <a href="http://developer.github.com/v3/#schema">Schema</a>
     */
    @Immutable
    final class Time {
        /**
         * Pattern to present day in ISO-8601.
         */
        public static final String FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        /**
         * The time zone we're in.
         */
        public static final TimeZone TIMEZONE = TimeZone.getTimeZone("UTC");
        /**
         * Encapsulated time in milliseconds.
         */
        private final transient long msec;
        /**
         * Ctor.
         */
        public Time() {
            this(new Date());
        }
        /**
         * Ctor.
         * @param text ISO date/time
         * @throws ParseException If fails
         */
        public Time(final String text) throws ParseException {
            this(Github.Time.format().parse(text));
        }
        /**
         * Ctor.
         * @param date Date to encapsulate
         */
        public Time(final Date date) {
            this(date.getTime());
        }
        /**
         * Ctor.
         * @param millis Milliseconds
         */
        public Time(final long millis) {
            this.msec = millis;
        }
        @Override
        public String toString() {
            return Github.Time.format().format(this.date());
        }
        /**
         * Get date.
         * @return Date
         */
        public Date date() {
            return new Date(this.msec);
        }
        /**
         * Make format.
         * @return Date format
         */
        private static DateFormat format() {
            final DateFormat fmt = new SimpleDateFormat(
                Github.Time.FORMAT_ISO, Locale.ENGLISH
            );
            fmt.setTimeZone(Github.Time.TIMEZONE);
            return fmt;
        }
    }

}
