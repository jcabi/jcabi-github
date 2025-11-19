/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
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
 * Testcase for RtTag.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtTagTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void fetchesContent() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"sha\":\"abdes00test\",\"tag\":\"v.0.1\"}"
            )
        ).start(this.resource.port());
        final Tag tag = new RtTag(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo(),
            "abdes00test"
        );
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                tag.json().getString("tag"),
                Matchers.is("v.0.1")
            );
        } finally {
            container.stop();
        }
    }
}
