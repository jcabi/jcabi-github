/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLabels}.
 * @since 0.1
 */
public final class RtLabelsTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void createLabel() throws IOException {
        final String name = "API";
        final String color = "FFFFFF";
        final String body = RtLabelsTest.label(name, color).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(RandomPort.port())
        ) {
            final RtLabels labels = new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            );
            final Label label = labels.create(name, color);
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new Label.Smart(label).name(),
                Matchers.equalTo(name)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new Label.Smart(label).color(),
                Matchers.equalTo(color)
            );
            container.stop();
        }
    }

    @Test
    public void getSingleLabel() throws IOException {
        final String name = "bug";
        final String color = "f29513";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtLabelsTest.label(name, color).toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtLabels issues = new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            );
            final Label label = issues.get(name);
            MatcherAssert.assertThat(
                "Values are not equal",
                new Label.Smart(label).color(),
                Matchers.equalTo(color)
            );
            container.stop();
        }
    }

    @Test
    public void deleteLabel() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final RtLabels issues = new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            );
            issues.delete("issue");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
            container.stop();
        }
    }

    @Test
    public void iterateLabels() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtLabelsTest.label("new issue", "f29512"))
                        .add(RtLabelsTest.label("new bug", "f29522"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtLabels labels = new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                labels.iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param name The name of the label
     * @param color A 6 character hex code, identifying the color
     * @return JsonObject
     */
    private static JsonObject label(
        final String name, final String color) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("color", color)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
