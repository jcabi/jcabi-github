/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkRepoCommit}.
 * @since 0.8
 */
final class MkRepoCommitTest {

    /**
     * The fist test key.
     */
    private static final String SHA1 =
        "6dcb09b5b57875f334f61aebed695e2e4193db5e";

    /**
     * The second test key.
     */
    private static final String SHA2 =
        "51cabb8e759852a6a40a7a2a76ef0afd4beef96d";

    /**
     * MkRepoCommit can return repository.
     * @throws IOException If some problem inside
     */
    @Test
    void fetchesRepo() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = MkRepoCommitTest.repo(storage);
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkRepoCommit(
                storage, repo, MkRepoCommitTest.SHA1
            ).repo(), Matchers.equalTo(repo)
        );
    }

    /**
     * MkRepoCommit can return sha.
     * @throws IOException If some problem inside
     */
    @Test
    void fetchesSha() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkRepoCommit(storage, MkRepoCommitTest.repo(storage), MkRepoCommitTest.SHA2).sha(),
            Matchers.equalTo(MkRepoCommitTest.SHA2)
        );
    }

    @Test
    void comparesSmallerCommit() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            "Smaller commit is not smaller",
            MkRepoCommitTest.commit(storage, 1)
                .compareTo(MkRepoCommitTest.commit(storage, 2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerCommit() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            "Bigger commit is not bigger",
            MkRepoCommitTest.commit(storage, 2)
                .compareTo(MkRepoCommitTest.commit(storage, 1)),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void canGetJson() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github").add("repos")
                .add("repo").attr("coords", "test_login/test_repo")
                .add("commits").add("commit").add("sha").set(MkRepoCommitTest.SHA1)
        );
        MatcherAssert.assertThat(
            "Value is null",
            new MkRepoCommit(
                storage, MkRepoCommitTest.repo(storage), MkRepoCommitTest.SHA1
            ).json(), Matchers.notNullValue()
        );
    }

    @Test
    void compareEqual() throws IOException {
        final String sha = "c2c53d66948214258a26ca9ca845d7ac0c17f8e7";
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = MkRepoCommitTest.repo(storage);
        MatcherAssert.assertThat(
            "Equal commits are not equal",
            new MkRepoCommit(storage, repo, sha)
                .compareTo(new MkRepoCommit(storage, repo, sha)),
            Matchers.equalTo(0)
        );
    }

    @Test
    void compareDifferent() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = MkRepoCommitTest.repo(storage);
        MatcherAssert.assertThat(
            "Different commits are equal",
            new MkRepoCommit(
                storage, repo, "6dcd4ce23d88e2ee9568ba546c007c63d9131c1b"
            ).compareTo(
                new MkRepoCommit(
                    storage, repo, "e9d71f5ee7c92d6dc9e92ffdad17b8bd49418f98"
                )
            ),
            Matchers.not(0)
        );
    }

    /**
     * Create repository for test.
     * @param storage The storage
     * @return Repo
     */
    private static Repo repo(final MkStorage storage) {
        final String login = "test_login";
        return new MkRepo(
            storage,
            login,
            new Coordinates.Simple(login, "test_repo")
        );
    }

    /**
     * Create a commit in its own repo.
     * @param storage The storage
     * @param number Number of the repo
     * @return Commit
     */
    private static MkRepoCommit commit(
        final MkStorage storage, final int number) {
        return new MkRepoCommit(
            storage,
            new MkRepo(
                storage,
                String.format("login%d", number),
                new Coordinates.Simple(
                    String.format("test_login%d", number),
                    String.format("test_repo%d", number)
                )
            ),
            MkRepoCommitTest.SHA1
        );
    }
}
