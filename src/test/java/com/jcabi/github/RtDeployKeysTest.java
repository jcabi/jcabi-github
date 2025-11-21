/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
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
 * Test case for {@link RtDeployKeys}.
 * @since 0.8
 */
@Immutable
@ExtendWith(RandomPort.class)
public final class RtDeployKeysTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    public void canFetchEmptyListOfDeployKeys() {
        final DeployKeys keys = new RtDeployKeys(
            new FakeRequest().withBody("[]"),
            RtDeployKeysTest.repo()
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            keys.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtDeployKeys can fetch non empty list of deploy keys.
     *
     * @throws IOException If some problem inside.
     */
    @Test
    public void canFetchNonEmptyListOfDeployKeys() throws IOException {
        try (MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtDeployKeysTest.key(1))
                    .add(RtDeployKeysTest.key(2))
                    .build().toString()
            )
        )) {
            container.start(RandomPort.port());
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtDeployKeys(
                    new ApacheRequest(container.home()),
                    RtDeployKeysTest.repo()
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    /**
     * RtDeployKeys can fetch single deploy key.
     * @throws IOException If some problem inside
     */
    @Test
    public void canFetchSingleDeployKey() throws IOException {
        final int number = 1;
        final DeployKeys keys = new RtDeployKeys(
            // @checkstyle MultipleStringLiterals (1 line)
            new FakeRequest().withBody(String.format("{\"id\":%d}", number)),
            RtDeployKeysTest.repo()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            // @checkstyle MultipleStringLiterals (1 line)
            keys.get(number).json().getInt("id"),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtDeployKeys can create a key.
     * @throws IOException If some problem inside.
     */
    @Test
    public void canCreateDeployKey() throws IOException {
        final int number = 2;
        try (MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("{\"id\":%d}", number)
            )
        )) {
            container.start(RandomPort.port());
            final DeployKeys keys = new RtDeployKeys(
                new ApacheRequest(container.home()), RtDeployKeysTest.repo()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                keys.create("Title", "Key").number(),
                Matchers.equalTo(number)
            );
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "keys"))
            .when(repo).coordinates();
        return repo;
    }

    /**
     * Create and return key to test.
     * @param number Deploy Key Id
     * @return JsonObject
     */
    private static JsonObject key(final int number) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("key", "ssh-rsa AAA")
            .build();
    }
}
