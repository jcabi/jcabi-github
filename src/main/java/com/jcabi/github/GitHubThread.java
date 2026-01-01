/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * Represents a GitHub notifications thread.
 * @since 1.0
 */
public interface GitHubThread {
    /**
     * Marks this thread as read.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#mark-a-thread-as-read">Mark a thread as read</a>
     */
    void markAsRead();

    /**
     * Checks, if the current user is subscribed to this thread.
     * @return Subscription data, if the user is subscribe, null otherwise.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#get-a-thread-subscription">Get a Thread Subscription</a>
     */
    ThreadSubscription getSubscription();

    /**
     * Subscribes the user to the thread and/or makes the user ignore the
     *  thread.
     * @param subscribe True, if notifications should be received from this
     *  thread.
     * @param ignore True, if all notifications should be blocked from this
     *  thread.
     * @return Data of the subscription.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#set-a-thread-subscription">Set a Thread Subscription</a>
     */
    ThreadSubscription setSubscription(boolean subscribe,
        boolean ignore);

    /**
     * Unsubscribes the user from this thread.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#delete-a-thread-subscription">Delete a Thread Subscription</a>
     */
    void deleteSubscription();
}
