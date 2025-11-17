/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHooks}.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
public final class RtHooksTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtHooks can fetch empty list of hooks.
     */
    @Test
    public void canFetchEmptyListOfHooks() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")
            ).start(this.resource.port())
        ) {
            final Hooks hooks = new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            );
            MatcherAssert.assertThat(
                hooks.iterate(),
                Matchers.emptyIterable()
            );
            container.stop();
        }
    }

    /**
     * RtHooks can fetch non empty list of hooks.
     */
    @Test
    public void canFetchNonEmptyListOfHooks() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(
                            RtHooksTest.hook(
                                "hook 1",
                                Collections.emptyMap()
                            )
                        )
                        .add(
                            RtHooksTest.hook(
                                "hook 2",
                                Collections.emptyMap()
                            )
                        )
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtHooks hooks = new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            );
            MatcherAssert.assertThat(
                hooks.iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtHooks can fetch single hook.
     */
    @Test
    public void canFetchSingleHook() throws IOException {
        final String name = "hook name";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtHooksTest.hook(
                        name,
                        Collections.emptyMap()
                    ).toString()
                )
            ).start(this.resource.port())
        ) {
            final Hooks hooks = new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            );
            final Hook hook = hooks.get(1);
            MatcherAssert.assertThat(
                new Hook.Smart(hook).name(),
                Matchers.equalTo(name)
            );
            container.stop();
        }
    }

    /**
     * RtHooks can create a hook.
     *
     */
    @Test
    public void canCreateHook() throws IOException {
        final ConcurrentHashMap<String, String> config =
            new ConcurrentHashMap<>(2);
        config.put("url", "http://example.com");
        config.put("content_type", "json");
        final String name = "hook name";
        final String body = RtHooksTest.hook(name, config).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(this.resource.port())
        ) {
            final Hooks hooks = new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            );
            final Hook hook = hooks.create(
                name, config, Collections.emptyList(), true
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Hook.Smart(hook).name(),
                Matchers.equalTo(name)
            );
            container.stop();
        }
    }

    /**
     * RtHooks can delete a hook.
     *
     */
    @Test
    public void canDeleteHook() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())
        ) {
            final Hooks hooks = new RtHooks(
                new JdkRequest(container.home()),
                RtHooksTest.repo()
            );
            hooks.remove(1);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.is(Matchers.emptyString())
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param name Name of the hook
     * @param config Config of hook
     * @return JsonObject
     */
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

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "hooks"))
            .when(repo).coordinates();
        return repo;
    }
}
