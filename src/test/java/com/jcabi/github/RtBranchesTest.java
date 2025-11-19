/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtBranches}.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtBranchesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void iteratesOverBranches() throws IOException {
        final String firstname = "first";
        final String firstsha = "a971b1aca044105897297b87b0b0983a54dd5817";
        final String secondname = "second";
        final String secondsha = "5d8dc2acf9c95d0d4e8881eebe04c2f0cbb249ff";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(RtBranchesTest.branch(firstname, firstsha))
                .add(RtBranchesTest.branch(secondname, secondsha))
                .build().toString()
        );
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(answer)
                .next(answer)
                .start(this.resource.port())
        ) {
            final RtBranches branches = new RtBranches(
                new JdkRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                branches.iterate(),
                Matchers.iterableWithSize(2)
            );
            final Iterator<Branch> iter = branches.iterate().iterator();
            final Branch first = iter.next();
            MatcherAssert.assertThat(
                "Values are not equal", first.name(), Matchers.equalTo(firstname)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                first.commit().sha(),
                Matchers.equalTo(firstsha)
            );
            final Branch second = iter.next();
            MatcherAssert.assertThat(
                "Values are not equal",
                second.name(),
                Matchers.equalTo(secondname)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                second.commit().sha(),
                Matchers.equalTo(secondsha)
            );
            container.stop();
        }
    }

    @Test
    public void findBranch() throws IOException {
        final String thirdname = "third";
        final String thirdsha = "297b87b0b0983a54dd5817a971b1aca044105897";
        final String fourthname = "fourth";
        final String fourthsha = "d0d4e8881eebe04c5d8dc2acf9c952f0cbb249ff";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(RtBranchesTest.branch(thirdname, thirdsha))
                .add(RtBranchesTest.branch(fourthname, fourthsha))
                .build().toString()
        );
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(answer)
                .next(answer)
                .start(this.resource.port())
        ) {
            final RtBranches branches = new RtBranches(
                new JdkRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "could not find branch correctly",
                branches.find(fourthname).commit().sha(),
                new IsEqual<>(
                    fourthsha
                )
            );
            container.stop();
        }
    }

    /**
     * RtBranches can fetch its repository.
     * @throws IOException If there is any I/O problem
     */
    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final RtBranches branch = new RtBranches(new FakeRequest(), repo);
        final Coordinates coords = branch.repo().coordinates();
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.repo(),
            Matchers.equalTo(repo.coordinates().repo())
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
            .add("name", name)
            .add(
                "commit",
                Json.createObjectBuilder()
                    .add("sha", sha)
                    .add(
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
