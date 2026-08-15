/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
final class RtPaginationTest {

    @Test
    void readsFirstPage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPaginationTest.linked())
                .next(RtPaginationTest.simple("Hi Mark"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "First page is different",
                RtPaginationTest.page(container).iterator().next().toString(),
                Matchers.containsString("Jeff")
            );
        }
    }

    @Test
    void jumpNextPage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPaginationTest.linked())
                .next(RtPaginationTest.simple("Hi Mark"))
                .start(RandomPort.port())
        ) {
            final Iterator<JsonObject> iterator =
                RtPaginationTest.page(container).iterator();
            iterator.next();
            MatcherAssert.assertThat(
                "Next page is different",
                iterator.next().toString(),
                Matchers.containsString("Mark")
            );
        }
    }

    @Test
    void throwsIfNoMoreElement() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPaginationTest.simple("Hi there")).start(RandomPort.port())
        ) {
            final RtPagination<JsonObject> page = new RtPagination<>(
                new ApacheRequest(container.home()),
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
     * Pagination served by the given container.
     * @param container Container to serve the pages
     * @return Pagination
     * @throws IOException If there is any I/O problem
     */
    private static RtPagination<JsonObject> page(final MkContainer container)
        throws IOException {
        return new RtPagination<>(
            new ApacheRequest(container.home()),
            object -> object
        );
    }

    /**
     * Answer with a link to the next page.
     * @return Answer
     */
    private static MkAnswer.Simple linked() {
        return RtPaginationTest.simple("Hi Jeff").withHeader(
            "Link",
            "</s?page=3&per_page=100>; rel=\"next\""
        );
    }

    /**
     * Create and return MkAnswer.Simple to test.
     * @param msg Message to build MkAnswer.Simple
     * @return MkAnswer.Simple
     */
    private static MkAnswer.Simple simple(final String msg) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("msg", msg))
                .build().toString()
        );
    }
}
