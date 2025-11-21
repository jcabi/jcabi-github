/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Smarts}.
 * @since 0.5
 */
final class SmartsTest {

    @Test
    void decoratesObjectsOnFly() throws IOException {
        final Comment origin = Mockito.mock(Comment.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("body", "hello, world!").build()
        ).when(origin).json();
        final Iterable<Comment.Smart> comments = new Smarts<>(
            Collections.singletonList(origin)
        );
        MatcherAssert.assertThat(
            "String does not end with expected value",
            comments.iterator().next().body(),
            Matchers.endsWith("world!")
        );
    }

}
