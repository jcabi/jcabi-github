/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gist}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.GIST)
public final class RtGistITCase {

    /**
     * RtGist can text and write files.
     */
    @Test
    public void readsAndWritesGists() throws IOException {
        final Gists gists = RtGistITCase.github().gists();
        Gist.Smart smart = null;
        try {
            final String content = "content of file";
            final String filename = "filename.txt";
            final Gist gist = gists.create(
                Collections.singletonMap(filename, content), false
            );
            smart = new Gist.Smart(gist);
            final String file = smart.files().iterator().next();
            gist.write(file, "hey, works for you this way?");
            MatcherAssert.assertThat(
                "String does not start with expected value",
                gist.read(file),
                Matchers.startsWith("hey, works for ")
            );
        } finally {
            if (smart != null) {
                gists.remove(smart.identifier());
            }
        }
    }

    /**
     * RtGist can fork a gist.
     * @checkstyle MultipleStringLiterals (7 lines)
     * @checkstyle LocalFinalVariableName (11 lines)
     */
    @Test
    public void forksGist() throws IOException {
        final String filename = "filename1.txt";
        final String content = "content of file1";
        final Gists gists1 = RtGistITCase.github("failsafe.github.key").gists();
        final Gists gists2 = RtGistITCase.github("failsafe.github.key.second")
            .gists();
        final Gist gist = gists1.get(
            gists2.create(Collections.singletonMap(filename, content), false)
                .identifier()
        );
        final Gist forked = gist.fork();
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                forked.read(filename),
                Matchers.equalTo(content)
            );
        } finally {
            gists1.remove(forked.identifier());
            gists2.remove(gist.identifier());
        }
    }

    /**
     * Return github to test. Property "failsafe.github.key" is used
     * for authentication.
     * @return GitHub
     */
    private static GitHub github() {
        return RtGistITCase.github("failsafe.github.key");
    }

    /**
     * Return github to test.
     * @param property Name of a property with github key
     * @return GitHub
     */
    private static GitHub github(final String property) {
        final String key = System.getProperty(property);
        Assume.assumeThat(
            key,
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
        return new RtGitHub(key);
    }

}
