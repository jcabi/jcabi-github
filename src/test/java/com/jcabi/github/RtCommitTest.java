/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for RtCommit.
 * @since 0.18.2
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtCommitTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void readsMessage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"sha\":\"a0b1c3\", \"commit\":{\"message\":\"hello\"}}"
                )
            ).start(RandomPort.port())) {
            final Commit.Smart commit = new Commit.Smart(
                new RtCommit(
                    new JdkRequest(container.home()),
                    new MkGitHub().randomRepo(),
                    "sha"
                )
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                commit.message(),
                Matchers.equalTo("hello")
            );
        }
    }
}
