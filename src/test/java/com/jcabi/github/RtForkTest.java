/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtFork}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtForkTest {

    @Test
    void patchesOrganizationOfFork() throws IOException {
        final String patched = "some patched organization";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtForkTest.answer("some organization"))
                .next(RtForkTest.answer(patched))
                .start(RandomPort.port());
            MkContainer forks = new MkGrizzlyContainer()
                .start(RandomPort.port())
        ) {
            final RtFork fork = RtForkTest.fork(container, forks);
            fork.patch(RtForkTest.fork(patched));
            MatcherAssert.assertThat(
                "Organization of the fork is not patched",
                new Fork.Smart(fork).organization(),
                Matchers.equalTo(patched)
            );
        }
    }

    @Test
    void fetchesNameOfPatchedFork() throws IOException {
        final String patched = "some patched organization";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtForkTest.answer("some organization"))
                .next(RtForkTest.answer(patched))
                .start(RandomPort.port());
            MkContainer forks = new MkGrizzlyContainer()
                .start(RandomPort.port())
        ) {
            final RtFork fork = RtForkTest.fork(container, forks);
            fork.patch(RtForkTest.fork(patched));
            MatcherAssert.assertThat(
                "Patched fork has no name",
                new Fork.Smart(fork).name(),
                Matchers.notNullValue()
            );
        }
    }

    private static RtFork fork(
        final MkContainer container,
        final MkContainer forks
    ) throws IOException {
        return new RtFork(
            new ApacheRequest(container.home()),
            new RtRepo(
                new MkGitHub(),
                new ApacheRequest(forks.home()),
                new Coordinates.Simple("test_user", "test_repo")
            ),
            1
        );
    }

    private static MkAnswer.Simple answer(final String organization) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtForkTest.fork(organization).toString()
        );
    }

    private static JsonObject fork(final String organization) {
        return Json.createObjectBuilder()
            .add("organization", organization)
            .add("name", "nm")
            .build();
    }
}
