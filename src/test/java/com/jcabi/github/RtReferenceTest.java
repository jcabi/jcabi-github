/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtReference}.
 *
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferenceTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtReference should be able to execute patch.
     */
    @Test
    public void patchesContent() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureA\"}"
                )
            ).start(this.resource.port())
        ) {
            final Reference reference = new RtReference(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo(),
                "refs/heads/featureA"
            );
            reference.patch(
                Json.createObjectBuilder().add("sha", "abcdef12345")
                .add("force", "false").build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
            container.stop();
        }
    }

    /**
     * RtReference should be able to fetch its json.
     */
    @Test
    public void fetchesContent() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureB\"}"
                )
            ).start(this.resource.port())
        ) {
            final Reference reference = new RtReference(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo(),
                "refs/heads/featureB"
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                reference.json().getString("ref"),
                Matchers.is("refs/heads/featureB")
            );
            container.stop();
        }
    }

    /**
     * RtReference should be able to return its ref.
     */
    @Test
    public void returnsRef() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureC\"}"
                )
            ).start(this.resource.port())
        ) {
            final Reference reference = new RtReference(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo(),
                "refs/heads/featureC"
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                reference.ref(),
                Matchers.is("refs/heads/featureC")
            );
            container.stop();
        }
    }

    /**
     * RtReference should be able to return its owner repo.
     */
    @Test
    public void returnsOwner() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureD\"}"
                )
            ).start(this.resource.port());
        ) {
            final Reference reference = new RtReference(
                new ApacheRequest(container.home()),
                owner,
                "refs/heads/featureD"
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                reference.repo(),
                Matchers.is(owner)
            );
            container.stop();
        }
    }
}
