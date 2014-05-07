/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.github.Coordinates;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkRepoCommits).
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class MkRepoCommitsTest {

    /**
     * MkRepoCommits can return commits' iterator.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnIterator() throws IOException {
        final String user =  "testuser1";
        MatcherAssert.assertThat(
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo1")
            ).iterate(Collections.<String, String>emptyMap()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can get a commit.
     * @throws IOException if some problem inside
     */
    @Test
    public void getCommit() throws IOException {
        final String user =  "testuser2";
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db5e";
        MatcherAssert.assertThat(
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo2")
            ).get(sha),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can compare commits.
     * @throws IOException if some problem inside
     */
    @Test
    public void canCompare() throws IOException {
        final String user =  "testuser3";
        MatcherAssert.assertThat(
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo3")
            ).compare("5339b8e35b", "9b2e6efde9"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepoCommits can compare commits as diff fromat.
     * @throws Exception if some problem inside
     */
    @Test
    public void canCompareAsDiffFormat() throws Exception {
        final String user =  "testuser4";
        final String base =  "c034abc";
        final String head =  "a0ed832";
        MatcherAssert.assertThat(
            new MkRepoCommits(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo4")
            ).diff(base, head),
            Matchers.stringContainsInOrder(Arrays.asList(base, head))
        );
    }

    /**
     * MkRepoCommits can compare commits as patch.
     * @throws Exception if some problem inside
     */
    @Test
    public void canCompareAsPatch() throws Exception {
        final String user =  "testuser5";
        final String head = "9b2e6e7de9";
        MatcherAssert.assertThat(
            new MkRepoCommits(
                new MkStorage.InFile(), user,
                new Coordinates.Simple(user, "testrepo5")
            ).patch("5c39b8e35b", head),
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    head, "From:", "Date:", "Subject:", "files changed",
                    "insertions", "deletions"
                )
            )
        );
    }
}
