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
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtReferences}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferencesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void createsReference() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(RandomPort.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "Object is not of expected type",
                refs.create("abceefgh3456", "refs/heads/feature-a"),
                Matchers.instanceOf(Reference.class)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            container.stop();
        }
    }

    @Test
    public void iteratesReferences() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(RandomPort.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "Value is null",
                refs.iterate(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    public void removesReference() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            refs.remove("heads/feature-a");
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    @Test
    public void iteratesTags() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/tags/feature-b\"}]"
                )
            ).start(RandomPort.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                refs.tags(),
                Matchers.iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/tags")
            );
            container.stop();
        }
    }

    @Test
    public void iteratesHeads() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/heads/feature-c\"}]"
                )
            ).start(RandomPort.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                refs.heads(),
                Matchers.iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/heads")
            );
            container.stop();
        }
    }
}
