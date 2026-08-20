/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 * @since 0.8
 */
final class RtCommitsComparisonTest {

    /**
     * SHA of the changed file.
     */
    private static final String SHA =
        "fffffffffffffffffffffffffffffffffffffffe";

    /**
     * Name of the changed file.
     */
    private static final String FILENAME = "bar/quux.txt";

    /**
     * Diff of the changed file.
     */
    private static final String PATCH = "some diff here";

    /**
     * Amount of added lines.
     */
    private static final int ADDITIONS = 7;

    /**
     * Amount of deleted lines.
     */
    private static final int DELETIONS = 2;

    /**
     * Amount of changed lines.
     */
    private static final int CHANGES = 9;

    /**
     * RtCommitsComparison can fetch the base commit.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesBaseCommit() throws IOException {
        MatcherAssert.assertThat(
            "Base commit is absent",
            RtCommitsComparisonTest.comparison().json()
                .getJsonObject("base_commit"),
            Matchers.notNullValue()
        );
    }

    /**
     * RtCommitsComparison can fetch the commits.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesCommits() throws IOException {
        MatcherAssert.assertThat(
            "Commits are absent",
            RtCommitsComparisonTest.comparison().json()
                .getJsonArray("commits"),
            Matchers.notNullValue()
        );
    }

    /**
     * RtCommitsComparison can fetch the file changes.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFiles() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            RtCommitsComparisonTest.comparison().files(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * RtCommitsComparison can fetch the SHA of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFileSha() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong SHA",
            RtCommitsComparisonTest.change().sha(),
            Matchers.equalTo(RtCommitsComparisonTest.SHA)
        );
    }

    /**
     * RtCommitsComparison can fetch the name of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFilename() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong name",
            RtCommitsComparisonTest.change().filename(),
            Matchers.equalTo(RtCommitsComparisonTest.FILENAME)
        );
    }

    /**
     * RtCommitsComparison can fetch the additions of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFileAdditions() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of additions",
            RtCommitsComparisonTest.change().additions(),
            Matchers.equalTo(RtCommitsComparisonTest.ADDITIONS)
        );
    }

    /**
     * RtCommitsComparison can fetch the deletions of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFileDeletions() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of deletions",
            RtCommitsComparisonTest.change().deletions(),
            Matchers.equalTo(RtCommitsComparisonTest.DELETIONS)
        );
    }

    /**
     * RtCommitsComparison can fetch the changes of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFileChanges() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of changes",
            RtCommitsComparisonTest.change().changes(),
            Matchers.equalTo(RtCommitsComparisonTest.CHANGES)
        );
    }

    /**
     * RtCommitsComparison can fetch the status of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFileStatus() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong status",
            RtCommitsComparisonTest.change().status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
    }

    /**
     * RtCommitsComparison can fetch the patch of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void fetchesFilePatch() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong patch",
            RtCommitsComparisonTest.change().patch(),
            Matchers.equalTo(Optional.of(RtCommitsComparisonTest.PATCH))
        );
    }

    private static FileChange.Smart change() throws IOException {
        return new FileChange.Smart(
            RtCommitsComparisonTest.comparison().files().iterator().next()
        );
    }

    private static CommitsComparison comparison() {
        return new RtCommitsComparison(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("base_commit", Json.createObjectBuilder())
                    .add("commits", Json.createArrayBuilder()).add(
                        "files",
                        Json.createArrayBuilder().add(
                            Json.createObjectBuilder()
                                .add("sha", RtCommitsComparisonTest.SHA).add(
                                    "filename",
                                    RtCommitsComparisonTest.FILENAME
                                )
                                .add("status", "added").add(
                                    "additions",
                                    RtCommitsComparisonTest.ADDITIONS
                                ).add(
                                    "deletions",
                                    RtCommitsComparisonTest.DELETIONS
                                ).add(
                                    "changes",
                                    RtCommitsComparisonTest.CHANGES
                                ).add(
                                    "patch", RtCommitsComparisonTest.PATCH
                                ).add(
                                    "blob_url",
                                    String.join(
                                        "",
                                        "https://api.jcabi-github.invalid/",
                                        "johndoe/my-repo/blob/",
                                        RtCommitsComparisonTest.SHA,
                                        "/bar/quux.txt"
                                    )
                                ).add(
                                    "raw_url",
                                    String.join(
                                        "",
                                        "https://api.jcabi-github.invalid/",
                                        "johndoe/my-repo/raw/",
                                        RtCommitsComparisonTest.SHA,
                                        "/bar/quux.txt"
                                    )
                                ).add(
                                    "contents_url",
                                    String.join(
                                        "",
                                        "https://api.github.invalid/repos/",
                                        "johndoe/my-repo/contents/",
                                        "bar/quux.txt?ref=",
                                        RtCommitsComparisonTest.SHA
                                    )
                                )
                                .build()
                        ).build()
                    )
                    .build().toString()
            ),
            RtCommitsComparisonTest.repo(),
            "6dcb09b5b57875f334f61aebed695e2e4193db51",
            "6dcb09b5b57875f334f61aebed695e2e4193db52"
        );
    }

    private static Repo repo() {
        return new RtGitHub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }
}
