/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import com.jcabi.log.Logger;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration case for {@link Issue}.
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtIssueITCase {
    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = new RepoRule().repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtIssue can talk in github. This test is ignored because of bug
     * https://github.com/jcabi/jcabi-github/issues/1178.
     * @throws Exception If some problem inside
     */
    @Ignore
    @Test
    public void talksInGithubProject() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            issue.comments().iterate(new Date(0L)),
            Matchers.<Comment>iterableWithSize(1)
        );
        final User.Smart author = new User.Smart(
            new Comment.Smart(comment)
                .author()
        );
        final User.Smart self = new User.Smart(
            issue.repo().github().users().self()
        );
        if (author.hasName() && self.hasName()) {
            MatcherAssert.assertThat(
                author.name(),
                Matchers.equalTo(
                    self.name()
                )
            );
        }
        comment.remove();
    }

    /**
     * RtIssue can change title and body.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesTitleAndBody() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).title("test one more time");
        MatcherAssert.assertThat(
            new Issue.Smart(issue).title(),
            Matchers.startsWith("test o")
        );
        new Issue.Smart(issue).body("some new body of the issue");
        MatcherAssert.assertThat(
            new Issue.Smart(issue).body(),
            Matchers.startsWith("some new ")
        );
    }

    /**
     * RtIssue can change issue state.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesIssueState() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            new Issue.Smart(issue).isOpen(),
            Matchers.is(false)
        );
        new Issue.Smart(issue).open();
        MatcherAssert.assertThat(
            new Issue.Smart(issue).isOpen(),
            Matchers.is(true)
        );
    }

    /**
     * RtIssue can fetch assignee.
     *
     * <p> If you get AssertionError during this test execution and test was
     *  ignored it means that something happened with account that you try to
     *  edit with Issue.assign(). We had this problem when our account was
     *  flagged as suspicious by Github. In this case you should contact Github
     *  support and ask them to unblock account you use.
     *
     * @see <a href="https://github.com/jcabi/jcabi-github/issues/810">Why test is ignored?</a>
     * @throws Exception if any problem inside.
     */
    @Test
    public void identifyAssignee() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        final String login = issue.repo().github().users().self().login();
        try {
            new Issue.Smart(issue).assign(login);
        } catch (final AssertionError error) {
            Logger.warn(this, "Test failed with error: %s", error.getMessage());
            Assume.assumeFalse(
                "Something wrong with your test account. Read test's java-doc.",
                true
            );
        }
        final User assignee = new Issue.Smart(issue).assignee();
        MatcherAssert.assertThat(
            assignee.login(),
            Matchers.equalTo(login)
        );
    }
    /**
     * RtIssue can check whether it is a pull request.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksForPullRequest() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        MatcherAssert.assertThat(
            new Issue.Smart(issue).isPull(),
            Matchers.is(false)
        );
    }

    /**
     * GhIssue can list issue events.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsIssueEvents() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            new Event.Smart(issue.events().iterator().next()).type(),
            Matchers.equalTo(Event.CLOSED)
        );
    }

    /**
     * Issue.Smart can find the latest event.
     * @throws Exception If some problem inside
     */
    @Test
    public void findsLatestEvent() throws Exception {
        final Issue.Smart issue = new Issue.Smart(RtIssueITCase.issue());
        issue.close();
        MatcherAssert.assertThat(
            new Event.Smart(
                new Issue.Smart(issue).latestEvent(Event.CLOSED)
            ).author().login(),
            Matchers.equalTo(issue.author().login())
        );
    }

    /**
     * RtIssue always exists in Github.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void issueAlwaysExistsInGithub() throws Exception {
        MatcherAssert.assertThat(
            new Issue.Smart(RtIssueITCase.issue()).exists(), Matchers.is(true)
        );
    }

    /**
     * RtIssue can lock conversation.
     * @throws Exception If some problem inside
     */
    @Test
    public void locks() throws Exception {
        final Issue issue = new Issue.Smart(RtIssueITCase.issue());
        issue.lock("off-topic");
        MatcherAssert.assertThat(
            issue.isLocked(),
            new IsEqual<>(true)
        );
    }

    /**
     * RtIssue can unlock conversation.
     * @throws Exception If some problem inside
     */
    @Test
    public void unlocks() throws Exception {
        final Issue issue = new Issue.Smart(RtIssueITCase.issue());
        issue.lock("too heated");
        issue.unlock();
        MatcherAssert.assertThat(
            issue.isLocked(),
            new IsEqual<>(false)
        );
    }

    /**
     * Create and return issue to test.
     * @return Issue
     * @throws Exception If some problem inside
     */
    private static Issue issue() throws Exception {
        return repo.issues().create("test issue title", "test issue body");
    }

}
