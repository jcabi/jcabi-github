/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 * @since 0.8
 */
public final class RtCommitsComparisonTest {

    /**
     * RtCommitsComparison can fetch JSON.
     * @checkstyle MultipleStringLiterals (75 lines)
     * @checkstyle ExecutableStatementCountCheck (75 lines)
     */
    @Test
    public void fetchesJson() throws IOException {
        final String sha = "fffffffffffffffffffffffffffffffffffffffe";
        final String filename = "bar/quux.txt";
        // @checkstyle MagicNumberCheck (3 lines)
        final int additions = 7;
        final int deletions = 2;
        final int changes = 9;
        final String patch = "some diff here";
        final String bloburl = String.join(
            "",
            "https://api.jcabi-github.invalid/johndoe/my-repo/blob/",
            "fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt"
        );
        final String rawurl = String.join(
            "",
            "https://api.jcabi-github.invalid/johndoe/my-repo/raw/",
            "fffffffffffffffffffffffffffffffffffffffe/bar/quux.txt"
        );
        final String contentsurl = String.join(
            "",
            "https://api.github.invalid/repos/johndoe/my-repo/contents/",
            "bar/quux.txt?ref=fffffffffffffffffffffffffffffffffffffffe"
        );
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
            "Value is null",
            json.getJsonObject("base_commit"),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Value is null",
            json.getJsonArray("commits"),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comparison.files(),
            Matchers.iterableWithSize(1)
        );
        final FileChange.Smart file = new FileChange.Smart(
            comparison.files().iterator().next()
        );
        MatcherAssert.assertThat(
            "Values are not equal", file.sha(), Matchers.equalTo(sha)
        );
        MatcherAssert.assertThat(
            "Values are not equal", file.filename(), Matchers.equalTo(filename)
        );
        MatcherAssert.assertThat(
            "Values are not equal", file.additions(), Matchers.equalTo(additions)
        );
        MatcherAssert.assertThat(
            "Values are not equal", file.deletions(), Matchers.equalTo(deletions)
        );
        MatcherAssert.assertThat(
            "Values are not equal", file.changes(), Matchers.equalTo(changes)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.patch(),
            Matchers.equalTo(Optional.of(patch))
        );
    }

    /**
     * Return repo for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGitHub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
