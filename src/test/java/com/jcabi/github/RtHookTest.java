/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHook}.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (2 lines)
 */
@ExtendWith(RandomPort.class)
final class RtHookTest {

    /**
     * RtHook should perform a JSON request to "/repos/:owner/:repo/hooks/:id".
     */
    @Test
    void performsValidRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"test\":\"hook\"}"
                )
            ).start(RandomPort.port())
        ) {
            final Hook hook = new RtHook(
                new ApacheRequest(container.home()),
                RtHookTest.repo(),
                1
            );
            MatcherAssert.assertThat(
                "Value is null",
                hook.json(),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/test/repo/hooks/1")
            );
            container.stop();
        }
    }

    /**
     * RtHook.json() should return a json array with the hook's events.
     */
    @Test
    void returnsEvents() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{ \"id\": 1, \"events\": [ \"push\", \"pull_request\" ] }"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Assertion failed",
                new RtHook(
                    new ApacheRequest(container.home()),
                    RtHookTest.repo(),
                    1
                ).json().getJsonArray("events")
                    .stream()
                    .map(JsonValue::toString)
                    .map(event -> event.replace("\"", ""))
                    .collect(Collectors.toList()),
                new IsIterableContainingInAnyOrder<>(
                    Arrays.asList(
                        new IsEqual<>("push"),
                        new IsEqual<>("pull_request")
                    )
                )
            );
            container.stop();
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "repo"))
            .when(repo).coordinates();
        return repo;
    }

}
