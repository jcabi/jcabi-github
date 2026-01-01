/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub Notification.
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 * @since 0.19
 */
@Immutable
final class RtNotification implements Notification {

    /**
     * Release notifnumber.
     */
    private final transient long notifnumber;

    /**
     * Public ctor.
     * @param notifid Notification notifnumber
     */
    RtNotification(final long notifid) {
        this.notifnumber = notifid;
    }

    @Override
    public long number() {
        return this.notifnumber;
    }
}
