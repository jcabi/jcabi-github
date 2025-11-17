/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Existence}.
 *
 * @since 0.38
 */
public final class ExistenceTest {

    /**
     * Existence can tell when the given JsonReadable exists.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void jsonExists() throws Exception {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.when(object.json()).thenReturn(
            Json.createObjectBuilder().build()
        );
        MatcherAssert.assertThat(
            new Existence(object).check(), Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Existence can tell when the given JsonReadable does not exist.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void jsonDoesNotExist() throws Exception {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.doThrow(new AssertionError()).when(object).json();
        MatcherAssert.assertThat(
            new Existence(object).check(), Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * Existends throws the possible IOException resulted from the server call.
     * @throws Exception If something goes wrong.
     */
    @Test(expected = IOException.class)
    public void rethrowsIOException() throws Exception {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.doThrow(new IOException()).when(object).json();
        new Existence(object).check();
    }

}
