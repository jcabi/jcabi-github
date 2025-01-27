/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkFileChange;
import java.io.IOException;
import javax.json.Json;
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
            FileChangeTest.stringFileChange(status, "added").status(),
            Matchers.equalTo(FileChange.Status.ADDED)
        );
        MatcherAssert.assertThat(
            FileChangeTest.stringFileChange(status, "modified").status(),
            Matchers.equalTo(FileChange.Status.MODIFIED)
        );
        MatcherAssert.assertThat(
            FileChangeTest.stringFileChange(status, "removed").status(),
            Matchers.equalTo(FileChange.Status.REMOVED)
        );
        MatcherAssert.assertThat(
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
            stringFileChange(
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
            stringFileChange(
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
            stringFileChange(
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
