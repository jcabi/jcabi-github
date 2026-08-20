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
 * Testcase for RtTags.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtTagsTest {

    @Test
    void createsTag() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTagsTest.tag())
                .next(RtTagsTest.reference())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created tag is of a wrong type",
                RtTagsTest.create(container),
                Matchers.instanceOf(Tag.class)
            );
        }
    }

    @Test
    void createsTagWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTagsTest.tag())
                .next(RtTagsTest.reference())
                .start(RandomPort.port())
        ) {
            RtTagsTest.create(container);
            MatcherAssert.assertThat(
                "Tag is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsReferenceWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTagsTest.tag())
                .next(RtTagsTest.reference())
                .start(RandomPort.port())
        ) {
            RtTagsTest.create(container);
            container.take();
            MatcherAssert.assertThat(
                "Reference of the tag is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    private static Tag create(final MkContainer container) throws IOException {
        return new RtTags(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo()
        ).create(
            Json.createObjectBuilder()
                .add("tag", "v.0.1")
                .add("message", "initial version")
                .add("object", "07cd4r45Test444")
                .add("type", "commit").add(
                    "tagger",
                    Json.createObjectBuilder()
                        .add("name", "Scott")
                        .add("email", "scott@gmail.com")
                        .add("date", "2011-06-17T14:53:35-07:00")
                )
                .build()
        );
    }

    private static MkAnswer tag() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            "{\"sha\":\"0abcd89jcabitest\", \"tag\":\"v.0.1\"}"
        );
    }

    private static MkAnswer reference() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            "{\"ref\":\"refs/heads/feature-a\"}"
        );
    }
}
