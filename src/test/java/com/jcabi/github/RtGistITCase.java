/**
 * Copyright (c) 2012-2013, JCabi.com
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gist}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class RtGistITCase {

    /**
     * RtGist can text and write files.
     * @throws Exception If some problem inside
     */
    @Test
    public void readsAndWritesGists() throws Exception {
        final Gist gist = RtGistITCase.gist();
        final String file = new Gist.Smart(gist).files().iterator().next();
        gist.write(file, "hey, works for you this way?");
        MatcherAssert.assertThat(
            gist.read(file),
            Matchers.startsWith("hey, works for ")
        );
    }

    /**
     * RtGist can fork a gist.
     * @throws Exception If some problem inside
     */
    @Test
    public void forkGist() throws Exception {
        final String key = System.getProperty("failsafe.github.key.second");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Gist gist = new RtGithub(key).gists().get(
            RtGistITCase.gist().name()
        );
        final String file = new Gist.Smart(gist).files().iterator().next();
        MatcherAssert.assertThat(
            gist.fork().read(file),
            Matchers.equalTo(gist.read(file))
        );
    }

    /**
     * Return gist to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Gist gist() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).gists().iterate().iterator().next();
    }

}
