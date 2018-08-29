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
package com.jcabi.github.mock;

import com.jcabi.github.UserOrganizations;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Unit tests for the MkUser class.
 *
 * @author Ed Hillmann (edhillmann@yahoo.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
public final class MkUserTest {

    /**
     * Tests that MkUser.organizations() returns a value.
     *
     * @throws IOException when there is an error creating the MkUser begin tested
     */
    @Test
    public void testGetOrganizations() throws IOException {
        final MkUser user = new MkUser(
            new MkStorage.InFile(),
            "orgTestIterate"
        );
        final UserOrganizations orgs = user.organizations();
        MatcherAssert.assertThat(
            orgs,
            Matchers.notNullValue()
        );
    }

    /**
     * MkUser returns notifications.
     * <p>
     * There is no requirement for us to return actual mock data because our
     * API does not provide a way to create notifications.
     * @throws IOException If there is an error creating the user.
     */
    @Test
    public void returnsNotifications() throws IOException {
        MatcherAssert.assertThat(
            new MkUser(
                new MkStorage.InFile(),
                "notifications"
            ).notifications(),
            Matchers.notNullValue()
        );
    }

    /**
     * Must mark notifications as read only if their 'lastread' is equal or
     * older than the given date.
     * @throws IOException If any error occurs.
     */
    @Test
    public void marksNotificationsAsReadUpToDate() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(new Directives().xpath("/github").add("users"));
        final MkUser user = new MkUser(storage, "joe");
        final Instant upto = Instant.now();
        storage.apply(
            new Directives()
                .xpath("//notifications")
                .add("notification")
                    .add("id").set(1).up()
                    .add("date").set(
                        // @checkstyle MagicNumberCheck (1 line)
                        upto.minus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
                .add("notification")
                    .add("id").set(2).up()
                    .add("date").set(
                         // @checkstyle MagicNumberCheck (1 line)
                        upto.plus(30, ChronoUnit.MINUTES).toEpochMilli()
                    ).up()
                    .add("read").set(false).up()
                    .up()
        );
        user.markAsRead(Date.from(upto));
        MatcherAssert.assertThat(
            storage.xml().xpath("//notification[id = 1]/read/text()").get(0),
            Matchers.is("true")
        );
        MatcherAssert.assertThat(
            storage.xml().xpath("//notification[id = 2]/read/text()").get(0),
            Matchers.is("false")
        );
    }
}
