/**
 * Copyright (c) 2012-2013, JCabi.com
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

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Github}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
public final class GithubITCase {

    /**
     * Github OAuth key.
     */
    private static final String KEY =
        System.getProperty("failsafe.github.key");

    /**
     * Github test repository.
     */
    private static final String REPO =
        System.getProperty("failsafe.github.repo");

    /**
     * Github.Simple can talk in github.
     * @throws Exception If some problem inside
     */
    @Test
    public void talksInGithubProject() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        final Issue issue = repo.issues().create("something", "just a test");
        MatcherAssert.assertThat(
            new Issue.Tool(issue).title(),
            Matchers.startsWith("someth")
        );
        MatcherAssert.assertThat(
            new Issue.Tool(issue).body(),
            Matchers.startsWith("just a ")
        );
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            new Comment.Tool(comment).body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).comments().iterate(),
            Matchers.<Comment>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            new User.Tool(comment.author()).name(),
            Matchers.equalTo(new User.Tool(github.self()).name())
        );
        comment.remove();
    }

    /**
     * Github.Simple can iterate issues.
     * @throws Exception If some problem inside
     */
    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void iteratesIssues() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        for (final Issue issue : repo.issues().iterate()) {
            MatcherAssert.assertThat(
                new Issue.Tool(issue).title(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * Github.Simple can add and remove issue labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndRemovesIssueLabels() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        final Issue issue = repo.issues().create("another test", "a test");
        final Label label = new Label.Simple("first");
        issue.labels().add(Collections.singletonList(label));
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).labels().iterate(),
            Matchers.<Label>iterableWithSize(1)
        );
        issue.labels().remove(label.name());
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).labels().iterate(),
            Matchers.<Label>emptyIterable()
        );
        issue.labels().clear();
    }

    /**
     * Github.Simple can change title and body.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesTitleAndBody() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        final Issue issue = repo.issues().create("alpha", "beta");
        new Issue.Tool(issue).title("test one more time");
        MatcherAssert.assertThat(
            new Issue.Tool(issue).title(),
            Matchers.startsWith("test o")
        );
        new Issue.Tool(issue).body("some new body of the issue");
        MatcherAssert.assertThat(
            new Issue.Tool(issue).body(),
            Matchers.startsWith("some new ")
        );
    }

    /**
     * Github.Simple can change issue state.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesIssueState() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        final Issue issue = repo.issues().create("test issue no.2", "data 2");
        new Issue.Tool(issue).close();
        MatcherAssert.assertThat(
            new Issue.Tool(issue).isOpen(),
            Matchers.is(false)
        );
        new Issue.Tool(issue).open();
        MatcherAssert.assertThat(
            new Issue.Tool(issue).isOpen(),
            Matchers.is(true)
        );
    }

    /**
     * Github.Simple can read and write Gists.
     * @throws Exception If some problem inside
     */
    @Test
    public void readsAndWritesGists() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Gist gist = github.gists().iterate().iterator().next();
        final String file = new Gist.Tool(gist).files().iterator().next();
        gist.write(file, "hey, works for you this way?");
        MatcherAssert.assertThat(
            gist.read(file),
            Matchers.startsWith("hey, works for ")
        );
    }

    /**
     * Github.Simple can understand who am I.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksWhoAmI() throws Exception {
        Assume.assumeThat(GithubITCase.KEY, Matchers.notNullValue());
        final Github github = new Github.Simple(GithubITCase.KEY);
        final User self = github.self();
        MatcherAssert.assertThat(
            self.login(),
            Matchers.not(Matchers.isEmptyString())
        );
    }

}
