/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkIssue}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MkIssueTest {

    /**
     * MkIssue can open and close.
     * @throws Exception If some problem inside
     */
    @Test
    public void opensAndCloses() throws Exception {
        final Issue issue = this.issue();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isOpen(),
            Matchers.is(true)
        );
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isOpen(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can point to an absent pull request.
     * @throws Exception If some problem inside
     */
    @Test
    public void pointsToAnEmptyPullRequest() throws Exception {
        final Issue issue = this.issue();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(false)
        );
    }

    /**
     * MkIssue can show an issue author.
     * @throws Exception If some problem inside
     */
    @Test
    public void showsIssueAuthor() throws Exception {
        final Issue issue = this.issue();
        MatcherAssert.assertThat(
            "Value is null",
            new Issue.Smart(issue).author().login(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkIssue can change title.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesTitle() throws Exception {
        final Issue issue = this.issue();
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
    public void changesBody() throws Exception {
        final Issue issue = this.issue();
        new Issue.Smart(issue).body("hey, body works?");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Issue.Smart(issue).body(),
            Matchers.startsWith("hey, b")
        );
    }

    /**
     * MkIssue can expose all properties.
     * @throws Exception If some problem inside
     */
    @Test
    public void exponsesProperties() throws Exception {
        final Issue.Smart issue = new Issue.Smart(this.issue());
        MatcherAssert.assertThat(
            "Value is null",issue.createdAt(), Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Value is null",issue.updatedAt(), Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Value is null",issue.htmlUrl(), Matchers.notNullValue());
    }

    /**
     * MkIssue can list its labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsReadOnlyLabels() throws Exception {
        final Issue issue = this.issue();
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

    /**
     * MkIssue should be able to compare different instances.
     */
    @Test
    public void canCompareInstances() throws IOException {
        final MkIssue less = new MkIssue(
            new MkStorage.InFile(),
            "login-less",
            Mockito.mock(Coordinates.class),
            1
        );
        final MkIssue greater = new MkIssue(
            new MkStorage.InFile(),
            "login-greater",
            Mockito.mock(Coordinates.class),
            2
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkIssue can remember it's author.
     */
    @Test
    public void canRememberItsAuthor() throws IOException {
        final MkGitHub first = new MkGitHub("first");
        final GitHub second = first.relogin("second");
        final Repo repo = first.randomRepo();
        final int number = second.repos()
            .get(repo.coordinates())
            .issues()
            .create("", "")
            .number();
        final Issue issue = first.repos()
            .get(repo.coordinates())
            .issues()
            .get(number);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).author().login(),
            Matchers.is("second")
        );
    }

    /**
     * Can check if issue exists.
     * @throws Exception if any error occurs.
     */
    @Test
    public void canCheckIfIssueExists() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",this.issue().exists(), Matchers.is(true));
    }

    /**
     * MkIssue.exists() return false on nonexistent issues.
     */
    @Test
    public void canCheckNonExistentIssue() throws IOException {
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
    public void assignsUser() throws Exception {
        final Issue.Smart issue = new Issue.Smart(this.issue());
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
    public void createsClosedEvent() throws Exception {
        final Issue.Smart issue = new Issue.Smart(this.issue());
        issue.close();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.events(),
            Matchers.iterableWithSize(1)
        );
        final Event.Smart closed = new Event.Smart(
            issue.events().iterator().next()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            closed.type(),
            Matchers.equalTo(Event.CLOSED)
        );
    }

    /**
     * MkIssue can create a reopened event when closing an issue.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsReopenedEvent() throws Exception {
        final Issue.Smart issue = new Issue.Smart(this.issue());
        issue.close();
        issue.open();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.events(),
            Matchers.iterableWithSize(2)
        );
        final Iterator<Event> events = issue.events().iterator();
        final Event.Smart closed = new Event.Smart(events.next());
        final Event.Smart reopened = new Event.Smart(events.next());
        MatcherAssert.assertThat(
            "Values are not equal",
            closed.type(),
            Matchers.equalTo(Event.CLOSED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            reopened.type(),
            Matchers.equalTo(Event.REOPENED)
        );
    }

    /**
     * Create an issue to work with.
     * @return Issue just created
     */
    private Issue issue() throws IOException {
        return new MkGitHub().randomRepo()
            .issues().create("hey", "how are you?");
    }

}
