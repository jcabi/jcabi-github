/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Event;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkIssueLabels}.
 * @since 0.6
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MkIssueLabelsTest {
    /**
     * Username of actor.
     */
    private static final String USER = "jeff";

    @Test
    void iteratesIssues() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final String name = "bug";
        repo.labels().create(name, "c0c0c0");
        final Issue issue = repo.issues().create("title", "body");
        issue.labels().add(Collections.singletonList(name));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.labels().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void createsLabelsThroughDecorator() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue issue = repo.issues().create("how are you?", "");
        final String name = "task";
        new IssueLabels.Smart(issue.labels()).addIfAbsent(name, "f0f0f0");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.labels().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkIssueLabels creates a "labeled" event when a label is added.
     */
    @Test
    void addingLabelGeneratesEvent() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final String name = "confirmed";
        repo.labels().create(name, "663399");
        final Issue issue = repo.issues().create("Titular", "Corpus");
        issue.labels().add(Collections.singletonList(name));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.events(),
            Matchers.iterableWithSize(1)
        );
        final Event.Smart labeled = new Event.Smart(
            issue.events().iterator().next()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            labeled.type(),
            Matchers.equalTo(Event.LABELED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            labeled.author().login(),
            Matchers.equalTo(MkIssueLabelsTest.USER)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            labeled.repo(),
            Matchers.equalTo(repo)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            labeled.label().get().name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * MkIssueLabels creates an "unlabeled" event when a label is removed.
     */
    @Test
    void removingLabelGeneratesEvent() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final String name = "invalid";
        repo.labels().create(name, "ee82ee");
        final Issue issue = repo.issues().create("Rewrite", "Sound good?");
        issue.labels().add(Collections.singletonList(name));
        issue.labels().remove(name);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.events(),
            Matchers.iterableWithSize(2)
        );
        final Iterator<Event> events = issue.events().iterator();
        events.next();
        final Event.Smart unlabeled = new Event.Smart(events.next());
        MatcherAssert.assertThat(
            "Values are not equal",
            unlabeled.type(),
            Matchers.equalTo(Event.UNLABELED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            unlabeled.author().login(),
            Matchers.equalTo(MkIssueLabelsTest.USER)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            unlabeled.repo(),
            Matchers.equalTo(repo)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            unlabeled.label().get().name(),
            Matchers.equalTo(name)
        );
    }
}
