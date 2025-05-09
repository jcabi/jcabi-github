/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.http.request.FakeRequest;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 */
public final class RtCommitsComparisonTest {

    /**
     * RtCommitsComparison can fetch JSON.
     * @throws Exception If some problem inside
     * @checkstyle MultipleStringLiterals (75 lines)
     * @checkstyle ExecutableStatementCountCheck (75 lines)
     */
    @Test
    public void fetchesJson() throws Exception {
        final String sha = "fffffffffffffffffffffffffffffffffffffffe";
        final String filename = "bar/quux.txt";
        // @checkstyle MagicNumberCheck (3 lines)
        final int additions = 7;
        final int deletions = 2;
        final int changes = 9;
        final String patch = "some diff here";
        // @checkstyle LineLength (3 lines)
        final String bloburl = "https://api.jcabi-github.invalid/johndoe/my-repo/blob/fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt";
        final String rawurl = "https://api.jcabi-github.invalid/johndoe/my-repo/raw/fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt";
        final String contentsurl = "https://api.github.invalid/repos/johndoe/my-repo/contents/bar/quux.txt?ref=fffffffffffffffffffffffffffffffffffffffe";
        final CommitsComparison comparison = new RtCommitsComparison(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("base_commit", Json.createObjectBuilder())
                    .add("commits", Json.createArrayBuilder())
                    .add(
                        "files",
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder()
                                    .add("sha", sha)
                                    .add("filename", filename)
                                    .add("status", "added")
                                    .add("additions", additions)
                                    .add("deletions", deletions)
                                    .add("changes", changes)
                                    .add("patch", patch)
                                    .add("blob_url", bloburl)
                                    .add("raw_url", rawurl)
                                    .add("contents_url", contentsurl)
                                    .build()
                            )
                            .build()
                    )
                    .build().toString()
            ),
            RtCommitsComparisonTest.repo(),
            "6dcb09b5b57875f334f61aebed695e2e4193db51",
            "6dcb09b5b57875f334f61aebed695e2e4193db52"
        );
        final JsonObject json = comparison.json();
        MatcherAssert.assertThat(
            json.getJsonObject("base_commit"), Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            json.getJsonArray("commits"), Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            comparison.files(),
            Matchers.<FileChange>iterableWithSize(1)
        );
        final FileChange.Smart file = new FileChange.Smart(
            comparison.files().iterator().next()
        );
        MatcherAssert.assertThat(file.sha(), Matchers.equalTo(sha));
        MatcherAssert.assertThat(file.filename(), Matchers.equalTo(filename));
        MatcherAssert.assertThat(file.additions(), Matchers.equalTo(additions));
        MatcherAssert.assertThat(file.deletions(), Matchers.equalTo(deletions));
        MatcherAssert.assertThat(file.changes(), Matchers.equalTo(changes));
        MatcherAssert.assertThat(
            file.status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
        MatcherAssert.assertThat(
            file.patch(),
            Matchers.equalTo(Optional.of(patch))
        );
    }

    /**
     * Return repo for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGithub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
