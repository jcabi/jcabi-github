/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
final class MkIssueLabelsTest {

    /**
     * Username of actor.
     */
    private static final String USER = "jeff";

    /**
     * Name of the label.
     */
    private static final String NAME = "confirmed";

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
        new IssueLabels.Smart(issue.labels()).addIfAbsent("task", "f0f0f0");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.labels().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkIssueLabels creates a "labeled" event when a label is added.
     * @throws IOException If some problem inside
     */
    @Test
    void addingLabelGeneratesEvent() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkIssueLabelsTest.labeled(new MkGitHub().randomRepo()).events(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkIssueLabels types the event of adding a label.
     * @throws IOException If some problem inside
     */
    @Test
    void typesEventOfAddingLabel() throws IOException {
        MatcherAssert.assertThat(
            "Event of adding a label has a wrong type",
            MkIssueLabelsTest.first(
                MkIssueLabelsTest.labeled(new MkGitHub().randomRepo())
            ).type(),
            Matchers.equalTo(Event.LABELED)
        );
    }

    /**
     * MkIssueLabels signs the event of adding a label.
     * @throws IOException If some problem inside
     */
    @Test
    void signsEventOfAddingLabel() throws IOException {
        MatcherAssert.assertThat(
            "Event of adding a label has a wrong author",
            MkIssueLabelsTest.first(
                MkIssueLabelsTest.labeled(new MkGitHub().randomRepo())
            ).author().login(),
            Matchers.equalTo(MkIssueLabelsTest.USER)
        );
    }

    /**
     * MkIssueLabels puts the event of adding a label into its repo.
     * @throws IOException If some problem inside
     */
    @Test
    void putsEventOfAddingLabelIntoRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Event of adding a label belongs to a wrong repo",
            MkIssueLabelsTest.first(MkIssueLabelsTest.labeled(repo)).repo(),
            Matchers.equalTo(repo)
        );
    }

    /**
     * MkIssueLabels names the label in the event of adding it.
     * @throws IOException If some problem inside
     */
    @Test
    void namesLabelInEventOfAddingIt() throws IOException {
        MatcherAssert.assertThat(
            "Event of adding a label names a wrong label",
            MkIssueLabelsTest.first(
                MkIssueLabelsTest.labeled(new MkGitHub().randomRepo())
            ).label().get().name(),
            Matchers.equalTo(MkIssueLabelsTest.NAME)
        );
    }

    /**
     * MkIssueLabels creates an "unlabeled" event when a label is removed.
     * @throws IOException If some problem inside
     */
    @Test
    void removingLabelGeneratesEvent() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkIssueLabelsTest.unlabeled(new MkGitHub().randomRepo()).events(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkIssueLabels types the event of removing a label.
     * @throws IOException If some problem inside
     */
    @Test
    void typesEventOfRemovingLabel() throws IOException {
        MatcherAssert.assertThat(
            "Event of removing a label has a wrong type",
            MkIssueLabelsTest.second(
                MkIssueLabelsTest.unlabeled(new MkGitHub().randomRepo())
            ).type(),
            Matchers.equalTo(Event.UNLABELED)
        );
    }

    /**
     * MkIssueLabels signs the event of removing a label.
     * @throws IOException If some problem inside
     */
    @Test
    void signsEventOfRemovingLabel() throws IOException {
        MatcherAssert.assertThat(
            "Event of removing a label has a wrong author",
            MkIssueLabelsTest.second(
                MkIssueLabelsTest.unlabeled(new MkGitHub().randomRepo())
            ).author().login(),
            Matchers.equalTo(MkIssueLabelsTest.USER)
        );
    }

    /**
     * MkIssueLabels puts the event of removing a label into its repo.
     * @throws IOException If some problem inside
     */
    @Test
    void putsEventOfRemovingLabelIntoRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Event of removing a label belongs to a wrong repo",
            MkIssueLabelsTest.second(MkIssueLabelsTest.unlabeled(repo)).repo(),
            Matchers.equalTo(repo)
        );
    }

    /**
     * MkIssueLabels names the label in the event of removing it.
     * @throws IOException If some problem inside
     */
    @Test
    void namesLabelInEventOfRemovingIt() throws IOException {
        MatcherAssert.assertThat(
            "Event of removing a label names a wrong label",
            MkIssueLabelsTest.second(
                MkIssueLabelsTest.unlabeled(new MkGitHub().randomRepo())
            ).label().get().name(),
            Matchers.equalTo(MkIssueLabelsTest.NAME)
        );
    }

    /**
     * An issue with one label attached to it.
     * @param repo Repo to create the issue in
     * @return Issue with a label
     * @throws IOException If some problem inside
     */
    private static Issue labeled(final Repo repo) throws IOException {
        repo.labels().create(MkIssueLabelsTest.NAME, "663399");
        final Issue issue = repo.issues().create("Titular", "Corpus");
        issue.labels().add(
            Collections.singletonList(MkIssueLabelsTest.NAME)
        );
        return issue;
    }

    /**
     * An issue with one label attached and then detached.
     * @param repo Repo to create the issue in
     * @return Issue without a label
     * @throws IOException If some problem inside
     */
    private static Issue unlabeled(final Repo repo) throws IOException {
        final Issue issue = MkIssueLabelsTest.labeled(repo);
        issue.labels().remove(MkIssueLabelsTest.NAME);
        return issue;
    }

    /**
     * The first event of the given issue.
     * @param issue Issue to take the event from
     * @return Event
     * @throws IOException If some problem inside
     */
    private static Event.Smart first(final Issue issue) throws IOException {
        return new Event.Smart(issue.events().iterator().next());
    }

    /**
     * The second event of the given issue.
     * @param issue Issue to take the event from
     * @return Event
     * @throws IOException If some problem inside
     */
    private static Event.Smart second(final Issue issue) throws IOException {
        final Iterator<Event> events = issue.events().iterator();
        events.next();
        return new Event.Smart(events.next());
    }
}
