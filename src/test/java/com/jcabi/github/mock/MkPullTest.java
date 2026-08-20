/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkPull}.
 * @since 0.7
 */
final class MkPullTest {

    /**
     * Login of test user.
     */
    private static final String USERNAME = "patrick";

    /**
     * Base branch name.
     */
    private static final String BASE = "my-base-branch";

    /**
     * Head branch name.
     */
    private static final String HEAD = "my-head-branch";

    @Test
    void comparesSmallerPull() throws IOException {
        MatcherAssert.assertThat(
            "Smaller pull request is not smaller",
            MkPullTest.pull(1).compareTo(MkPullTest.pull(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerPull() throws IOException {
        MatcherAssert.assertThat(
            "Bigger pull request is not bigger",
            MkPullTest.pull(2).compareTo(MkPullTest.pull(1)),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkPull can get comments number if no comments.
     * @throws Exception when a problem occurs.
     */
    @Test
    void canGetCommentsNumberIfZero() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            MkPullTest.pullRequest().json().getInt("comments"),
            Matchers.is(0)
        );
    }

    /**
     * MkPull can get comments number if some comments exist.
     * @throws Exception when a problem occurs.
     */
    @Test
    void canGetCommentsNumberIfNonZero() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        pull.comments().post("comment1", "path1", "how are you?", 1);
        pull.comments().post("comment2", "path2", "how are you2?", 2);
        MatcherAssert.assertThat(
            "Values are not equal",
            pull.json().getInt("comments"),
            Matchers.is(2)
        );
    }

    /**
     * MkPull can get comments.
     * @throws Exception when a problem occurs.
     */
    @Test
    void canGetComments() throws Exception {
        MatcherAssert.assertThat(
            "Value is null",
            MkPullTest.pullRequest().comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can get its base ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canGetBase() throws Exception {
        MatcherAssert.assertThat(
            "Base ref is absent",
            MkPullTest.pullRequest().base(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can name its base ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    void namesBase() throws Exception {
        MatcherAssert.assertThat(
            "Base ref has a wrong name",
            MkPullTest.pullRequest().base().ref(),
            Matchers.equalTo(MkPullTest.BASE)
        );
    }

    /**
     * MkPull can get its head ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canGetHead() throws Exception {
        MatcherAssert.assertThat(
            "Head ref is absent",
            MkPullTest.pullRequest().head(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can name its head ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    void namesHead() throws Exception {
        MatcherAssert.assertThat(
            "Head ref has a wrong name",
            MkPullTest.pullRequest().head().ref(),
            Matchers.equalTo(MkPullTest.HEAD)
        );
    }

    /**
     * MkPull can be represented as JSON.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canRetrieveAsJson() throws Exception {
        MatcherAssert.assertThat(
            "Pull request has a wrong number",
            MkPullTest.json().getInt("number"),
            Matchers.equalTo(1)
        );
    }

    /**
     * MkPull can show its head in JSON.
     * @throws Exception If a problem occurs.
     */
    @Test
    void showsHeadInJson() throws Exception {
        MatcherAssert.assertThat(
            "Head has a wrong label",
            MkPullTest.json().getJsonObject("head").getString("label"),
            Matchers.equalTo(
                String.format("%s:%s", MkPullTest.USERNAME, "blah")
            )
        );
    }

    /**
     * MkPull can show its base in JSON.
     * @throws Exception If a problem occurs.
     */
    @Test
    void showsBaseInJson() throws Exception {
        MatcherAssert.assertThat(
            "Base has a wrong label",
            MkPullTest.json().getJsonObject("base").getString("label"),
            Matchers.equalTo(
                String.format("%s:%s", MkPullTest.USERNAME, "aaa")
            )
        );
    }

    /**
     * MkPull can show its author in JSON.
     * @throws Exception If a problem occurs.
     */
    @Test
    void showsAuthorInJson() throws Exception {
        MatcherAssert.assertThat(
            "Author has a wrong login",
            MkPullTest.json().getJsonObject("user").getString("login"),
            Matchers.equalTo(MkPullTest.USERNAME)
        );
    }

    /**
     * MkPull can perform JSON patch operation.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canPatchJson() throws Exception {
        final Pull pull = MkPullTest.repo().pulls()
            .create("Test Patch", "def", "abc");
        final String value = "someValue";
        pull.patch(
            Json.createObjectBuilder().add("somekey", value).build()
        );
        MatcherAssert.assertThat(
            "Assertion failed",
            pull.json().getString("somekey"),
            new IsEqual<>(value)
        );
    }

    /**
     * MkPull can patch a numeric attribute.
     * @throws Exception If a problem occurs.
     */
    @Test
    void canPatchNumberInJson() throws Exception {
        final Pull pull = MkPullTest.repo().pulls()
            .create("Test Patch", "def", "abc");
        final int lines = 20;
        pull.patch(Json.createObjectBuilder().add("additions", lines).build());
        MatcherAssert.assertThat(
            "Assertion failed",
            pull.json().getString("additions"),
            new IsEqual<>(Integer.toString(lines))
        );
    }

    @Test
    void issueIsPull() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            "Issue is not a pull request",
            new Issue.Smart(pull.repo().issues().get(pull.number())).isPull(),
            Matchers.is(true)
        );
    }

    @Test
    void retrievesAllChecks() throws Exception {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkPullTest.pullRequest().checks().all(),
            Matchers.hasSize(0)
        );
    }

    private static Repo repo() throws IOException {
        return new MkGitHub(MkPullTest.USERNAME).randomRepo();
    }

    private static MkPull pull(final int number) throws IOException {
        return new MkPull(
            new MkStorage.InFile(),
            String.format("login-%d", number),
            Mockito.mock(Coordinates.class),
            number
        );
    }

    private static JsonObject json() throws IOException {
        return MkPullTest.repo().pulls()
            .create("Test Pull Json", "blah", "aaa").json();
    }

    private static Pull pullRequest() throws Exception {
        final Repo rpo = MkPullTest.repo();
        final MkBranches branches = (MkBranches) rpo.branches();
        branches.create(
            MkPullTest.BASE,
            "e11f7ffa797f8422f016576cb7c2f5bb6f66aa51"
        );
        branches.create(
            MkPullTest.HEAD,
            "5a8d0143b3fa9de883a5672d4a1f44d472657a8a"
        );
        return rpo.pulls().create(
            "Test PR",
            MkPullTest.HEAD,
            MkPullTest.BASE
        );
    }
}
