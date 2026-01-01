/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for {@link RtGitignores}.
 * @see <a href="https://developer.github.com/v3/gitignore/">Gitignore API</a>
 * @since 0.8
 */
@Immutable
@ExtendWith(RandomPort.class)
final class RtGitignoresTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void iterateTemplateNames() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add("C")
                        .add("Java")
                        .build()
                        .toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtGitignores gitignores = new RtGitignores(
                new RtGitHub(new JdkRequest(container.home()))
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                    gitignores.iterate(),
                    Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void getRawTemplateByName() throws IOException {
        final RtGitignores gitignores = new RtGitignores(
            new RtGitHub(new FakeRequest().withBody("# Object files"))
        );
        MatcherAssert.assertThat(
            "String does not start with expected value",
            gitignores.template("C#"),
            Matchers.startsWith("# Object")
        );
    }

}
