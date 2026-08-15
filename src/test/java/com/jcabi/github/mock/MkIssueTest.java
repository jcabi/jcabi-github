/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkIssue}.
 * @since 0.1
 */
final class MkIssueTest {

    /**
     * MkIssue can open.
     * @throws Exception If some problem inside
     */
    @Test
    void opens() throws Exception {
        MatcherAssert.assertThat(
            "Fresh issue is not open",
            new Issue.Smart(MkIssueTest.issue()).isOpen(),
            Matchers.is(true)
        );
    }

    /**
     * MkIssue can close.
     * @throws Exception If some problem inside
     */
    @Test
    void closes() throws Exception {
        final Issue issue = MkIssueTest.issue();
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            "Closed issue is still open",
            new Issue.Smart(issue).isOpen(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can point to an absent pull request.
     * @throws Exception If some problem inside
     */
    @Test
    void pointsToAnEmptyPullRequest() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(MkIssueTest.issue()).isPull(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can show an issue author.
     * @throws Exception If some problem inside
     */
    @Test
    void showsIssueAuthor() throws Exception {
        MatcherAssert.assertThat(
            "Value is null",
            new Issue.Smart(MkIssueTest.issue()).author().login(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkIssue can change title.
     * @throws Exception If some problem inside
     */
    @Test
    void changesTitle() throws Exception {
        final Issue issue = MkIssueTest.issue();
        new Issue.Smart(issue).title("hey, works?");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Issue.Smart(issue).title(),
            Matchers.startsWith("hey, ")
        );
    }

    /**
     * MkIssue can change body.
     * @throws Exception If some problem inside
     */
    @Test
    void changesBody() throws Exception {
        final Issue issue = MkIssueTest.issue();
        new Issue.Smart(issue).body("hey, body works?");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Issue.Smart(issue).body(),
            Matchers.startsWith("hey, b")
        );
    }

    /**
     * MkIssue can expose the moment of its own creation.
     * @throws Exception If some problem inside
     */
    @Test
    void exposesCreationTime() throws Exception {
        MatcherAssert.assertThat(
            "Creation time is absent",
            new Issue.Smart(MkIssueTest.issue()).createdAt(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkIssue can expose the moment of its own update.
     * @throws Exception If some problem inside
     */
    @Test
    void exposesUpdateTime() throws Exception {
        MatcherAssert.assertThat(
            "Update time is absent",
            new Issue.Smart(MkIssueTest.issue()).updatedAt(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkIssue can expose its own HTML URL.
     * @throws Exception If some problem inside
     */
    @Test
    void exposesHtmlUrl() throws Exception {
        MatcherAssert.assertThat(
            "HTML URL is absent",
            new Issue.Smart(MkIssueTest.issue()).htmlUrl(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkIssue can list its labels.
     * @throws Exception If some problem inside
     */
    @Test
    void listsReadOnlyLabels() throws Exception {
        final Issue issue = MkIssueTest.issue();
        final String tag = "test-tag";
        issue.repo().labels().create(tag, "c0c0c0");
        issue.labels().add(Collections.singletonList(tag));
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            new Issue.Smart(issue).roLabels().iterate(),
            Matchers.hasItem(
                new CustomMatcher<Label>("label just created") {
                    @Override
                    public boolean matches(final Object item) {
                        return Label.class.cast(item).name().equals(tag);
                    }
                }
            )
        );
    }

    @Test
    void comparesSmallerIssue() throws IOException {
        MatcherAssert.assertThat(
            "Smaller issue is not smaller",
            MkIssueTest.issue(1).compareTo(MkIssueTest.issue(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerIssue() throws IOException {
        MatcherAssert.assertThat(
            "Bigger issue is not bigger",
            MkIssueTest.issue(2).compareTo(MkIssueTest.issue(1)),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkIssue can remember it's author.
     */
    @Test
    void canRememberItsAuthor() throws IOException {
        final MkGitHub first = new MkGitHub("first");
        final GitHub second = first.relogin("second");
        final Repo repo = first.randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(
                first.repos()
                    .get(repo.coordinates())
                    .issues().get(
                        second.repos()
                            .get(repo.coordinates())
                            .issues()
                            .create("", "")
                            .number()
                    )
            ).author().login(),
            Matchers.is("second")
        );
    }

    /**
     * Can check if issue exists.
     * @throws Exception if any error occurs.
     */
    @Test
    void canCheckIfIssueExists() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal", MkIssueTest.issue().exists(), Matchers.is(true)
        );
    }

    /**
     * MkIssue.exists() return false on nonexistent issues.
     */
    @Test
    void canCheckNonExistentIssue() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkIssue(
                new MkStorage.InFile(),
                "login",
                new Coordinates.Simple("user", "repo"),
                1
            ).exists(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can assign a user.
     * @throws Exception If some problem inside
     */
    @Test
    void assignsUser() throws Exception {
        final Issue.Smart issue = new Issue.Smart(MkIssueTest.issue());
        issue.assign("walter");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            issue.assignee().login(),
            Matchers.startsWith("wal")
        );
    }

    /**
     * MkIssue can create a closed event when closing an issue.
     * @throws Exception If some problem inside
     */
    @Test
    void createsClosedEvent() throws Exception {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkIssueTest.closed().events(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkIssue can type the event of closing an issue.
     * @throws Exception If some problem inside
     */
    @Test
    void typesClosedEvent() throws Exception {
        MatcherAssert.assertThat(
            "Event of closing has a wrong type",
            new Event.Smart(
                MkIssueTest.closed().events().iterator().next()
            ).type(),
            Matchers.equalTo(Event.CLOSED)
        );
    }

    /**
     * MkIssue can create a reopened event when reopening an issue.
     * @throws Exception If some problem inside
     */
    @Test
    void createsReopenedEvent() throws Exception {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkIssueTest.reopened().events(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkIssue can keep the event of closing after reopening.
     * @throws Exception If some problem inside
     */
    @Test
    void keepsClosedEventAfterReopening() throws Exception {
        MatcherAssert.assertThat(
            "Event of closing has a wrong type",
            new Event.Smart(
                MkIssueTest.reopened().events().iterator().next()
            ).type(),
            Matchers.equalTo(Event.CLOSED)
        );
    }

    /**
     * MkIssue can type the event of reopening an issue.
     * @throws Exception If some problem inside
     */
    @Test
    void typesReopenedEvent() throws Exception {
        final Iterator<Event> events =
            MkIssueTest.reopened().events().iterator();
        events.next();
        MatcherAssert.assertThat(
            "Event of reopening has a wrong type",
            new Event.Smart(events.next()).type(),
            Matchers.equalTo(Event.REOPENED)
        );
    }

    /**
     * Create an issue to work with.
     * @return Issue just created
     * @throws IOException If some problem inside
     */
    private static Issue issue() throws IOException {
        return new MkGitHub().randomRepo()
            .issues().create("hey", "how are you?");
    }

    /**
     * Create an issue with the given number.
     * @param number Number of the issue
     * @return Issue just created
     * @throws IOException If some problem inside
     */
    private static MkIssue issue(final int number) throws IOException {
        return new MkIssue(
            new MkStorage.InFile(),
            String.format("login-%d", number),
            Mockito.mock(Coordinates.class),
            number
        );
    }

    /**
     * Create a closed issue.
     * @return Closed issue
     * @throws IOException If some problem inside
     */
    private static Issue.Smart closed() throws IOException {
        final Issue.Smart issue = new Issue.Smart(MkIssueTest.issue());
        issue.close();
        return issue;
    }

    /**
     * Create a closed and then reopened issue.
     * @return Reopened issue
     * @throws IOException If some problem inside
     */
    private static Issue.Smart reopened() throws IOException {
        final Issue.Smart issue = MkIssueTest.closed();
        issue.open();
        return issue;
    }
}
