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

import com.rexsl.test.Request;
import com.rexsl.test.request.ApacheRequest;
import java.io.IOException;
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
 */
public class RtRepoCommitsITCase {

    public final transient String requestUrl = "https://api.github.com/";
    public final transient String user = "jcabi";
    public final transient String repo = "jcabi-github";

    /**
     * RtRepoCommits can fetch commits.
     */
    @Test
    public void fetchCommits() {
        final Request request = new ApacheRequest(requestUrl);
        final Coordinates coordinates = new Coordinates.Simple(user, repo);

        RepoCommits repoCommits = new RtRepoCommits(request, coordinates);
        Iterator<Commit> iterator = repoCommits.iterate().iterator();

        final List<String> shas = new ArrayList<String>();
        shas.add("1aa4af45aa2c56421c3d911a0a06da513a7316a0");
        shas.add("940dd5081fada0ead07762933036bf68a005cc40");
        shas.add("05940dbeaa6124e4a87d9829fb2fce80b713dcbe");
        shas.add("51cabb8e759852a6a40a7a2a76ef0afd4beef96d");
        shas.add("11bd4d527236f9cb211bc6667df06fde075beded");
        int existsCount = 0;

        while (iterator.hasNext()) {
            if (shas.contains(iterator.next().sha())) {
                existsCount++;
            }
        }

        MatcherAssert.assertThat(existsCount,
            Matchers.equalTo(shas.size())
        );
    }

    /**
     * RtRepoCommits can get commit.
     */
    @Test
    public void getCommit() throws IOException {
        final Request request = new ApacheRequest(requestUrl);
        final Coordinates coordinates = new Coordinates.Simple(user, repo);
        final RepoCommits repoCommits = new RtRepoCommits(request, coordinates);

        final String sha = "51cabb8e759852a6a40a7a2a76ef0afd4beef96d";
        final String expectedName = "\"Alexander Sinyagin\"";
        final Commit commit = repoCommits.get(sha);

        MatcherAssert.assertThat(commit.json()
            //.getJsonObject("commit")
            .getJsonObject("author")
            .get("name")
            .toString(),
            Matchers.equalTo(expectedName)
        );
    }
}
