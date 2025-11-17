/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.github.Event;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkEvent}.
 */
public final class MkEventTest {
    /**
     * Can get created_at value from json object.
     */
    @Test
    public void canGetCreatedAt() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "test_user";
        final Repo repo = new MkGithub(storage, user).randomRepo();
        final MkIssueEvents events = (MkIssueEvents) repo.issueEvents();
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
     */
    @Test
    public void canGetPresentLabel() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "ken";
        final Repo repo = new MkGithub(storage, user).repos().create(
            new Repos.RepoCreate("foo", false)
        );
        final MkIssueEvents events = (MkIssueEvents) repo.issueEvents();
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
     */
    @Test
    public void canGetAbsentLabel() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final String user = "barbie";
        final Repo repo = new MkGithub(storage, user).repos().create(
            new Repos.RepoCreate("bar", false)
        );
        final int num = ((MkIssueEvents) repo.issueEvents()).create(
            Event.LABELED,
            1,
            user,
            Optional.absent()
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
