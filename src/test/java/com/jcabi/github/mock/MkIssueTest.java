/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkIssue}.
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
            new Issue.Smart(issue).isOpen(),
            Matchers.is(true)
        );
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
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
        MatcherAssert.assertThat(issue.createdAt(), Matchers.notNullValue());
        MatcherAssert.assertThat(issue.updatedAt(), Matchers.notNullValue());
        MatcherAssert.assertThat(issue.htmlUrl(), Matchers.notNullValue());
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
            new Issue.Smart(issue).roLabels().iterate(),
            Matchers.<Label>hasItem(
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
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
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
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkIssue can remember it's author.
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canRememberItsAuthor() throws Exception {
        final MkGithub first = new MkGithub("first");
        final Github second = first.relogin("second");
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
        MatcherAssert.assertThat(this.issue().exists(), Matchers.is(true));
    }

    /**
     * MkIssue.exists() return false on nonexistent issues.
     * @throws Exception if any error occurs.
     */
    @Test
    public void canCheckNonExistentIssue() throws Exception {
        MatcherAssert.assertThat(
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
            issue.events(),
            Matchers.<Event>iterableWithSize(1)
        );
        final Event.Smart closed = new Event.Smart(
            issue.events().iterator().next()
        );
        MatcherAssert.assertThat(
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
            issue.events(),
            Matchers.<Event>iterableWithSize(2)
        );
        final Iterator<Event> events = issue.events().iterator();
        final Event.Smart closed = new Event.Smart(events.next());
        final Event.Smart reopened = new Event.Smart(events.next());
        MatcherAssert.assertThat(
            closed.type(),
            Matchers.equalTo(Event.CLOSED)
        );
        MatcherAssert.assertThat(
            reopened.type(),
            Matchers.equalTo(Event.REOPENED)
        );
    }

    /**
     * Create an issue to work with.
     * @return Issue just created
     * @throws Exception If some problem inside
     */
    private Issue issue() throws Exception {
        return new MkGithub().randomRepo()
            .issues().create("hey", "how are you?");
    }

}
