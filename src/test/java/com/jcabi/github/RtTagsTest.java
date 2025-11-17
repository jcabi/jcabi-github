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
import java.net.HttpURLConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testcase for RtTags.
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
     * @throws Exception - If something goes wrong.
     * @checkstyle IndentationCheck (20 lines)
     */
    @Test
    public void createsTag() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"sha\":\"0abcd89jcabitest\",\"tag\":\"v.0.1\"}"
            )
        ).next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"ref\":\"refs/heads/feature-a\"}"
            )
        ).start(this.resource.port());
        final Tags tags = new RtTags(
            new ApacheRequest(container.home()),
            new MkGithub().randomRepo()
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
                tags.create(input),
                Matchers.instanceOf(Tag.class)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        } finally {
            container.stop();
        }
    }
}
