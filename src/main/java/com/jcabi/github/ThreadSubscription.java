/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * Encapsulates data from the response for get/set subscription calls to
 * GitHub Notification API.
 * @see <a href="https://developer.github.com/v3/activity/notifications/#get-a-thread-subscription">Get a Thread Subscription</a>, <a href="https://developer.github.com/v3/activity/notifications/#set-a-thread-subscription">Set a Thread Subscription</a>
 * @since 1.0
 * @todo #913:30min Add properties subscribed (boolean), ignored (boolean),
 *  createdAt (Date), url (String), threadUrl (String) as shown in section
 *  "Response" at
 *  https://developer.github.com/v3/activity/notifications/#response-6 .
 */
public interface ThreadSubscription {
}
