/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Comment;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.User;
import com.jcabi.immutable.ArrayMap;
import com.jcabi.log.VerboseCallable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * Login of the user to re-login as.
     */
    private static final String LOGIN = "mark";

    @Test
    void worksWithMockedData() throws IOException {
        MatcherAssert.assertThat(
            "Comment has a wrong body",
            new Comment.Smart(
                MkGitHubTest.comment(new MkGitHub())
            ).body(),
            Matchers.startsWith("hey, ")
        );
    }

    @Test
    void countsMockedComments() throws IOException {
        final Repo repo = new MkGitHub().repos()
            .create(MkGitHubTest.NEW_REPO_SETTINGS);
        final Issue issue = repo.issues().create("hey", "how are you?");
        issue.comments().post("hey, works?");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.issues().get(issue.number()).comments().iterate(Instant.EPOCH),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void signsMockedComments() throws IOException {
        final MkGitHub github = new MkGitHub();
        MatcherAssert.assertThat(
            "Comment has a wrong author",
            new User.Smart(
                new Comment.Smart(MkGitHubTest.comment(github)).author()
            ).login(),
            Matchers.equalTo(new User.Smart(github.users().self()).login())
        );
    }

    @Test
    void canRelogin() throws IOException {
        final MkGitHub github = new MkGitHub();
        MatcherAssert.assertThat(
            "Comment is signed by the original user",
            new User.Smart(
                new Comment.Smart(MkGitHubTest.relogged(github)).author()
            ).login(),
            Matchers.not(
                Matchers.equalTo(
                    new User.Smart(github.users().self()).login()
                )
            )
        );
    }

    @Test
    void signsCommentsWithNewLogin() throws IOException {
        MatcherAssert.assertThat(
            "Comment is not signed by the new user",
            new User.Smart(
                new Comment.Smart(
                    MkGitHubTest.relogged(new MkGitHub())
                ).author()
            ).login(),
            Matchers.equalTo(MkGitHubTest.LOGIN)
        );
    }

    @Test
    void retrievesMarkdown() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().markdown(),
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

    /**
     * A comment posted to a fresh issue of a fresh repo.
     * @param github GitHub to post the comment to
     * @return Posted comment
     * @throws IOException If some problem inside
     */
    private static Comment comment(final MkGitHub github) throws IOException {
        return github.repos().create(MkGitHubTest.NEW_REPO_SETTINGS)
            .issues().create("hey", "how are you?")
            .comments().post("hey, works?");
    }

    /**
     * A comment posted by a re-logged user.
     * @param github GitHub to post the comment to
     * @return Posted comment
     * @throws IOException If some problem inside
     */
    private static Comment relogged(final MkGitHub github) throws IOException {
        final Repo repo = github.repos()
            .create(MkGitHubTest.NEW_REPO_SETTINGS);
        return github
            .relogin(MkGitHubTest.LOGIN)
            .repos()
            .get(repo.coordinates())
            .issues()
            .get(repo.issues().create("title", "Found a bug").number())
            .comments()
            .post("Nice change");
    }
}
