/**
 * Copyright (c) 2013-2014, JCabi.com
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link RepoCommits}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @todo #117 Add test getCommit() to check that commit actually got.
 *  See http://developer.github.com/v3/repos/commits/#get-a-single-commit.
 * @todo #439 Implement test compareCommitsPatch to check that
 *  two commits can be compared and result is in patch format.
 *  See http://developer.github.com/v3/repos/commits/#compare-two-commits.
 */
public class RtRepoCommitsITCase {
    /**
     * RtRepoCommits can fetch repo commits.
     * @throws Exception if there is no github key provided
     */
    @Test
    public final void fetchCommits() throws Exception {
        final Iterator<RepoCommit> iterator =
            RtRepoCommitsITCase.repo().commits().iterate().iterator();
        final List<String> shas = new ArrayList<String>(5);
        shas.add("1aa4af45aa2c56421c3d911a0a06da513a7316a0");
        shas.add("940dd5081fada0ead07762933036bf68a005cc40");
        shas.add("05940dbeaa6124e4a87d9829fb2fce80b713dcbe");
        shas.add("51cabb8e759852a6a40a7a2a76ef0afd4beef96d");
        shas.add("11bd4d527236f9cb211bc6667df06fde075beded");
        int found = 0;
        while (iterator.hasNext()) {
            if (shas.contains(iterator.next().sha())) {
                found += 1;
            }
        }
        MatcherAssert.assertThat(
            found,
            Matchers.equalTo(shas.size())
        );
    }

    /**
     * RtRepoCommits can compare two commits and return result in patch mode.
     * @throws Exception if there is no github key provided
     */
    @Test
    public final void compareCommitsPatch() throws Exception {
        // To be implemented
    }

    /**
     * RtRepoCommits can compare two commits and return result in diff mode.
     * @throws Exception if there is no github key provided
     */
    @Test
    public final void compareCommitsDiff() throws Exception {
        final String diff = RtRepoCommitsITCase.repo().commits().diff(
            "5339b8e35b",
            "9b2e6efde9"
        );
        MatcherAssert.assertThat(
            diff,
            Matchers.startsWith("diff --git")
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos().get(
            new Coordinates.Simple("jcabi", "jcabi-github")
        );
    }
}
