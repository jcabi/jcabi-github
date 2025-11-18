/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPull}.
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RtPullTest {
    /**
     * Property name for ref name in pull request ref JSON object.
     */
    private static final String REF_PROP = "ref";
    /**
     * Property name for commit SHA in pull request ref JSON object.
     */
    private static final String SHA_PROP = "sha";

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPull should be able to retrieve commits.
     *
     */
    @Test
    public void fetchesCommits() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"commits\":\"test\"}]"
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            MatcherAssert.assertThat(
                "Value is null",
                pull.commits(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to retrieve files.
     *
     */
    @Test
    public void fetchesFiles() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"file1\":\"testFile\"}]"
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                2
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                pull.files().iterator().next().getString("file1"),
                Matchers.equalTo("testFile")
            );
            container.stop();
        }
    }

    /**
     * RtPull can fetch its base ref.
     * @throws IOException If some I/O problem occurs
     */
    @Test
    public void fetchesBase() throws IOException {
        final String ref = "sweet-feature-branch";
        final String sha = "e93c6a2216c69daa574abc16e7c14767fce44ad6";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add(
                            "base",
                            Json.createObjectBuilder()
                                .add(RtPullTest.REF_PROP, ref)
                                .add(RtPullTest.SHA_PROP, sha)
                                .build()
                        )
                        .build()
                        .toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            final PullRef base = pull.base();
            MatcherAssert.assertThat(
                "Value is null",
                base,
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                base.ref(),
                Matchers.equalTo(ref)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                base.sha(),
                Matchers.equalTo(sha)
            );
            container.stop();
        }
    }

    /**
     * RtPull can fetch its head ref.
     * @throws IOException If some I/O problem occurs
     */
    @Test
    public void fetchesHead() throws IOException {
        final String ref = "neat-other-branch";
        final String sha = "9c717b4716e4fc4d917f546e8e6b562e810e3922";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtPullTest.head(ref, sha).toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                1
            );
            final PullRef head = pull.head();
            MatcherAssert.assertThat(
                "Value is null",
                head,
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                head.ref(),
                Matchers.equalTo(ref)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                head.sha(),
                Matchers.equalTo(sha)
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to perform a merge.
     *
     */
    @Test
    public void executeMerge() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testMerge")
            ).start(this.resource.port())
        ) {
            final RtPull pull = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                3
            );
            pull.merge("Test commit.");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.equalTo("{\"commit_message\":\"Test commit.\"}")
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to fetch pull checks.
     * @throws IOException If some I/O problem occurs.
     */
    @Test
    public void canFetchChecks() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        RtPullTest.head().toString()
                    )
                )
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        RtPullTest.check().toString()
                    ))
                .start(this.resource.port())
        ) {
            final Collection<? extends Check> all = new RtPull(
                new ApacheRequest(container.home()),
                this.repo(),
                new Random().nextInt()
            ).checks().all();
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                all,
                Matchers.hasSize(1)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                all.iterator().next().successful(),
                Matchers.is(true)
            );
            container.stop();
        }
    }

    /**
     * RtPull should be able to compare different instances.
     *
     */
    @Test
    public void canCompareInstances() {
        final RtPull less = new RtPull(new FakeRequest(), this.repo(), 1);
        final RtPull greater = new RtPull(new FakeRequest(), this.repo(), 2);
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * RtPull should be able to fetch pull comments.
     *
     */
    @Test
    @Ignore
    public void canFetchComments() {
        //to be implemented
    }

    /**
     * Mock repository for testing purposes.
     * @return Repo the mock repository.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("/user").when(coords).user();
        Mockito.doReturn("/repo").when(coords).repo();
        return repo;
    }

    /**
     * Check as JSON object.
     * @return Check as JSON object.
     */
    private static JsonObject check() {
        return Json.createObjectBuilder()
            .add("total_count", Json.createValue(1))
            .add(
                "check_runs",
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("id", Json.createValue(new Random().nextInt()))
                            .add("status", "completed")
                            .add("conclusion", "success")
                            .build()
                    )
            ).build();
    }

    /**
     * Head as JSON object.
     * @return Head as JSON object.
     */
    private static JsonObject head() {
        return RtPullTest.head(
            "ref-ref",
            "6d299617d9094ae6940b3958bbabab68fd1ddabb"
        );
    }

    /**
     * Head as JSON object.
     * @param ref Ref.
     * @param sha Sha.
     * @return Head as JSON object.
     */
    private static JsonObject head(final String ref, final String sha) {
        return Json.createObjectBuilder()
            .add(
                "head",
                Json.createObjectBuilder()
                    .add(RtPullTest.REF_PROP, ref)
                    .add(RtPullTest.SHA_PROP, sha)
                    .build()
            )
            .build();
    }

}
