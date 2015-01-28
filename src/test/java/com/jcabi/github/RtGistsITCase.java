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
package com.jcabi.github;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gists}. This test requires OAuth scope gist.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 */
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
     * @throws Exception If some problem inside
     */
    private static Gists gists() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).gists();
    }
}
