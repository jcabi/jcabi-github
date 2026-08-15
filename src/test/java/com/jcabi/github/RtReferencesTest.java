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
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtReferences}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtReferencesTest {

    @Test
    void createsReference() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created reference is of a wrong type",
                RtReferencesTest.references(container)
                    .create("abceefgh3456", "refs/heads/feature-a"),
                Matchers.instanceOf(Reference.class)
            );
        }
    }

    @Test
    void createsReferenceWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(RandomPort.port())
        ) {
            RtReferencesTest.references(container)
                .create("abceefgh3456", "refs/heads/feature-a");
            MatcherAssert.assertThat(
                "Reference is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void iteratesReferences() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtReferences(
                    new ApacheRequest(container.home()),
                    new MkGitHub().randomRepo()
                ).iterate(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void removesReference() throws IOException {
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
    void iteratesTags() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/tags/feature-b\"}]"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Wrong amount of tags is iterated",
                RtReferencesTest.references(container).tags(),
                Matchers.iterableWithSize(1)
            );
        }
    }

    @Test
    void iteratesTagsFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/tags/feature-b\"}]"
                )
            ).start(RandomPort.port())
        ) {
            RtReferencesTest.references(container).tags().iterator().next();
            MatcherAssert.assertThat(
                "Tags are iterated from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/tags")
            );
        }
    }

    @Test
    void iteratesHeads() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/heads/feature-c\"}]"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Wrong amount of heads is iterated",
                RtReferencesTest.references(container).heads(),
                Matchers.iterableWithSize(1)
            );
        }
    }

    @Test
    void iteratesHeadsFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/heads/feature-c\"}]"
                )
            ).start(RandomPort.port())
        ) {
            RtReferencesTest.references(container).heads().iterator().next();
            MatcherAssert.assertThat(
                "Heads are iterated from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/heads")
            );
        }
    }

    /**
     * References served by the given container.
     * @param container Container to serve the references
     * @return References
     * @throws IOException If there is any I/O problem
     */
    private static RtReferences references(final MkContainer container)
        throws IOException {
        return new RtReferences(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo()
        );
    }
}
