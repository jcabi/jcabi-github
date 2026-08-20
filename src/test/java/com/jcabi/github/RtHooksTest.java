/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHooks}.
 * @since 0.8
 */
@Immutable
@ExtendWith(RandomPort.class)
final class RtHooksTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void canFetchEmptyListOfHooks() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection is not empty",
                new RtHooks(
                    new JdkRequest(container.home()),
                    RtHooksTest.repo()
                ).iterate(),
                Matchers.emptyIterable()
            );
            container.stop();
        }
    }

    @Test
    void canFetchNonEmptyListOfHooks() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder().add(
                        RtHooksTest.hook(
                            "hook 1",
                            Collections.emptyMap()
                        )
                        ).add(
                            RtHooksTest.hook(
                                "hook 2",
                                Collections.emptyMap()
                            )
                        )
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtHooks(
                    new JdkRequest(container.home()),
                    RtHooksTest.repo()
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void canFetchSingleHook() throws IOException {
        final String name = "hook name";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtHooksTest.hook(
                        name,
                        Collections.emptyMap()
                    ).toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new Hook.Smart(
                    new RtHooks(
                        new JdkRequest(container.home()),
                        RtHooksTest.repo()
                    ).get(1)
                ).name(),
                Matchers.equalTo(name)
            );
            container.stop();
        }
    }

    @Test
    void createsHookWithPost() throws IOException {
        final String name = "hook name";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtHooksTest.answer(name))
                .start(RandomPort.port())
        ) {
            RtHooksTest.create(container, name);
            MatcherAssert.assertThat(
                "Hook is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsHookWithName() throws IOException {
        final String name = "hook name";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtHooksTest.answer(name))
                .next(RtHooksTest.fetched(name))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created hook has a wrong name",
                new Hook.Smart(RtHooksTest.create(container, name)).name(),
                Matchers.equalTo(name)
            );
        }
    }

    @Test
    void deletesHookWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            ).remove(1);
            MatcherAssert.assertThat(
                "Hook is not deleted with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void deletesHookWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            ).remove(1);
            MatcherAssert.assertThat(
                "Hook is deleted with a body",
                container.take().body(),
                Matchers.is(Matchers.emptyString())
            );
        }
    }

    private static Hook create(final MkContainer container, final String name)
        throws IOException {
        return new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        ).create(
            name, RtHooksTest.config(), Collections.emptyList(), true
        );
    }

    private static MkAnswer answer(final String name) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtHooksTest.hook(name, RtHooksTest.config()).toString()
        );
    }

    private static MkAnswer fetched(final String name) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtHooksTest.hook(name, RtHooksTest.config()).toString()
        );
    }

    private static Map<String, String> config() {
        final Map<String, String> config = new ConcurrentHashMap<>(2);
        config.put("url", "http://example.com");
        config.put("content_type", "json");
        return config;
    }

    private static JsonObject hook(final String name,
        final Map<String, String> config) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("name", name)
            .add("config", builder)
            .build();
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "hooks"))
            .when(repo).coordinates();
        return repo;
    }
}
