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
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).comments(),
            Matchers.<Comment>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            comment.author().name(),
            Matchers.equalTo(github.self().name())
        );
        comment.remove();
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
            repo.issues().get(issue.number()).labels(),
            Matchers.<Label>iterableWithSize(1)
        );
        issue.labels().remove(label.name());
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).labels(),
            Matchers.<Label>emptyIterable()
        );
        issue.labels().clear();
    }

    /**
     * Github.Simple can change title and body.
     * @throws Exception If some problem inside
     * @todo #1 The test doesn't work because PATCH method is not
     *  allowed in HttpURLConnection in Java. I don't know yet how
     *  to fix it the right way.
     */
    @Test
    @org.junit.Ignore
    public void changesTitleAndBody() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Repo repo = github.repo(GithubITCase.REPO);
        final Issue issue = repo.issues().create("alpha", "beta");
        issue.title("test one more time");
        MatcherAssert.assertThat(
            issue.title(),
            Matchers.startsWith("test o")
        );
        issue.body("some new body of the issue");
        MatcherAssert.assertThat(
            issue.body(),
            Matchers.startsWith("some new ")
        );
    }

    /**
     * Github.Simple can read Gists.
     * @throws Exception If some problem inside
     */
    @Test
    public void readsGists() throws Exception {
        if (GithubITCase.KEY == null) {
            return;
        }
        final Github github = new Github.Simple(GithubITCase.KEY);
        final Gist gist = github.gists().iterator().next();
        MatcherAssert.assertThat(
            gist.read(gist.files().iterator().next()),
            Matchers.notNullValue()
        );
    }

}
