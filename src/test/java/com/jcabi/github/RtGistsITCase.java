/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link Gists}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.GIST)
final class RtGistsITCase {

    @Test
    void createGist() throws IOException {
        final String filename = "filename.txt";
        final String content = "content of file";
        final Gists gists = RtGistsITCase.gists();
        final Gist.Smart smart = new Gist.Smart(
            gists.create(
                Collections.singletonMap(filename, content), false
                )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.read(filename),
            Matchers.equalTo(content)
        );
        gists.remove(smart.identifier());
    }

    @Test
    void iterateGists() throws IOException {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("test.txt", "content"), false
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            gists.iterate(),
            Matchers.hasItem(gist)
        );
        gists.remove(gist.identifier());
    }

    @Test
    void singleGist() throws IOException {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("single-name.txt", "body"), false
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            gists.get(gist.identifier()).identifier(),
            Matchers.equalTo(gist.identifier())
        );
        gists.remove(gist.identifier());
    }

    @Test
    void removesGistByName() throws IOException {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("fileName.txt", "content of test file"),
            false
        );
        gists.remove(gist.json().getString("id"));
        MatcherAssert.assertThat(
            "Removed gist is still there",
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }

    /**
     * Return gists to test.
     * @return Gists
     */
    private static Gists gists() {
        return GitHubIT.connect().gists();
    }
}
