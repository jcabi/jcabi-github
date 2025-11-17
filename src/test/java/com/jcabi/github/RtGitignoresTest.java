/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link RtGitignores}.
 *
 * @see <a href="https://developer.github.com/v3/gitignore/">Gitignore API</a>
 */
@Immutable
public final class RtGitignoresTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGitignores can iterate template names.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateTemplateNames() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add("C")
                        .add("Java")
                        .build()
                        .toString()
                )
            ).start(this.resource.port())
        ) {
            final RtGitignores gitignores = new RtGitignores(
                    new RtGithub(new JdkRequest(container.home()))
            );
            MatcherAssert.assertThat(
                    gitignores.iterate(),
                    Matchers.<String>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtGitignores can get raw template by name.
     * @throws Exception if there is any error
     */
    @Test
    public void getRawTemplateByName() throws Exception {
        final RtGitignores gitignores = new RtGitignores(
            new RtGithub(new FakeRequest().withBody("# Object files"))
        );
        MatcherAssert.assertThat(
            gitignores.template("C#"),
            Matchers.startsWith("# Object")
        );
    }

}
