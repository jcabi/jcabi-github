/**
 * Copyright (c) 2012-2013, JCabi.com
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

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for Markdown.
 * @author Pavel Danilchenko (mits0908@gmail.com)
 * @version $Id$
 */
public final class GhMarkdownTest {

    /**
     * Markdown can return string when pass null to render method parameter.
     * @throws Exception If some problem inside
     */
    @Test
    public void returnStringWhenPassNullToRender() throws Exception {
        final String notNull = "JSON can't be NULL";
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.when(markdown.render(null)).thenReturn(notNull);
        MatcherAssert.assertThat(
            markdown.render(null), Matchers.containsString(notNull)
        );
        Mockito.verify(markdown).render(null);
    }

    /**
     * Markdown can return string when pass null to raw method parameter.
     * @throws Exception If some problem inside
     */
    @Test
    public void returnStringWhenPassNullToRaw() throws Exception {
        final String nutNull = "Markdown can't be NULL";
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.when(markdown.raw(null)).thenReturn(nutNull);
        MatcherAssert.assertThat(
            markdown.raw(null), Matchers.containsString(nutNull)
        );
        Mockito.verify(markdown).raw(null);
    }

    /**
     * Markdown can return string when invoke render method.
     * @todo #32:0.2hr add verification that render() returns correct string
     * @throws Exception If some problem inside
     */
    @Test
    public void returnStringWhenInvokeRender() throws Exception {
        final String first = "color";
        final String second = "bla";
        final Markdown markdown = Mockito.mock(Markdown.class);
        markdown.render(Json.createObjectBuilder().add(first, second).build());
        Mockito.verify(markdown).render(
            Json.createObjectBuilder().add(first, second).build()
        );
    }

    /**
     * Markdown can return string when invoke raw method.
     * @todo #32:0.2hr add verification that raw() returns correct string
     * @throws Exception If some problem inside
     */
    @Test
    public void returnStringWhenInvokeRaw() throws Exception {
        final String dump = "some string";
        final Markdown markdown = Mockito.mock(Markdown.class);
        markdown.raw(dump);
        Mockito.verify(markdown).raw(dump);
    }
}
