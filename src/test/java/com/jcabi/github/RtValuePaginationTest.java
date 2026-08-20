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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtValuePagination}.
 * @since 0.4
 */
@ExtendWith(RandomPort.class)
final class RtValuePaginationTest {

    @Test
    void readsFirstPage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtValuePaginationTest.linked())
                .next(RtValuePaginationTest.simple("Judy", "Jessy"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "First page is different",
                RtValuePaginationTest.page(container)
                    .iterator().next().toString(),
                Matchers.allOf(
                    Matchers.containsString("Jeff"),
                    Matchers.containsString("Mark")
                )
            );
        }
    }

    @Test
    void jumpNextPage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtValuePaginationTest.linked())
                .next(RtValuePaginationTest.simple("Judy", "Jessy"))
                .start(RandomPort.port())
        ) {
            final Iterator<JsonObject> iterator =
                RtValuePaginationTest.page(container).iterator();
            iterator.next();
            MatcherAssert.assertThat(
                "Next page is different",
                iterator.next().toString(),
                Matchers.allOf(
                    Matchers.containsString("Judy"),
                    Matchers.containsString("Jessy")
                )
            );
        }
    }

    @Test
    void throwsIfNoMoreElement() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            RtValuePaginationTest.simple("other Jeff", "other Mark")
        ).start(RandomPort.port());
        try {
            final RtValuePagination<JsonObject, JsonArray> page =
                new RtValuePagination<>(
                    new ApacheRequest(container.home()),
                    object -> Json.createObjectBuilder()
                        .add("id3", object.getString(0))
                        .add("id4", object.getString(1))
                        .build()
                );
            final Iterator<JsonObject> iterator = page.iterator();
            iterator.next();
            Assertions.assertThrows(
                NoSuchElementException.class,
                iterator::next,
                "Should throw when no more elements"
            );
        } finally {
            container.stop();
        }
    }

    private static RtValuePagination<JsonObject, JsonArray> page(
        final MkContainer container
    ) throws IOException {
        return new RtValuePagination<>(
            new ApacheRequest(container.home()),
            object -> Json.createObjectBuilder()
                .add("id1", object.getString(0))
                .add("id2", object.getString(1))
                .build()
        );
    }

    private static MkAnswer.Simple linked() {
        return RtValuePaginationTest.simple("Jeff", "Mark")
            .withHeader("Link", "</s?page=3&per_page=100>; rel=\"next\"");
    }

    private static MkAnswer.Simple simple(final String one,
        final String another
    ) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(Json.createArrayBuilder().add(one).add(another))
                .build().toString()
        );
    }
}
