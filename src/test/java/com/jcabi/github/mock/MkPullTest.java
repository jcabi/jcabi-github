/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkPull}.
 *
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MkPullTest {
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

    /**
     * MkPull should be able to compare different instances.
     *
     */
    @Test
    public void canCompareInstances() throws IOException {
        final MkPull less = new MkPull(
            new MkStorage.InFile(),
            "login-less",
            Mockito.mock(Coordinates.class),
            1
        );
        final MkPull greater = new MkPull(
            new MkStorage.InFile(),
            "login-greater",
            Mockito.mock(Coordinates.class),
            2
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkPull can get comments number if no comments.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetCommentsNumberIfZero() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            "Values are not equal",
            pull.json().getInt("comments"),
            Matchers.is(0)
        );
    }

    /**
     * MkPull can get comments number if some comments exist.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetCommentsNumberIfNonZero() throws Exception {
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
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetComments() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            "Value is null",
            pull.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can get its base ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canGetBase() throws Exception {
        final PullRef base = MkPullTest.pullRequest().base();
        MatcherAssert.assertThat(
            "Value is null",base, Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Values are not equal",
            base.ref(),
            Matchers.equalTo(MkPullTest.BASE)
        );
    }

    /**
     * MkPull can get its head ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canGetHead() throws Exception {
        final PullRef head = MkPullTest.pullRequest().head();
        MatcherAssert.assertThat(
            "Value is null",head, Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Values are not equal",
            head.ref(),
            Matchers.equalTo(MkPullTest.HEAD)
        );
    }

    /**
     * MkPull can be represented as JSON.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canRetrieveAsJson() throws Exception {
        final String head = "blah";
        final String base = "aaa";
        final Pull pull = MkPullTest.repo().pulls()
            .create("Test Pull Json", head, base);
        final JsonObject json = pull.json();
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getInt("number"),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getJsonObject("head").getString("label"),
            Matchers.equalTo(
                String.format(
                    "%s:%s",
                    MkPullTest.USERNAME,
                    head
                )
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getJsonObject("base").getString("label"),
            Matchers.equalTo(
                String.format(
                    "%s:%s",
                    MkPullTest.USERNAME,
                    base
                )
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getJsonObject("user").getString("login"),
            Matchers.equalTo(MkPullTest.USERNAME)
        );
    }

    /**
     * MkPull can perform JSON patch operation.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canPatchJson() throws Exception {
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
        final int lines = 20;
        pull.patch(Json.createObjectBuilder().add("additions", lines).build());
        MatcherAssert.assertThat(
            "Assertion failed",
            pull.json().getString("additions"),
            new IsEqual<>(Integer.toString(lines))
        );
    }

    @Test
    public void issueIsPull() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            "Issue is not a pull request",
            new Issue.Smart(pull.repo().issues().get(pull.number())).isPull(),
            Matchers.is(true)
        );
    }

    @Test
    public void retrievesAllChecks() throws Exception {
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            pull.checks().all(),
            Matchers.hasSize(0)
        );
    }

    /**
     * Create an repo to work with.
     * @return Repo
     */
    private static Repo repo() throws IOException {
        return new MkGitHub(MkPullTest.USERNAME).randomRepo();
    }

    /**
     * Create a pull request to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
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
