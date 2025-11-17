/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
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
 *
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferencesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtReferences should create and return a Reference.
     */
    @Test
    public void createsReference() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(this.resource.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGithub().randomRepo()
            );
            MatcherAssert.assertThat(
                refs.create("abceefgh3456", "refs/heads/feature-a"),
                Matchers.instanceOf(Reference.class)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over References.
     */
    @Test
    public void iteratesReferences() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"ref\":\"refs/heads/feature-a\"}"
                )
            ).start(this.resource.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGithub().randomRepo()
            );
            MatcherAssert.assertThat(
                refs.iterate(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtReferences should be able to remove a Reference.
     */
    @Test
    public void removesReference() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGithub().randomRepo()
            );
            refs.remove("heads/feature-a");
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over tags.
     */
    @Test
    public void iteratesTags() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/tags/feature-b\"}]"
                )
            ).start(this.resource.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGithub().randomRepo()
            );
            MatcherAssert.assertThat(
                refs.tags(),
                Matchers.iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/tags")
            );
            container.stop();
        }
    }

    /**
     * RtReferences should be able to iterate over heads.
     */
    @Test
    public void iteratesHeads() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"ref\":\"refs/heads/feature-c\"}]"
                )
            ).start(this.resource.port())
        ) {
            final References refs = new RtReferences(
                new ApacheRequest(container.home()),
                new MkGithub().randomRepo()
            );
            MatcherAssert.assertThat(
                refs.heads(),
                Matchers.iterableWithSize(1)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/git/refs/heads")
            );
            container.stop();
        }
    }
}
