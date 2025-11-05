/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Notification;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkNotifications}.
 *
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkNotificationsTest {

    /**
     * MkNotifications can fetch empty list of Notification.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchesEmptyListOfNotifications() throws Exception {
        MatcherAssert.assertThat(
            new MkNotifications(
                new MkStorage.InFile(),
                "notifications"
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkNotifications can fetch non empty list of Notifications.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesNonEmptyListOfNotifications() throws Exception  {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("2").up().up()
                    .add("notification")
                        .add("id").set("3").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).iterate(),
            // @checkstyle MagicNumberCheck (1 line)
            Matchers.<Notification>iterableWithSize(3)
        );
    }

    /**
     * MkNotifications can fetch a notification by id.
     * @throws Exception If something goes wrong
     */
    @Test
    public void fetchesNotificationById() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("2").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).get(2),
            Matchers.notNullValue()
        );
    }

    /**
     * MkNotifications can fetch a notification by id.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void cannotFetchNotificationByNonExistentId() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("3").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).get(2),
            Matchers.notNullValue()
        );
    }
}
