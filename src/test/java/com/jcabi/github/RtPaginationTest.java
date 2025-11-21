/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtPagination}.
 * @since 0.4
 */
@ExtendWith(RandomPort.class)
public final class RtPaginationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    public void jumpNextPage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtPaginationTest.simple("Hi Jeff")
                    .withHeader(
                        "Link",
                        "</s?page=3&per_page=100>; rel=\"next\""
                    )
            ).next(RtPaginationTest.simple("Hi Mark"))
                .start(RandomPort.port())
        ) {
            final Request request = new ApacheRequest(container.home());
            final RtPagination<JsonObject> page = new RtPagination<>(
                request,
                object -> object
            );
            final Iterator<JsonObject> iterator = page.iterator();
            MatcherAssert.assertThat(
                "String does not contain expected value",
                iterator.next().toString(),
                Matchers.containsString("Jeff")
            );
            MatcherAssert.assertThat(
                "String does not contain expected value",
                iterator.next().toString(),
                Matchers.containsString("Mark")
            );
            container.stop();
        }
    }

    @Test
    public void throwsIfNoMoreElement() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPaginationTest.simple("Hi there")).start(RandomPort.port())
        ) {
            final Request request = new ApacheRequest(container.home());
            final RtPagination<JsonObject> page = new RtPagination<>(
                request,
                object -> object
            );
            final Iterator<JsonObject> iterator = page.iterator();
            iterator.next();
            Assertions.assertThrows(
                java.util.NoSuchElementException.class,
                iterator::next,
                "Should throw when no more elements"
            );
            container.stop();
        }
    }

    /**
     * Create and return MkAnswer.Simple to test.
     * @param msg Message to build MkAnswer.Simple
     * @return MkAnswer.Simple
     */
    private static MkAnswer.Simple simple(final String msg) {
        final String message = Json.createArrayBuilder()
            .add(Json.createObjectBuilder().add("msg", msg))
            .build().toString();
        return new MkAnswer.Simple(HttpURLConnection.HTTP_OK, message);
    }
}
