/**
 * Copyright (c) 2013-2017, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.github.Notification;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkNotifications}.
 *
 * @author Piotr Pradzynski (prondzyn@gmail.com)
 * @version $Id$
 */
public final class MkNotificationsTest {

    /**
     * MkNotifications can fetch empty list of Notification.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchesEmptyListOfNotifications() throws Exception {
        MatcherAssert.assertThat(
            new MkNotifications(
                new MkStorage.InFile(),
                "notifications"
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkNotifications can fetch non empty list of Notifications.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesNonEmptyListOfNotifications() throws Exception  {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("2").up().up()
                    .add("notification")
                        .add("id").set("3").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).iterate(),
            Matchers.<Notification>iterableWithSize(3)
        );
    }

    /**
     * MkNotifications can fetch a notification by id.
     * @throws Exception If something goes wrong
     */
    @Test
    public void fetchesNotificationById() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("2").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).get(2),
            Matchers.notNullValue()
        );
    }

    /**
     * MkNotifications can fetch a notification by id.
     * @throws Exception If something goes wrong
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void cannotFetchNotificationByNonExistentId() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("notifications")
                    .add("notification")
                        .add("id").set("1").up().up()
                    .add("notification")
                        .add("id").set("3").up().up()
        );
        MatcherAssert.assertThat(
            new MkNotifications(
                storage,
                "/github/notifications/notification"
            ).get(2),
            Matchers.notNullValue()
        );
    }
}
