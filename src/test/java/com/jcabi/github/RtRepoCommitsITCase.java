/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RepoCommits}.
 *
 * <p>
 * WARNING: As there is no way to create Commit directly it was decided to use
 * real commits from jcabi-github repository for integration testing of
 * RtRepoCommits
 *
 */
@OAuthScope(OAuthScope.Scope.REPO)
public class RtRepoCommitsITCase {

    /**
     * RtRepoCommits can fetch repo commits.
     */
    @Test
    public final void fetchCommits() {
        final Iterator<RepoCommit> iterator =
            RtRepoCommitsITCase.repo().commits().iterate(
                new ArrayMap<String, String>()
                    .with("since", "2014-01-26T00:00:00Z")
                    .with("until", "2014-01-27T00:00:00Z")
            ).iterator();
        final List<String> shas = new ArrayList<>(5);
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
     */
    @Test
    public final void compareCommitsPatch() throws IOException {
        final String patch = RtRepoCommitsITCase.repo().commits().patch(
            "5339b8e35b",
            "9b2e6efde9"
        );
        MatcherAssert.assertThat(
            patch,
            Matchers.startsWith(
                "From 9b2e6efde94fabec5876dc481b38811e8b4e992f"
            )
        );
        MatcherAssert.assertThat(
            patch,
            Matchers.containsString(
                "Issue #430 RepoCommit interface was added"
            )
        );
    }

    /**
     * RtRepoCommits can compare two commits and return result in diff mode.
     */
    @Test
    public final void compareCommitsDiff() throws IOException {
        final String diff = RtRepoCommitsITCase.repo().commits().diff(
            "2b3814e",
            "b828dfa"
        );
        MatcherAssert.assertThat(
            diff,
            Matchers.startsWith("diff --git")
        );
    }

    /**
     * Check that commit actually got.
     */
    @Test
    public final void getCommit() {
        final String sha = "94e4216";
        MatcherAssert.assertThat(
            RtRepoCommitsITCase.repo().commits().get(sha).sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        return new GithubIT().connect().repos().get(
            new Coordinates.Simple("jcabi", "jcabi-github")
        );
    }
}
