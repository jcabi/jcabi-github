/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
