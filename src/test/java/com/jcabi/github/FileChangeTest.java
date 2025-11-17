/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkFileChange;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link FileChange}.
 * @since 0.24
 */
public final class FileChangeTest {
    /**
     * FileChange.Smart can get the status of the file.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void getsStatus() throws IOException {
        final String status = "status";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(status, "added").status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(status, "modified").status(),
            Matchers.equalTo(FileChange.Status.MODIFIED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(status, "removed").status(),
            Matchers.equalTo(FileChange.Status.REMOVED)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(status, "renamed").status(),
            Matchers.equalTo(FileChange.Status.RENAMED)
        );
    }

    /**
     * FileChange.Smart can get the filename of the file.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsFilename() throws IOException {
        final String filename = "foo/bar.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange("filename", filename).filename(),
            Matchers.equalTo(filename)
        );
    }

    /**
     * FileChange.Smart can get the commit SHA of the file.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsSha() throws IOException {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db51";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange("sha", sha).sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * FileChange.Smart can get the file's count of lines added.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsAdditions() throws IOException {
        // @checkstyle MagicNumberCheck (1 line)
        final int adds = 42;
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.intFileChange("additions", adds).additions(),
            Matchers.equalTo(adds)
        );
    }

    /**
     * FileChange.Smart can get the file's count of lines deleted.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsDeletions() throws IOException {
        // @checkstyle MagicNumberCheck (1 line)
        final int deletions = 97;
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.intFileChange("deletions", deletions).deletions(),
            Matchers.equalTo(deletions)
        );
    }

    /**
     * FileChange.Smart can get the file's count of lines changed.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsChanges() throws IOException {
        // @checkstyle MagicNumberCheck (1 line)
        final int changes = 11;
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.intFileChange("changes", changes).changes(),
            Matchers.equalTo(changes)
        );
    }

    /**
     * FileChange.Smart does not fail when attempting to get the
     * it is absent (which is normally the case for binary files).
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsAbsentPatch() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new FileChange.Smart(
                new MkFileChange(
                    Json.createObjectBuilder().build()
                )
            ).patch(),
            Matchers.equalTo(Optional.<String>absent())
        );
    }

    /**
     * FileChange.Smart can get the file's diff patch string when
     * it is present (which is normally the case for text files).
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsPresentPatch() throws IOException {
        // @checkstyle LineLength (1 line)
        final String patch = "@@ -120,7 +120,7 @@ class Test1 @@ -1000,7 +1000,7 @@ class Test1";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(
                "patch",
                patch
            ).patch(),
            Matchers.equalTo(Optional.of(patch))
        );
    }

    /**
     * FileChange.Smart can get the URL for the file's raw content.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsRawUrl() throws IOException {
        // @checkstyle LineLength (1 line)
        final String url = "https://api.jcabi-github.invalid/octocat/Hello-World/raw/6dcb09b5b57875f334f61aebed695e2e4193db51/foo/bar.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(
                "raw_url",
                url
            ).rawUrl(),
            Matchers.equalTo(url)
        );
    }

    /**
     * FileChange.Smart can get the URL of the file's blob.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsBlobUrl() throws IOException {
        // @checkstyle LineLength (1 line)
        final String url = "https://api.jcabi-github.invalid/octocat/Hello-World/blob/6dcb09b5b57875f334f61aebed695e2e4193db51/foo/bar.txt";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange(
                "blob_url",
                url
            ).blobUrl(),
            Matchers.equalTo(url)
        );
    }

    /**
     * FileChange.Smart can get the contents URL of the file.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void getsContentsUrl() throws IOException {
        // @checkstyle LineLength (1 line)
        final String url = "https://api.jcabi-github.invalid/repos/octocat/Hello-World/contents/foo/bar.txt?ref=6dcb09b5b57875f334f61aebed695e2e4193db51";
        MatcherAssert.assertThat(
            "Values are not equal",
            FileChangeTest.stringFileChange("contents_url", url)
                .contentsUrl(),
            Matchers.equalTo(url)
        );
    }

    /**
     * Make a new smart file change backed by a JSON object consisting of a
     * single key-value pair, where the value is a string.
     * @param key Key string
     * @param value Value string
     * @return FileChange.Smart
     */
    private static FileChange.Smart stringFileChange(
        final String key,
        final String value
    ) {
        return new FileChange.Smart(
            new MkFileChange(
                Json.createObjectBuilder()
                    .add(key, value)
                    .build()
            )
        );
    }

    /**
     * Make a new smart file change backed by a JSON object consisting of a
     * single key-value pair, where the value is an integer.
     * @param key Key string
     * @param value Value integer
     * @return FileChange.Smart
     */
    private static FileChange.Smart intFileChange(
        final String key,
        final int value
    ) {
        return new FileChange.Smart(
            new MkFileChange(
                Json.createObjectBuilder()
                    .add(key, value)
                    .build()
            )
        );
    }
}
