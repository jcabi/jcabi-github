/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.util.Collections;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Smarts}.
 */
public final class SmartsTest {

    /**
     * Smarts can make instances of Comment.Smart.
     * @throws Exception If some problem inside
     */
    @Test
    public void decoratesObjectsOnFly() throws Exception {
        final Comment origin = Mockito.mock(Comment.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("body", "hello, world!").build()
        ).when(origin).json();
        final Iterable<Comment.Smart> comments = new Smarts<>(
            Collections.singletonList(origin)
        );
        MatcherAssert.assertThat(
            comments.iterator().next().body(),
            Matchers.endsWith("world!")
        );
    }

}
