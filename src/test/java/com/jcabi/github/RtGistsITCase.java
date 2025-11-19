/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link Gists}.
 */
@OAuthScope(OAuthScope.Scope.GIST)
public final class RtGistsITCase {
    @Test
    public void createGist() throws IOException {
        final String filename = "filename.txt";
        final String content = "content of file";
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap(filename, content), false
        );
        final Gist.Smart smart = new Gist.Smart(gist);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.read(filename),
            Matchers.equalTo(content)
        );
        gists.remove(smart.identifier());
    }

    @Test
    public void iterateGists() throws IOException {
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
    public void singleGist() throws IOException {
        final String filename = "single-name.txt";
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap(filename, "body"), false
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            gists.get(gist.identifier()).identifier(),
            Matchers.equalTo(gist.identifier())
        );
        gists.remove(gist.identifier());
    }

    @Test
    public void removesGistByName() throws IOException {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("fileName.txt", "content of test file"),
            false
        );
        MatcherAssert.assertThat(
            "Value is null",
            gists.iterate(),
            Matchers.notNullValue()
        );
        gists.remove(gist.json().getString("id"));
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }

    /**
     * Return gists to test.
     * @return Gists
     */
    private static Gists gists() {
        return new GitHubIT().connect().gists();
    }
}
