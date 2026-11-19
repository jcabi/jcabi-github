/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.User;
import com.jcabi.github.Users;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Unit tests for the MkUser class.
 *
 * @since 0.1
 */
final class MkUserTest {

    /**
     * Tests that MkUser.organizations() returns a value.
     *
     * @throws IOException when there is an error creating the MkUser begin tested
     */
    @Test
    void fetchesOrganizations() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkUser(
                new MkStorage.InFile(),
                "orgTestIterate"
            ).organizations(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkUser returns notifications.
     *
     * <p>There is no requirement for us to return actual mock data because our
     * API does not provide a way to create notifications.</p>
     *
     * @throws IOException If there is an error creating the user.
     */
    @Test
    void returnsNotifications() throws IOException {
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
     *
     * @throws IOException If any error occurs.
     */
    @Test
    void marksNotificationsAsReadUpToDate() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MkUserTest.notified(storage);
        MatcherAssert.assertThat(
            "Older notification is not marked as read",
            storage.xml().xpath("//notification[id = 1]/read/text()").get(0),
            Matchers.is("true")
        );
    }

    /**
     * Must leave notifications newer than the given date alone.
     *
     * @throws IOException If any error occurs.
     */
    @Test
    void keepsNewerNotificationsUnread() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MkUserTest.notified(storage);
        MatcherAssert.assertThat(
            "Newer notification is marked as read",
            storage.xml().xpath("//notification[id = 2]/read/text()").get(0),
            Matchers.is("false")
        );
    }

    @Test
    void listsFollowedUsers() throws IOException {
        final Users users = new MkGitHub("alice").users();
        final User alice = users.self();
        alice.follow(users.add("bob"));
        alice.follow(users.add("carol"));
        MatcherAssert.assertThat(
            "Followed users are not listed",
            alice.following(),
            Matchers.contains(users.get("bob"), users.get("carol"))
        );
    }

    @Test
    void listsFollowers() throws IOException {
        final MkGitHub github = new MkGitHub("alice");
        final User alice = github.users().self();
        final User bob = github.relogin("bob").users().self();
        final User carol = github.relogin("carol").users().self();
        bob.follow(alice);
        carol.follow(alice);
        github.relogin("dave").users().self().follow(bob);
        MatcherAssert.assertThat(
            "Followers are not listed",
            alice.followers(),
            Matchers.contains(bob, carol)
        );
    }

    @Test
    void followsUserOnlyOnce() throws IOException {
        final Users users = new MkGitHub("alice").users();
        final User alice = users.self();
        final User bob = users.add("bob");
        alice.follow(bob);
        alice.follow(bob);
        MatcherAssert.assertThat(
            "User is followed more than once",
            alice.following(),
            Matchers.contains(bob)
        );
    }

    @Test
    void unfollowsUser() throws IOException {
        final Users users = new MkGitHub("alice").users();
        final User alice = users.self();
        final User bob = users.add("bob");
        final User carol = users.add("carol");
        alice.follow(bob);
        alice.follow(carol);
        alice.unfollow(bob);
        MatcherAssert.assertThat(
            "Unfollowed user is still listed",
            alice.following(),
            Matchers.contains(carol)
        );
    }

    @Test
    void dropsFollowerOfUnfollowedUser() throws IOException {
        final Users users = new MkGitHub("alice").users();
        final User alice = users.self();
        final User bob = users.add("bob");
        alice.follow(bob);
        alice.unfollow(bob);
        MatcherAssert.assertThat(
            "Unfollowed user still has the follower",
            bob.followers(),
            Matchers.emptyIterable()
        );
    }

    private static void notified(final MkStorage storage) throws IOException {
        storage.apply(new Directives().xpath("/github").add("users"));
        final User user = new MkUsers(storage, "joe").add("joe");
        final Instant upto = Instant.now();
        storage.apply(
            new Directives()
                .xpath("//notifications")
                .add("notification")
                    .add("id").set(1).up()
                    .add("date").set(
                        upto.minus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
                .add("notification")
                    .add("id").set(2).up()
                    .add("date").set(
                        upto.plus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
        );
        user.markAsRead(upto);
    }
}
