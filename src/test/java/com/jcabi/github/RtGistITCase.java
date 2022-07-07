/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.jcabi.github.OAuthScope.Scope;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gist}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 */
@OAuthScope(Scope.GIST)
public final class RtGistITCase {

    /**
     * RtGist can text and write files.
     * @throws Exception If some problem inside
     */
    @Test
    public void readsAndWritesGists() throws Exception {
        final String filename = "filename.txt";
        final String content = "content of file";
        final Gists gists = RtGistITCase.github().gists();
        Gist.Smart smart = null;
        try {
            final Gist gist = gists.create(
                Collections.singletonMap(filename, content), false
            );
            smart = new Gist.Smart(gist);
            final String file = smart.files().iterator().next();
            gist.write(file, "hey, works for you this way?");
            MatcherAssert.assertThat(
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
     * @throws Exception If some problem inside
     * @checkstyle MultipleStringLiterals (7 lines)
     * @checkstyle LocalFinalVariableName (11 lines)
     */
    @Test
    public void forksGist() throws Exception {
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
     * @return Github
     * @throws Exception If some problem inside
     */
    private static Github github() throws Exception {
        return RtGistITCase.github("failsafe.github.key");
    }

    /**
     * Return github to test.
     * @param property Name of a property with github key
     * @return Github
     * @throws Exception If some problem inside
     */
    private static Github github(final String property) throws Exception {
        final String key = System.getProperty(property);
        Assume.assumeThat(key, Matchers.not(Matchers.isEmptyOrNullString()));
        return new RtGithub(key);
    }

}
