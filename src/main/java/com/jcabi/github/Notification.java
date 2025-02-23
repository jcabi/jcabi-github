/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * Github Notification.
 * <p>Use a supplementary "smart" decorator to get other properties
 * from an notification.
 *
 * @since 0.19
 * @todo #920 Create Smart decorator to get other properties of Notification,
 *  such as reason, unread, updated_at, last_read_at, url, etc.
 *  See
 *  https://developer.github.com/v3/activity/notifications/#view-a-single-thread
 *  for details. Don't forget about unit tests.
 */
@Immutable
public interface Notification {
    /**
     * Notification id.
     * @return Id
     */
    long number();
}
