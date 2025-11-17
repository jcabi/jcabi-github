/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtSearchPagination}.
 *
 */
public final class RtSearchPaginationTest {

    /**
     * RtSearchPagination can iterate through items.
     */
    @Test
    public void iteratesItems() {
        final String key = "key";
        final String value = "value";
        final Iterable<String> pagination = new RtSearchPagination<>(
            new FakeRequest().withBody(
                Json.createObjectBuilder().add(
                    "items", Json.createArrayBuilder().add(
                        Json.createObjectBuilder().add(key, value)
                    )
                ).build().toString()
            ),
            "/search/path", "keywords", "sort", "order",
            object -> object.getString(key)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            pagination.iterator().next(), Matchers.equalTo(value)
        );
    }

}
