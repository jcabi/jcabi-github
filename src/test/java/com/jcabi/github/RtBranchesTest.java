/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtBranches}.
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtBranchesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtBranches can iterate over all branches.
     * @throws Exception if there is any error
     */
    @Test
    public void iteratesOverBranches() throws Exception {
        final String firstname = "first";
        final String firstsha = "a971b1aca044105897297b87b0b0983a54dd5817";
        final String secondname = "second";
        final String secondsha = "5d8dc2acf9c95d0d4e8881eebe04c2f0cbb249ff";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(branch(firstname, firstsha))
                .add(branch(secondname, secondsha))
                .build().toString()
        );
        try (
            final MkContainer container = new MkGrizzlyContainer()
                .next(answer)
                .next(answer)
                .start(this.resource.port())
        ) {
            final RtBranches branches = new RtBranches(
                new JdkRequest(container.home()),
                new MkGithub().randomRepo()
            );
            MatcherAssert.assertThat(
                branches.iterate(),
                Matchers.<Branch>iterableWithSize(2)
            );
            final Iterator<Branch> iter = branches.iterate().iterator();
            final Branch first = iter.next();
            MatcherAssert.assertThat(first.name(), Matchers.equalTo(firstname));
            MatcherAssert.assertThat(
                first.commit().sha(),
                Matchers.equalTo(firstsha)
            );
            final Branch second = iter.next();
            MatcherAssert.assertThat(
                second.name(),
                Matchers.equalTo(secondname)
            );
            MatcherAssert.assertThat(
                second.commit().sha(),
                Matchers.equalTo(secondsha)
            );
            container.stop();
        }
    }

    /**
     * RtBranches can find one branck by name.
     * @throws Exception if there is any error
     */
    @Test
    public void findBranch() throws Exception {
        final String thirdname = "third";
        final String thirdsha = "297b87b0b0983a54dd5817a971b1aca044105897";
        final String fourthname = "fourth";
        final String fourthsha = "d0d4e8881eebe04c5d8dc2acf9c952f0cbb249ff";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(branch(thirdname, thirdsha))
                .add(branch(fourthname, fourthsha))
                .build().toString()
        );
        try (
            final MkContainer container = new MkGrizzlyContainer()
                .next(answer)
                .next(answer)
                .start(this.resource.port());
        ) {
            final RtBranches branches = new RtBranches(
                new JdkRequest(container.home()),
                new MkGithub().randomRepo()
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
        final Repo repo = new MkGithub().randomRepo();
        final RtBranches branch = new RtBranches(new FakeRequest(), repo);
        final Coordinates coords = branch.repo().coordinates();
        MatcherAssert.assertThat(
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
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
                    // @checkstyle LineLengthCheck (1 line)
                    .add("url", String.format("https://api.jcabi-github.invalid/repos/user/repo/commits/%s", sha))
            )
            .build();
    }
}
