/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtReference}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtReferenceTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void patchesContent() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureA\"}"
                )
            ).start(RandomPort.port())
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

    @Test
    void fetchesContent() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureB\"}"
                )
            ).start(RandomPort.port())
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

    @Test
    void returnsRef() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureC\"}"
                )
            ).start(RandomPort.port())
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

    @Test
    void returnsOwner() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/featureD\"}"
                )
            ).start(RandomPort.port())
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
