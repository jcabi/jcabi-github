/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link Issue}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
final class RtIssueITCase {
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
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtIssueITCase.repos = github.repos();
        RtIssueITCase.repo = new RepoRule().repo(RtIssueITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtIssueITCase.repos != null && RtIssueITCase.repo != null) {
            RtIssueITCase.repos.remove(RtIssueITCase.repo.coordinates());
        }
    }

    /**
     * RtIssue can talk in github. This test is ignored because of bug
     * https://github.com/jcabi/jcabi-github/issues/1178.
     * @throws Exception If some problem inside
     */
    @Disabled
    @Test
    void talksInGitHubProject() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            issue.comments().iterate(new Date(0L)),
            Matchers.iterableWithSize(1)
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
                "Values are not equal",
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
    void changesTitleAndBody() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).title("test one more time");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Issue.Smart(issue).title(),
            Matchers.startsWith("test o")
        );
        new Issue.Smart(issue).body("some new body of the issue");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Issue.Smart(issue).body(),
            Matchers.startsWith("some new ")
        );
    }

    /**
     * RtIssue can change issue state.
     * @throws Exception If some problem inside
     */
    @Test
    void changesIssueState() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isOpen(),
            Matchers.is(false)
        );
        new Issue.Smart(issue).open();
        MatcherAssert.assertThat(
            "Values are not equal",
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
     *  flagged as suspicious by GitHub. In this case you should contact GitHub
     *  support and ask them to unblock account you use.
     * @throws Exception if any problem inside.
     * @see <a href="https://github.com/jcabi/jcabi-github/issues/810">Why test is ignored?</a>
     */
    @Test
    void identifyAssignee() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        final String login = issue.repo().github().users().self().login();
        try {
            new Issue.Smart(issue).assign(login);
        } catch (final AssertionError error) {
            Logger.warn(this, "Test failed with error: %s", error.getMessage());
            Assumptions.assumeFalse(
                true,
                "Something wrong with your test account. Read test's java-doc."
            );
        }
        final User assignee = new Issue.Smart(issue).assignee();
        MatcherAssert.assertThat(
            "Values are not equal",
            assignee.login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * RtIssue can check whether it is a pull request.
     * @throws Exception If some problem inside
     */
    @Test
    void checksForPullRequest() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(issue).isPull(),
            Matchers.is(false)
        );
    }

    /**
     * GhIssue can list issue events.
     * @throws Exception If some problem inside
     */
    @Test
    void listsIssueEvents() throws Exception {
        final Issue issue = RtIssueITCase.issue();
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Event.Smart(issue.events().iterator().next()).type(),
            Matchers.equalTo(Event.CLOSED)
        );
    }

    /**
     * Issue.Smart can find the latest event.
     * @throws Exception If some problem inside
     */
    @Test
    void findsLatestEvent() throws Exception {
        final Issue.Smart issue = new Issue.Smart(RtIssueITCase.issue());
        issue.close();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Event.Smart(
                new Issue.Smart(issue).latestEvent(Event.CLOSED)
            ).author().login(),
            Matchers.equalTo(issue.author().login())
        );
    }

    /**
     * RtIssue always exists in GitHub.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    void issueAlwaysExistsInGitHub() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Issue.Smart(RtIssueITCase.issue()).exists(), Matchers.is(true)
        );
    }

    /**
     * RtIssue can lock conversation.
     * @throws Exception If some problem inside
     */
    @Test
    void locks() throws Exception {
        final Issue issue = new Issue.Smart(RtIssueITCase.issue());
        issue.lock("off-topic");
        MatcherAssert.assertThat(
            "Assertion failed",
            issue.isLocked(),
            new IsEqual<>(true)
        );
    }

    /**
     * RtIssue can unlock conversation.
     * @throws Exception If some problem inside
     */
    @Test
    void unlocks() throws Exception {
        final Issue issue = new Issue.Smart(RtIssueITCase.issue());
        issue.lock("too heated");
        issue.unlock();
        MatcherAssert.assertThat(
            "Assertion failed",
            issue.isLocked(),
            new IsEqual<>(false)
        );
    }

    /**
     * Create and return issue to test.
     * @return Issue
     */
    private static Issue issue() throws IOException {
        return RtIssueITCase.repo.issues().create("test issue title", "test issue body");
    }

}
