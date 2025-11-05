/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.GitHubThread;
import com.jcabi.github.Notification;
import com.jcabi.github.Notifications;
import java.io.IOException;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Mock for Github Notifications.
 *
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 * @todo #913:30min Implement markAsRead() and thread() operations in
 *  MkNotifications. Don't forget about unit tests.
 */
@Immutable
final class MkNotifications implements Notifications {

    /**
     * Storage with notifications.
     */
    private final transient MkStorage storage;
    /**
     * XPath for the notifications in the storage.
     */
    private final transient String xpath;

    /**
     * Public ctor.
     * @param strge The mock storage of github data.
     * @param entry The xpath to the notifications in the storage.
     */
    MkNotifications(final MkStorage strge, final String entry) {
        this.storage = strge;
        this.xpath = entry;
    }

    @Override
    public Iterable<Notification> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath,
            xml -> this.get(
                Integer.valueOf(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public Notification get(final int number) {
        try {
            return new MkNotification(
                this.storage.xml().nodes(
                    this.xpath.concat(String.format("[id = %s]", number))
                ).get(0)
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void markAsRead() {
        throw new NotImplementedException("MkNotifications#markAsRead");
    }

    @Override
    public GitHubThread thread(final int number) {
        throw new NotImplementedException("MkNotifications#thread");
    }
}
