/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Gist;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkGist}.
 * @since 0.1
 */
public final class MkGistTest {
    /**
     * MkGist can read empty file.
     * @throws IOException If some problem inside
     */
    @Test
    public void readEmptyGistFile() throws IOException {
        // @checkstyle MultipleStringLiterals (1 lines)
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

    /**
     * MkGist can fork itself.
     * @throws IOException If some problem inside
     */
    @Test
    public void fork() throws IOException {
        final String filename = "file.txt";
        final Gist gist = new MkGitHub().gists().create(
            Collections.singletonMap(filename, ""), false
        );
        gist.write(filename, "Hello, github!");
        final Gist fork = gist.fork();
        MatcherAssert.assertThat(
            "Values are not equal",
            fork.read(filename),
            Matchers.equalTo(gist.read(filename))
        );
    }
}
