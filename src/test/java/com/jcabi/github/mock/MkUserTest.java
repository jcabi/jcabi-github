/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.User;
import com.jcabi.github.UserOrganizations;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Unit tests for the MkUser class.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
public final class MkUserTest {

    /**
     * Tests that MkUser.organizations() returns a value.
     * @throws IOException when there is an error creating the MkUser begin tested
     */
    @Test
    public void testGetOrganizations() throws IOException {
        final MkUser user = new MkUser(
            new MkStorage.InFile(),
            "orgTestIterate"
        );
        final UserOrganizations orgs = user.organizations();
        MatcherAssert.assertThat(
            "Value is null",
            orgs,
            Matchers.notNullValue()
        );
    }

    /**
     * MkUser returns notifications.
     * <p>
     * There is no requirement for us to return actual mock data because our
     * API does not provide a way to create notifications.
     * @throws IOException If there is an error creating the user.
     */
    @Test
    public void returnsNotifications() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkUser(
                new MkStorage.InFile(),
                "notifications"
            ).notifications(),
            Matchers.notNullValue()
        );
    }

    /**
     * Must mark notifications as read only if their 'lastread' is equal or
     * older than the given date.
     * @throws IOException If any error occurs.
     */
    @Test
    public void marksNotificationsAsReadUpToDate() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(new Directives().xpath("/github").add("users"));
        final User user = new MkUsers(storage, "joe").add("joe");
        final Instant upto = Instant.now();
        storage.apply(
            new Directives()
                .xpath("//notifications")
                .add("notification")
                    .add("id").set(1).up()
                    .add("date").set(
                        // @checkstyle MagicNumberCheck (1 line)
                        upto.minus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
                .add("notification")
                    .add("id").set(2).up()
                    .add("date").set(
                        // @checkstyle MagicNumberCheck (1 line)
                        upto.plus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
        );
        user.markAsRead(Date.from(upto));
        MatcherAssert.assertThat(
            "Values are not equal",
            storage.xml().xpath("//notification[id = 1]/read/text()").get(0),
            Matchers.is("true")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            storage.xml().xpath("//notification[id = 2]/read/text()").get(0),
            Matchers.is("false")
        );
    }
}
