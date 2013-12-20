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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created with IntelliJ IDEA.
 * @author Pavel Danilchenko (mits0908@gmail.com)
 * @version $Id$
 * Date: 12/20/13
 */
public final class GhMarkdownTest {

    /**
     * Verify returns value for render() method if pass null.
     * @throws Exception If some problem inside
     */
    @Test
    public void renderNullReturnsString() throws Exception {
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.doReturn("JSON can't be NULL").when(markdown).render(null);
    }

    /**
     * Verify returns value for raw() method if pass null.
     * @throws Exception If some problem inside
     */
    @Test
    public void rawNullReturnsString() throws Exception {
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.doReturn("Markdown can't be NULL").when(markdown).raw(null);
    }

    /**
     * Verify return string.
     * @todo #32:15min add verification that render() returns correct string
     * @throws Exception If some problem inside
     */
    @Test
    public void checkRender() throws Exception {
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.doReturn("string1").when(markdown).render(
            Json.createObjectBuilder().add("bla1", "bla2").build()
        );
    }

    /**
     * Verify return string.
     * @todo #32:15min add verification that raw() returns correct string
     * @throws Exception If some problem inside
     */
    @Test
    public void checkRaw() throws Exception {
        final Markdown markdown = Mockito.mock(Markdown.class);
        Mockito.doReturn("string2").when(markdown).raw("bla3");
    }
}
