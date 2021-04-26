/**
 * Copyright (c) 2013-2020, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Existence}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.38
 */
final class ExistenceTest {

    /**
     * Existence can tell when the given JsonReadable exists.
     * @throws Exception If something goes wrong.
     */
    @Test
    void jsonExists() throws Exception {
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
    void jsonDoesNotExist() throws Exception {
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
    @Test
    void rethrowsIOException() throws IOException {
        final JsonReadable object = Mockito.mock(JsonReadable.class);
        Mockito.doThrow(new IOException()).when(object).json();
        Assertions.assertThrows(
            IOException.class,
            () -> {
                new Existence(object).check();
            }
        );
    }

}
