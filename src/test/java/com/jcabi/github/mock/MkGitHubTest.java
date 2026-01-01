/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Comment;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.User;
import com.jcabi.immutable.ArrayMap;
import com.jcabi.log.VerboseCallable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGitHub}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class MkGitHubTest {
    /**
     * Settings to use when creating temporary repos.
     */
    private static final Repos.RepoCreate NEW_REPO_SETTINGS =
        new Repos.RepoCreate(
            "test",
            false
        );

    @Test
    void worksWithMockedData() throws IOException {
        final Repo repo = new MkGitHub().repos().create(MkGitHubTest.NEW_REPO_SETTINGS);
        final Issue issue = repo.issues().create("hey", "how are you?");
        final Comment comment = issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hey, ")
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().get(issue.number()).comments().iterate(new Date(0L)),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.equalTo(
                new User.Smart(repo.github().users().self()).login()
            )
        );
    }

    @Test
    void canRelogin() throws IOException {
        final String login = "mark";
        final MkGitHub github = new MkGitHub();
        final Repo repo = github.repos().create(MkGitHubTest.NEW_REPO_SETTINGS);
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
            "Values are not equal",
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.not(
                Matchers.equalTo(
                    new User.Smart(repo.github().users().self()).login()
                )
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new User.Smart(new Comment.Smart(comment).author()).login(),
            Matchers.equalTo(login)
        );
    }

    @Test
    void retrievesMarkdown() throws IOException {
        final GitHub github = new MkGitHub();
        MatcherAssert.assertThat(
            "Value is null",
            github.markdown(),
            Matchers.notNullValue()
        );
    }

    @Test
    void canCreateRandomRepo() throws IOException {
        final MkGitHub github = new MkGitHub();
        final Repo repo = github.randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            github.repos().get(repo.coordinates()).coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void canHandleMultipleThreads() throws IOException, InterruptedException {
        final Repo repo = new MkGitHub().randomRepo();
        final Callable<Void> task = new VerboseCallable<>(
            () -> {
                repo.issues().create("", "");
                return null;
            }
        );
        final int threads = 100;
        final Collection<Callable<Void>> tasks =
            new ArrayList<>(threads);
        for (int idx = 0; idx < threads; ++idx) {
            tasks.add(task);
        }
        final ExecutorService svc = Executors.newFixedThreadPool(threads);
        svc.invokeAll(tasks);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(threads)
        );
    }

    @Test
    void canRetrieveUsers() throws IOException {
        MatcherAssert.assertThat(
            "Retrieved inexistent user",
            new User.Smart(
                new MkGitHub().users().get("other")
            ).exists(),
            new IsEqual<>(false)
        );
    }
}
