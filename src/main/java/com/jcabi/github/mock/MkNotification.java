/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Notification;
import com.jcabi.xml.XML;

/**
 * Mock for GitHub Notification.
 *
 * @since 0.25
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 */
@Immutable
final class MkNotification implements Notification {

    /**
     * XML data for this mock notification.
     */
    private final transient XML data;

    /**
     * Public ctor.
     * @param xml XML holding the data for this notification.
     */
    MkNotification(final XML xml) {
        this.data = xml;
    }

    @Override
    public long number() {
        return Long.valueOf(this.data.xpath("//id/text()").get(0));
    }
}
