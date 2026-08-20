/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLabels}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtLabelsTest {

    /**
     * Name of the created label.
     */
    private static final String NAME = "API";

    /**
     * Color of the created label.
     */
    private static final String COLOR = "FFFFFF";

    @Test
    void createsLabelWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtLabelsTest.answer())
                .start(RandomPort.port())
        ) {
            RtLabelsTest.create(container);
            MatcherAssert.assertThat(
                "Label is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsLabelWithName() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtLabelsTest.answer())
                .next(RtLabelsTest.fetched())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created label has a wrong name",
                new Label.Smart(RtLabelsTest.create(container)).name(),
                Matchers.equalTo(RtLabelsTest.NAME)
            );
        }
    }

    @Test
    void createsLabelWithColor() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtLabelsTest.answer())
                .next(RtLabelsTest.fetched())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created label has a wrong color",
                new Label.Smart(RtLabelsTest.create(container)).color(),
                Matchers.equalTo(RtLabelsTest.COLOR)
            );
        }
    }

    @Test
    void fetchesSingleLabel() throws IOException {
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
            MatcherAssert.assertThat(
                "Values are not equal",
                new Label.Smart(
                    new RtLabels(
                        new JdkRequest(container.home()),
                        RtLabelsTest.repo()
                    ).get(name)
                ).color(),
                Matchers.equalTo(color)
            );
            container.stop();
        }
    }

    @Test
    void deletesLabelWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            ).delete("issue");
            MatcherAssert.assertThat(
                "Label is not deleted with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void deletesLabelWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtLabels(
                new JdkRequest(container.home()),
                RtLabelsTest.repo()
            ).delete("issue");
            MatcherAssert.assertThat(
                "Label is deleted with a body",
                container.take().body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
        }
    }

    @Test
    void iterateLabels() throws IOException {
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
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtLabels(
                    new JdkRequest(container.home()),
                    RtLabelsTest.repo()
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    private static Label create(final MkContainer container)
        throws IOException {
        return new RtLabels(
            new JdkRequest(container.home()),
            RtLabelsTest.repo()
        ).create(RtLabelsTest.NAME, RtLabelsTest.COLOR);
    }

    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtLabelsTest.label(RtLabelsTest.NAME, RtLabelsTest.COLOR)
                .toString()
        );
    }

    private static MkAnswer fetched() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtLabelsTest.label(RtLabelsTest.NAME, RtLabelsTest.COLOR)
                .toString()
        );
    }

    private static JsonObject label(final String name, final String color) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("color", color)
            .build();
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
