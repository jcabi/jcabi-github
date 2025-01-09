/**
 * Copyright (c) 2013-2025, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkGists}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkGistsTest {

    /**
     * MkGists can work with gists.
     * @throws Exception If some problem inside
     */
    @Test
    public void worksWithMockedGists() throws Exception {
        final Gist gist = new MkGithub().gists().create(
            Collections.singletonMap("test-file-name.txt", "none"), false
        );
        final String file = "t.txt";
        gist.write(file, "hello, everybody!");
        MatcherAssert.assertThat(
            gist.read(file),
            Matchers.startsWith("hello, ")
        );
    }

    /**
     * This tests that the remove() method in MkGists is working fine.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void removesGistByIdentifier() throws Exception {
        final Gists gists = new MkGithub().gists();
        final Gist gist = gists.create(
            Collections.singletonMap("fileName.txt", "content"), false
        );
        MatcherAssert.assertThat(
            gists.iterate(),
            Matchers.hasItem(gist)
        );
        gists.remove(gist.identifier());
        MatcherAssert.assertThat(
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }
    /**
     * MkGists can work several gists.
     * Test to check issue #128
     * @throws Exception If some problem inside
     */
    @Test
    public void worksWithSeveralGists() throws Exception {
        final Gists gists = new MkGithub().gists();
        final Gist gist = gists.create(
            Collections.singletonMap("test-file-name.txt", "none"), false
        );
        final Gist othergist = gists.create(
            Collections.singletonMap("test-file-name2.txt", ""), false
        );
        final String file = "t.txt";
        gist.write(file, "hello, everybody!");
        othergist.write(file, "bye, everybody!");
        MatcherAssert.assertThat(
            gist.read(file),
            Matchers.startsWith("hello, ")
        );
        MatcherAssert.assertThat(
            othergist.read(file),
            Matchers.startsWith("bye, ")
        );
    }

    /**
     * Test starring and star-checking of a gist.
     * @throws Exception If some problem inside
     */
    @Test
    public void testStar() throws Exception {
        final Gist gist = new MkGithub().gists().create(
            Collections.singletonMap("file-name.txt", ""), false
        );
        MatcherAssert.assertThat(
            gist.starred(),
            Matchers.equalTo(false)
        );
        gist.star();
        MatcherAssert.assertThat(
            gist.starred(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Test unstarring and star-checking of a gist.
     * @throws Exception If some problem inside
     */
    @Test
    public void testUnstar() throws Exception {
        final Gist gist = new MkGithub().gists().create(
            Collections.singletonMap("file-name.txt", ""), false
        );
        MatcherAssert.assertThat(
            gist.starred(),
            Matchers.equalTo(false)
        );
        gist.star();
        MatcherAssert.assertThat(
            gist.starred(),
            Matchers.equalTo(true)
        );
        gist.unstar();
        MatcherAssert.assertThat(
            gist.starred(),
            Matchers.equalTo(false)
        );
    }

    /**
     * MkGists can create gists with empty files.
     * @throws IOException If some problem inside
     */
    @Test
    public void createGistWithEmptyFile() throws IOException {
        final String filename = "file.txt";
        final Gist gist = new MkGithub().gists().create(
            Collections.singletonMap(filename, ""), false
        );
        MatcherAssert.assertThat(
            gist.read(filename),
            Matchers.is(Matchers.emptyString())
        );
    }

}
