/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtPagination}.
 *
 */
public final class RtPaginationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPagination can jump to next page of results.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void jumpNextPage() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                RtPaginationTest.simple("Hi Jeff")
                    .withHeader(
                        "Link",
                        "</s?page=3&per_page=100>; rel=\"next\""
                    )
            ).next(RtPaginationTest.simple("Hi Mark"))
            .start(this.resource.port())
        ) {
            final Request request = new ApacheRequest(container.home());
            final RtPagination<JsonObject> page = new RtPagination<>(
                request,
                object -> object
            );
            final Iterator<JsonObject> iterator = page.iterator();
            MatcherAssert.assertThat(
                iterator.next().toString(),
                Matchers.containsString("Jeff")
            );
            MatcherAssert.assertThat(
                iterator.next().toString(),
                Matchers.containsString("Mark")
            );
            container.stop();
        }
    }

    /**
     * RtPagination can throw if there is no more elements in pagination.
     *
     * @throws Exception if there is any problem
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsIfNoMoreElement() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer()
                .next(simple("Hi there")).start(this.resource.port())
        ) {
            final Request request = new ApacheRequest(container.home());
            final RtPagination<JsonObject> page = new RtPagination<>(
                request,
                object -> object
            );
            final Iterator<JsonObject> iterator = page.iterator();
            iterator.next();
            MatcherAssert.assertThat(
                iterator.next(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * Create and return MkAnswer.Simple to test.
     * @param msg Message to build MkAnswer.Simple
     * @return MkAnswer.Simple
     */
    private static  MkAnswer.Simple simple(final String msg) {
        final String message = Json.createArrayBuilder()
            .add(Json.createObjectBuilder().add("msg", msg))
            .build().toString();
        return new MkAnswer.Simple(HttpURLConnection.HTTP_OK, message);
    }
}
