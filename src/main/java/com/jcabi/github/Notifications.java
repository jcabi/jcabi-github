/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub Notifications API.
 *
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 */
@Immutable
public interface Notifications {
    /**
     * Iterate them all.
     * @return Iterable of Notifications
     * @see <a href="https://developer.github.com/v3/activity/notifications/#list-your-notifications-in-a-repository">List your notifications in a repository</a>
     */
    Iterable<Notification> iterate();

    /**
     * Get a single notification.
     * @param number Notification id
     * @return Notification
     * @see <a href="https://developer.github.com/v3/activity/notifications/#view-a-single-thread">View a single thread</a>
     */
    Notification get(int number);

    /**
     * Marks all notifications on this repository as read.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#mark-notifications-as-read-in-a-repository">Mark notifications as read in a repository</a>
     */
    void markAsRead();

    /**
     * Get thread data.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#view-a-single-thread">View a single thread</a>
     * @param number Thread ID.
     * @return Data of the specified thread.
     */
    GitHubThread thread(int number);
}
