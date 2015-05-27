/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.google.common.base.Optional;
import com.jcabi.github.Event;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkEvent}.
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class MkEventTest {
    /**
     * Can get created_at value from json object.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetCreatedAt() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "test_user";
        final Repo repo = new MkGithub(storage, user).repos().create(
            new Repos.RepoCreate("test", false)
        );
        final MkIssueEvents events = (MkIssueEvents) (repo.issueEvents());
        final int eventnum = events.create(
            "test_type",
            1,
            user,
            Optional.of("test_label")
        ).number();
        MatcherAssert.assertThat(
            new MkEvent(
                storage,
                user,
                repo.coordinates(),
                eventnum
            )
                .json().getString("created_at"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkEvent can get present label value from json object.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetPresentLabel() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "ken";
        final Repo repo = new MkGithub(storage, user).repos().create(
            new Repos.RepoCreate("foo", false)
        );
        final MkIssueEvents events = (MkIssueEvents) (repo.issueEvents());
        final String label = "problem";
        final int num = events.create(
            Event.LABELED,
            1,
            user,
            Optional.of(label)
        ).number();
        MatcherAssert.assertThat(
            new Event.Smart(
                new MkEvent(
                    storage,
                    user,
                    repo.coordinates(),
                    num
                )
            ).label().get().name(),
            Matchers.equalTo(label)
        );
    }

    /**
     * MkEvent can get absent label value from json object.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetAbsentLabel() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "barbie";
        final Repo repo = new MkGithub(storage, user).repos().create(
            new Repos.RepoCreate("bar", false)
        );
        final int num = ((MkIssueEvents) (repo.issueEvents())).create(
            Event.LABELED,
            1,
            user,
            Optional.<String>absent()
        ).number();
        MatcherAssert.assertThat(
            new Event.Smart(
                new MkEvent(
                    storage,
                    user,
                    repo.coordinates(),
                    num
                )
            ).label(),
            Matchers.equalTo(Optional.<Label>absent())
        );
    }
}
