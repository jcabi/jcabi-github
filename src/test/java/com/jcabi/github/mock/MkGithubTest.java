/**
 * Copyright (c) 2013-2022, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Comment;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.User;
import com.jcabi.immutable.ArrayMap;
import com.jcabi.log.VerboseCallable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link MkGithub}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class MkGithubTest {
    /**
     * Settings to use when creating temporary repos.
     */
    private static final Repos.RepoCreate NEW_REPO_SETTINGS =
        new Repos.RepoCreate(
            "test",
            false
        );

    /**
     * MkGithub can work.
     * @throws Exception If some problem inside
     */
    @Test
    public void worksWithMockedData() throws Exception {
        final Repo repo = new MkGithub().repos().create(NEW_REPO_SETTINGS);
        final Issue issue = repo.issues().create("hey", "how are you?");
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            repo.issues().get(issue.number()).comments().iterate(new Date(0L)),
            Matchers.<Comment>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.equalTo(
                new User.Smart(repo.github().users().self()).login()
            )
        );
    }

    /**
     * MkGithub can relogin.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canRelogin() throws Exception {
        final String login = "mark";
        final MkGithub github = new MkGithub();
        final Repo repo = github.repos().create(NEW_REPO_SETTINGS);
        final Issue issue = repo.issues().create("title", "Found a bug");
        final Comment comment = github
            .relogin(login)
            .repos()
            .get(repo.coordinates())
            .issues()
            .get(issue.number())
            .comments()
            .post("Nice change");
        MatcherAssert.assertThat(
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.not(
                Matchers.equalTo(
                    new User.Smart(repo.github().users().self()).login()
                )
            )
        );
        MatcherAssert.assertThat(
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * MkGithub can retrieve the markdown.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void retrievesMarkdown() throws Exception {
        final Github github = new MkGithub();
        MatcherAssert.assertThat(
            github.markdown(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkGithub can create random repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateRandomRepo() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.randomRepo();
        MatcherAssert.assertThat(
            github.repos().get(repo.coordinates()).coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * MkGithub can handle multiple threads in parallel.
     * @throws Exception if some problem inside
     */
    @Test
    public void canHandleMultipleThreads() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final int threads = Tv.HUNDRED;
        final ExecutorService svc = Executors.newFixedThreadPool(threads);
        final Callable<Void> task = new VerboseCallable<Void>(
            new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    repo.issues().create("", "");
                    return null;
                }
            }
        );
        final Collection<Callable<Void>> tasks =
            new ArrayList<Callable<Void>>(threads);
        for (int idx = 0; idx < threads; ++idx) {
            tasks.add(task);
        }
        svc.invokeAll(tasks);
        MatcherAssert.assertThat(
            repo.issues().iterate(new ArrayMap<String, String>()),
            Matchers.<Issue>iterableWithSize(threads)
        );
    }

    /**
     * Can retrieve users.
     * @throws Exception If something goes wrong
     */
    @Test
    public void canRetrieveUsers() throws Exception {
        MatcherAssert.assertThat(
            "Retrieved inexistent user",
            new User.Smart(
                new MkGithub().users().get("other")
            ).exists(),
            new IsEqual<Boolean>(false)
        );
    }
}
