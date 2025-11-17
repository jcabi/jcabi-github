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
     */
    @Test
    public void jsonExists() throws IOException {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.when(object.json()).thenReturn(
            Json.createObjectBuilder().build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new Existence(object).check(), Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Existence can tell when the given JsonReadable does not exist.
     */
    @Test
    public void jsonDoesNotExist() throws IOException {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.doThrow(new AssertionError()).when(object).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Existence(object).check(), Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * Existends throws the possible IOException resulted from the server call.
     */
    @Test(expected = IOException.class)
    public void rethrowsIoException() throws IOException {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.doThrow(new IOException()).when(object).json();
        new Existence(object).check();
    }

}
