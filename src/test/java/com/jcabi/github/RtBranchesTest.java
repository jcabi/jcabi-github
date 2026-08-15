/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtBranches}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtBranchesTest {

    /**
     * Name of the first branch.
     */
    private static final String FIRST_NAME = "first";

    /**
     * Commit SHA of the first branch.
     */
    private static final String FIRST_SHA =
        "a971b1aca044105897297b87b0b0983a54dd5817";

    /**
     * Name of the second branch.
     */
    private static final String SECOND_NAME = "second";

    /**
     * Commit SHA of the second branch.
     */
    private static final String SECOND_SHA =
        "5d8dc2acf9c95d0d4e8881eebe04c2f0cbb249ff";

    @Test
    void iteratesOverBranches() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtBranchesTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                RtBranchesTest.branches(container).iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    @Test
    void iteratesOverNameOfFirstBranch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtBranchesTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "First branch has a wrong name",
                RtBranchesTest.branches(container)
                    .iterate().iterator().next().name(),
                Matchers.equalTo(RtBranchesTest.FIRST_NAME)
            );
        }
    }

    @Test
    void iteratesOverCommitOfFirstBranch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtBranchesTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "First branch has a wrong commit",
                RtBranchesTest.branches(container)
                    .iterate().iterator().next().commit().sha(),
                Matchers.equalTo(RtBranchesTest.FIRST_SHA)
            );
        }
    }

    @Test
    void iteratesOverNameOfSecondBranch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtBranchesTest.answer())
                .start(RandomPort.port())
        ) {
            final Iterator<Branch> iter =
                RtBranchesTest.branches(container).iterate().iterator();
            iter.next();
            MatcherAssert.assertThat(
                "Second branch has a wrong name",
                iter.next().name(),
                Matchers.equalTo(RtBranchesTest.SECOND_NAME)
            );
        }
    }

    @Test
    void iteratesOverCommitOfSecondBranch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtBranchesTest.answer())
                .start(RandomPort.port())
        ) {
            final Iterator<Branch> iter =
                RtBranchesTest.branches(container).iterate().iterator();
            iter.next();
            MatcherAssert.assertThat(
                "Second branch has a wrong commit",
                iter.next().commit().sha(),
                Matchers.equalTo(RtBranchesTest.SECOND_SHA)
            );
        }
    }

    @Test
    void findBranch() throws IOException {
        final String fourthname = "fourth";
        final String fourthsha = "d0d4e8881eebe04c5d8dc2acf9c952f0cbb249ff";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder().add(
                RtBranchesTest.branch(
                    "third",
                    "297b87b0b0983a54dd5817a971b1aca044105897"
                )
            ).add(RtBranchesTest.branch(fourthname, fourthsha))
                .build().toString()
        );
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(answer)
                .next(answer)
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "could not find branch correctly",
                RtBranchesTest.branches(container)
                    .find(fourthname).commit().sha(),
                new IsEqual<>(fourthsha)
            );
        }
    }

    /**
     * RtBranches can fetch its repository.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Branches belong to a wrong repo",
            new RtBranches(new FakeRequest(), repo).repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * Branches served by the given container.
     * @param container Container to serve the branches
     * @return Branches
     * @throws IOException If there is any I/O problem
     */
    private static RtBranches branches(final MkContainer container)
        throws IOException {
        return new RtBranches(
            new JdkRequest(container.home()),
            new MkGitHub().randomRepo()
        );
    }

    /**
     * Answer with two branches.
     * @return Answer
     */
    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder().add(
                RtBranchesTest.branch(
                    RtBranchesTest.FIRST_NAME,
                    RtBranchesTest.FIRST_SHA
                )
            ).add(
                RtBranchesTest.branch(
                    RtBranchesTest.SECOND_NAME,
                    RtBranchesTest.SECOND_SHA
                )
            ).build().toString()
        );
    }

    /**
     * Create and return JsonObject to test.
     * @param name Name of the branch
     * @param sha Commit SHA of the branch
     * @return JsonObject
     */
    private static JsonObject branch(final String name, final String sha) {
        return Json.createObjectBuilder()
            .add("name", name).add(
                "commit",
                Json.createObjectBuilder()
                    .add("sha", sha).add(
                        "url",
                        String.format(
                            "https://api.jcabi-github.invalid/repos/user/repo/commits/%s",
                            sha
                        )
                    )
            )
            .build();
    }
}
