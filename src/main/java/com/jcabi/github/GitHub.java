/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.EqualsAndHashCode;

/**
 * GitHub client, starting point to the entire library.
 *
 * <p>This is how you start communicating with GitHub API:
 *
 * <pre> GitHub github = new RtGitHub(oauthKey);
 * Repo repo = github.repos().get(
 *     new Coordinates.Simple("jcabi/jcabi-github")
 * );
 * Issues issues = repo.issues();
 * Issue issue = issues.post("issue title", "issue body");</pre>
 *
 * <p>It is strongly recommended to use
 * {@link com.jcabi.http.wire.RetryWire} to avoid
 * accidental I/O exceptions:
 *
 * <pre> GitHub github = new RtGitHub(
 *   new RtGitHub(oauthKey)
 *     .entry()
 *     .through(RetryWire.class)
 * );</pre>
 *
 * <p>The interfaces in this packages are trying to cover as much
 * as possible of GitHub API. However, there are parts of API that are
 * rarely used and making Java classes for them is not an effective
 * idea. That's why {@code GitHub} class has {@link #entry()} method,
 * which returns an entry point to the RESTful API. For example, you
 * want to use
 * <a href="https://developer.github.com/v3/search/#search-repositories">"Search
 * Repositories"</a> feature of GitHub:
 *
 * <pre> GitHub github = new RtGitHub(oauthKey);
 * int found = github.entry()
 *   .uri().path("/search/repositories").back()
 *   .method(Request.GET)
 *   .fetch()
 *   .as(JsonResponse.class)
 *   .getJsonObject()
 *   .getNumber("total_count")
 *   .intValue();</pre>
 * @since 0.1
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface GitHub {

    /**
     * RESTful request, an entry point to the GitHub API.
     * @return Request
     */
    Request entry();

    /**
     * Get repositories.
     * @return Repositories
     */
    Repos repos();

    /**
     * Get Gists API entry point.
     * @return Gists API entry point
     */
    Gists gists();

    /**
     * Get Users API entry point.
     * @return Users API entry point
     * @since 0.4
     */
    Users users();

    /**
     * Get Organizations API entry point.
     * @return Organizations API entry point
     * @since 0.24
     */
    Organizations organizations();

    /**
     * Get Markdown API entry point.
     * @return Markdown API entry point
     * @since 0.6
     */
    Markdown markdown();

    /**
     * Rate limit API entry point.
     * @return Rate limit API
     * @since 0.6
     */
    Limits limits();

    /**
     * Search API entry point.
     * @return Search API
     * @since 0.8
     */
    Search search();

    /**
     * Get gitignores.
     * @return Gitignotes API
     * @see <a href="https://developer.github.com/v3/gitignore/">Gitignore API</a>
     * @since 0.8
     */
    Gitignores gitignores();

    /**
     * Get meta information.
     * @return JSON with meta
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/meta/">Meta API</a>
     * @since 0.6
     */
    JsonObject meta() throws IOException;

    /**
     * Get emojis.
     * @return JSON with emojis
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/emojis/">Emojis API</a>
     * @since 0.6
     */
    JsonObject emojis() throws IOException;

    /**
     * Time in GitHub JSON.
     * @see <a href="https://developer.github.com/v3/#schema">Schema</a>
     * @since 0.2
     */
    @Immutable
    @EqualsAndHashCode(of = "msec")
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
            this(GitHub.Time.format().parse(text));
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
            return GitHub.Time.format().format(this.date());
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
                GitHub.Time.FORMAT_ISO, Locale.ENGLISH
            );
            fmt.setTimeZone(GitHub.Time.TIMEZONE);
            return fmt;
        }
    }

}
