/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import org.apache.commons.lang3.NotImplementedException;

/**
 * GitHub Notifications.
 *
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
 * @since 0.15
 * @todo #913:30min Implement markAsRead(), thread(final int number) operations
 *  in RtNotifications. Don't forget about unit tests.
 */
@Immutable
final class RtNotifications implements Notifications {
    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req The request for this notifications.
     */
    RtNotifications(final Request req) {
        this.request = req;
    }

    @Override
    public Iterable<Notification> iterate() {
        return new RtPagination<>(
            this.request.uri()
                .queryParam("all", "true")
                .queryParam("since", "1970-01-01T00:00:00Z")
                .back(),
            json -> new RtNotification(
                Long.valueOf(json.getString("id"))
            )
        );
    }

    @Override
    public Notification get(final int number) {
        return new RtNotification(number);
    }

    @Override
    public void markAsRead() {
        throw new NotImplementedException("RtNotifications#markAsRead");
    }

    @Override
    public GitHubThread thread(final int number) {
        throw new NotImplementedException("RtNotifications#thread");
    }
}
