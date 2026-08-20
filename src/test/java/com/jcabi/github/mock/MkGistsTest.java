/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGists}.
 * @since 0.1
 */
final class MkGistsTest {

    /**
     * Name of the file inside a gist.
     */
    private static final String FILE = "t.txt";

    @Test
    void worksWithMockedGists() throws IOException {
        final Gist gist = MkGistsTest.gist(new MkGitHub().gists());
        gist.write(MkGistsTest.FILE, "hello, everybody!");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            gist.read(MkGistsTest.FILE),
            Matchers.startsWith("hello, ")
        );
    }

    /**
     * This tests that the remove() method in MkGists is working fine.
     */
    @Test
    void createsGist() throws IOException {
        final Gists gists = new MkGitHub().gists();
        MatcherAssert.assertThat(
            "Created gist is not in the collection",
            gists.iterate(),
            Matchers.hasItem(MkGistsTest.gist(gists))
        );
    }

    /**
     * This tests that the remove() method in MkGists is working fine.
     */
    @Test
    void removesGistByIdentifier() throws IOException {
        final Gists gists = new MkGitHub().gists();
        final Gist gist = MkGistsTest.gist(gists);
        gists.remove(gist.identifier());
        MatcherAssert.assertThat(
            "Removed gist is still in the collection",
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }

    /**
     * MkGists can work several gists.
     * Test to check issue #128
     */
    @Test
    void worksWithSeveralGists() throws IOException {
        final Gists gists = new MkGitHub().gists();
        final Gist gist = MkGistsTest.gist(gists);
        final Gist other = MkGistsTest.gist(gists);
        gist.write(MkGistsTest.FILE, "hello, everybody!");
        other.write(MkGistsTest.FILE, "bye, everybody!");
        MatcherAssert.assertThat(
            "First gist has a wrong content",
            gist.read(MkGistsTest.FILE),
            Matchers.startsWith("hello, ")
        );
    }

    /**
     * MkGists can work several gists.
     * Test to check issue #128
     */
    @Test
    void keepsSeveralGistsApart() throws IOException {
        final Gists gists = new MkGitHub().gists();
        final Gist gist = MkGistsTest.gist(gists);
        final Gist other = MkGistsTest.gist(gists);
        gist.write(MkGistsTest.FILE, "hello, everybody!");
        other.write(MkGistsTest.FILE, "bye, everybody!");
        MatcherAssert.assertThat(
            "Second gist has a wrong content",
            other.read(MkGistsTest.FILE),
            Matchers.startsWith("bye, ")
        );
    }

    /**
     * Test that a fresh gist is not starred.
     */
    @Test
    void createsUnstarredGist() throws IOException {
        MatcherAssert.assertThat(
            "Fresh gist is starred",
            MkGistsTest.gist(new MkGitHub().gists()).starred(),
            Matchers.equalTo(false)
        );
    }

    /**
     * Test starring and star-checking of a gist.
     */
    @Test
    void starsGist() throws IOException {
        final Gist gist = MkGistsTest.gist(new MkGitHub().gists());
        gist.star();
        MatcherAssert.assertThat(
            "Gist is not starred",
            gist.starred(),
            Matchers.equalTo(true)
        );
    }

    /**
     * Test unstarring and star-checking of a gist.
     */
    @Test
    void unstarsGist() throws IOException {
        final Gist gist = MkGistsTest.gist(new MkGitHub().gists());
        gist.star();
        gist.unstar();
        MatcherAssert.assertThat(
            "Gist is still starred",
            gist.starred(),
            Matchers.equalTo(false)
        );
    }

    /**
     * MkGists can create gists with empty files.
     * @throws IOException If some problem inside
     */
    @Test
    void createGistWithEmptyFile() throws IOException {
        final String filename = "file.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkGitHub().gists().create(
                Collections.singletonMap(filename, ""), false
            ).read(filename),
            Matchers.is(Matchers.emptyString())
        );
    }

    private static Gist gist(final Gists gists) throws IOException {
        return gists.create(
            Collections.singletonMap("test-file-name.txt", "none"), false
        );
    }
}
