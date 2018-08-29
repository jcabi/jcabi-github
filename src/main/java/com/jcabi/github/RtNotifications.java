/**
 * Copyright (c) 2013-2018, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import javax.json.JsonObject;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Github Notifications.
 *
 * @author Giang Le (lthuangiang@gmail.com)
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
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
        return new RtPagination<Notification>(
            this.request.uri()
                .queryParam("all", "true")
                .queryParam("since", "1970-01-01T00:00:00Z")
                .back(),
            new RtValuePagination.Mapping<Notification, JsonObject>() {
                @Override
                public Notification map(final JsonObject json) {
                    return new RtNotification(
                        Long.valueOf(json.getString("id"))
                    );
                }
            }
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
