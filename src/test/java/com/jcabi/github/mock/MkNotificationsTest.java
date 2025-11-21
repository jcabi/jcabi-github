/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkNotifications}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkNotificationsTest {

    @Test
    public void fetchesEmptyListOfNotifications() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new MkNotifications(
                new MkStorage.InFile(),
                "notifications"
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    public void fetchesNonEmptyListOfNotifications() throws IOException {
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
            "Collection size is incorrect",
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).iterate(),
            // @checkstyle MagicNumberCheck (1 line)
            Matchers.iterableWithSize(3)
        );
    }

    @Test
    public void fetchesNotificationById() throws IOException {
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
            "Value is null",
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).get(2),
            Matchers.notNullValue()
        );
    }

    @Test
    public void cannotFetchNotificationByNonExistentId() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("3").up().up()
        );
        final MkNotifications notifications = new MkNotifications(
            storage,
            "/github/notifications/notification"
        );
        Assertions.assertThrows(
            IndexOutOfBoundsException.class,
            () -> notifications.get(2),
            "Should throw when notification ID does not exist"
        );
    }
}
