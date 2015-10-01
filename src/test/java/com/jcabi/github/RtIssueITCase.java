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
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import com.jcabi.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration case for {@link Issue}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
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
     * RtIssue can talk in github.
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
            issue.comments().iterate(),
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
     * Create and return issue to test.
     * @return Issue
     * @throws Exception If some problem inside
     */
    private static Issue issue() throws Exception {
        return repo.issues().create("test issue title", "test issue body");
    }

}
