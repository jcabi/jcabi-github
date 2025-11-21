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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtValuePagination}.
 * @since 0.4
 */
@ExtendWith(RandomPort.class)
public final class RtValuePaginationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    @Test
    public void jumpNextPage() throws IOException {
        final String jeff = "Jeff";
        final String mark = "Mark";
        final String judy = "Judy";
        final String jessy = "Jessy";
        final MkContainer container = new MkGrizzlyContainer().next(
            RtValuePaginationTest.simple(jeff, mark)
                .withHeader("Link", "</s?page=3&per_page=100>; rel=\"next\"")
        ).next(RtValuePaginationTest.simple(judy, jessy))
            .start(RandomPort.port());
        final Request request = new ApacheRequest(container.home());
        final RtValuePagination<JsonObject, JsonArray> page =
            new RtValuePagination<>(
                request,
                object -> Json.createObjectBuilder()
                    .add("id1", object.getString(0))
                    .add("id2", object.getString(1))
                    .build()
            );
        final Iterator<JsonObject> iterator = page.iterator();
        MatcherAssert.assertThat(
            "String does not contain expected value",
            iterator.next().toString(),
            Matchers.allOf(
                Matchers.containsString(jeff),
                Matchers.containsString(mark)
            )
        );
        MatcherAssert.assertThat(
            "String does not contain expected value",
            iterator.next().toString(),
            Matchers.allOf(
                Matchers.containsString(judy),
                Matchers.containsString(jessy)
            )
        );
        container.stop();
    }

    // TODO: Convert to Assertions.assertThrows(NoSuchElementException.class, () -> { ... });
    @Test
    public void throwsIfNoMoreElement() throws IOException {
        final String jeff = "other Jeff";
        final String mark = "other Mark";
        final MkContainer container = new MkGrizzlyContainer().next(
            RtValuePaginationTest.simple(jeff, mark)
        ).start(RandomPort.port());
        try {
            final Request request = new ApacheRequest(container.home());
            final RtValuePagination<JsonObject, JsonArray> page =
                new RtValuePagination<>(
                    request,
                    object -> Json.createObjectBuilder()
                        .add("id3", object.getString(0))
                        .add("id4", object.getString(1))
                        .build()
                );
            final Iterator<JsonObject> iterator = page.iterator();
            iterator.next();
            MatcherAssert.assertThat(
                "Value is null",
                iterator.next(),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return MkAnswer.Simple to test.
     * @param one First array element
     * @param another Second array element
     * @return MkAnswer.Simple
     */
    private static MkAnswer.Simple simple(final String one,
        final String another
    ) {
        final String message = Json.createArrayBuilder()
            .add(Json.createArrayBuilder().add(one).add(another))
            .build().toString();
        return new MkAnswer.Simple(HttpURLConnection.HTTP_OK, message);
    }
}
