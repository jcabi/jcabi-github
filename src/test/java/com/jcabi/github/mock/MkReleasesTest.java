/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleases}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
public final class MkReleasesTest {
    /**
     * MkReleases can fetch empty list of releases.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleases can fetch non-empty list of releases.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchNonEmptyListOfReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v1.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            // @checkstyle MultipleStringLiterals (1 line)
            releases.iterate().iterator().next().json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    /**
     * MkReleases can fetch a single release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchSingleRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        MatcherAssert.assertThat(releases.get(1), Matchers.notNullValue());
    }

    /**
     * MkReleases can create a release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v1.0.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            release.json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    /**
     * MkReleases can iterate through the releases.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void iteratesReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        releases.create("v1.0.1");
        releases.create("v1.0.2");
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(2)
        );
    }

    /**
     * MkReleases can be removed.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void canRemoveRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        releases.create("v1.1.1");
        releases.create("v1.1.2");
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(2)
        );
        releases.remove(1);
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(1)
        );
    }

    /**
     * MkReleases can find release by tag.
     * @throws Exception If some problem inside
     */
    @Test
    public void findsReleaseByTag() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v5.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            new Releases.Smart(releases).exists(tag),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new Release.Smart(new Releases.Smart(releases).find(tag)).tag(),
            Matchers.equalTo(tag)
        );
    }
}
