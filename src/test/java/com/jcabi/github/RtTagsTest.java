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
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * Testcase for RtTags.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtTagsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtTags can create a tag.
     * @checkstyle IndentationCheck (20 lines)
     */
    @Test
    public void createsTag() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"sha\":\"0abcd89jcabitest\", \"tag\":\"v.0.1\"}"
            )
        ).next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"ref\":\"refs/heads/feature-a\"}"
            )
        ).start(RandomPort.port());
        final Tags tags = new RtTags(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo()
        );
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "scott@gmail.com")
            .add("date", "2011-06-17T14:53:35-07:00").build();
        final JsonObject input = Json.createObjectBuilder()
            .add("tag", "v.0.1").add("message", "initial version")
            .add("object", "07cd4r45Test444").add("type", "commit")
            .add("tagger", tagger).build();
        try {
            MatcherAssert.assertThat(
                "Object is not of expected type",
                tags.create(input),
                Matchers.instanceOf(Tag.class)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        } finally {
            container.stop();
        }
    }
}
