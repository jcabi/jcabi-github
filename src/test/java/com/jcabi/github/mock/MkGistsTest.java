/*
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
     */
    @Test
    public void worksWithMockedGists() throws IOException {
        final Gist gist = new MkGitHub().gists().create(
            Collections.singletonMap("test-file-name.txt", "none"), false
        );
        final String file = "t.txt";
        gist.write(file, "hello, everybody!");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            gist.read(file),
            Matchers.startsWith("hello, ")
        );
    }

    /**
     * This tests that the remove() method in MkGists is working fine.
     */
    @Test
    public void removesGistByIdentifier() throws IOException {
        final Gists gists = new MkGitHub().gists();
        final Gist gist = gists.create(
            Collections.singletonMap("fileName.txt", "content"), false
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            gists.iterate(),
            Matchers.hasItem(gist)
        );
        gists.remove(gist.identifier());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }
    /**
     * MkGists can work several gists.
     * Test to check issue #128
     */

    @Test
    public void worksWithSeveralGists() throws IOException {
        final Gists gists = new MkGitHub().gists();
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
            "String does not start with expected value",
            gist.read(file),
            Matchers.startsWith("hello, ")
        );
        MatcherAssert.assertThat(
            "String does not start with expected value",
            othergist.read(file),
            Matchers.startsWith("bye, ")
        );
    }

    /**
     * Test starring and star-checking of a gist.
     */
    @Test
    public void testStar() throws IOException {
        final Gist gist = new MkGitHub().gists().create(
            Collections.singletonMap("file-name.txt", ""), false
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            gist.starred(),
            Matchers.equalTo(false)
        );
        gist.star();
        MatcherAssert.assertThat(
            "Values are not equal",
            gist.starred(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Test unstarring and star-checking of a gist.
     */
    @Test
    public void testUnstar() throws IOException {
        final Gist gist = new MkGitHub().gists().create(
            Collections.singletonMap("file-name.txt", ""), false
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            gist.starred(),
            Matchers.equalTo(false)
        );
        gist.star();
        MatcherAssert.assertThat(
            "Values are not equal",
            gist.starred(),
            Matchers.equalTo(true)
        );
        gist.unstar();
        MatcherAssert.assertThat(
            "Values are not equal",
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
        final Gist gist = new MkGitHub().gists().create(
            Collections.singletonMap(filename, ""), false
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            gist.read(filename),
            Matchers.is(Matchers.emptyString())
        );
    }

}
