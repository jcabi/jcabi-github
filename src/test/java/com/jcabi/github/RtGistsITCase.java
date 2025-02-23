/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link Gists}.
 */
@OAuthScope(Scope.GIST)
public final class RtGistsITCase {
    /**
     * RtGists can create a gist.
     * @throws Exception If some problem inside
     */
    @Test
    public void createGist() throws Exception {
        final String filename = "filename.txt";
        final String content = "content of file";
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap(filename, content), false
        );
        final Gist.Smart smart = new Gist.Smart(gist);
        MatcherAssert.assertThat(
            smart.read(filename),
            Matchers.equalTo(content)
        );
        gists.remove(smart.identifier());
    }

    /**
     * RtGists can iterate all gists.
     * @throws Exception If some problem inside
     */
    @Test
    public void iterateGists() throws Exception {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("test.txt", "content"), false
        );
        MatcherAssert.assertThat(
            gists.iterate(),
            Matchers.hasItem(gist)
        );
        gists.remove(gist.identifier());
    }
    /**
     * RtGists can get a single gist.
     * @throws Exception If some problem inside
     */
    @Test
    public void singleGist() throws Exception {
        final String filename = "single-name.txt";
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap(filename, "body"), false
        );
        MatcherAssert.assertThat(
            gists.get(gist.identifier()).identifier(),
            Matchers.equalTo(gist.identifier())
        );
        gists.remove(gist.identifier());
    }
    /**
     * This tests that RtGists can remove a gist by name.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void removesGistByName() throws Exception {
        final Gists gists = RtGistsITCase.gists();
        final Gist gist = gists.create(
            Collections.singletonMap("fileName.txt", "content of test file"),
            false
        );
        MatcherAssert.assertThat(
            gists.iterate(),
            Matchers.notNullValue()
        );
        gists.remove(gist.json().getString("id"));
        MatcherAssert.assertThat(
            gists.iterate(),
            Matchers.not(Matchers.hasItem(gist))
        );
    }
    /**
     * Return gists to test.
     * @return Gists
     */
    private static Gists gists() {
        return new GithubIT().connect().gists();
    }
}
