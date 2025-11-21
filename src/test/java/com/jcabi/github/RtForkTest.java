/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtFork}.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
public final class RtForkTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    /**
     * RtFork can patch comment and return new json.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    public void patchAndCheckJsonFork() throws IOException {
        final String original = "some organization";
        final String patched = "some patched organization";
        try (
            MkContainer container =
                new MkGrizzlyContainer().next(RtForkTest.answer(original))
                    .next(
                        RtForkTest.answer(patched)
                    ).next(RtForkTest.answer(original)).start(
                        RandomPort.port()
                    );
            MkContainer forksContainer = new MkGrizzlyContainer().start(
                RandomPort.port()
            )) {
            final RtRepo repo =
                new RtRepo(
                    new MkGitHub(),
                    new ApacheRequest(forksContainer.home()),
                    new Coordinates.Simple("test_user", "test_repo")
                );
            final RtFork fork = new RtFork(
                new ApacheRequest(container.home()), repo, 1
            );
            fork.patch(RtForkTest.fork(patched));
            MatcherAssert.assertThat(
                "Values are not equal",
                new Fork.Smart(fork).organization(),
                Matchers.equalTo(patched)
            );
            MatcherAssert.assertThat(
                "Value is null",
                new Fork.Smart(fork).name(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * Create and return success MkAnswer object to test.
     * @param organization The organization of the fork
     * @return Success MkAnswer
     */
    private static MkAnswer.Simple answer(final String organization) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtForkTest.fork(organization).toString()
        );
    }

    /**
     * Create and return JsonObject to test.
     * @param organization The organization of the fork
     * @return JsonObject
     */
    private static JsonObject fork(final String organization) {
        return Json.createObjectBuilder()
            .add("organization", organization)
            .add("name", "nm")
            .build();
    }

}
