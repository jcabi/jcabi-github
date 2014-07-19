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

import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #590 MkContents can now create and get files from non-default branches.
 *  However, the same functionality has not been implemented yet for the
 *  update() method. Let's fix it. See
 *  http://developer.github.com/v3/repos/contents for details
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" })
public final class MkContentsTest {
    /**
     * MkContents can fetch the default branch readme file.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchReadmeFile() throws Exception {
        final Contents contents = MkContentsTest.repo().contents();
        final String body = "Readme On Master";
        // @checkstyle MultipleStringLiterals (6 lines)
        contents.create(
            content("README.md", "readme on master", body).build()
        );
        MatcherAssert.assertThat(
            contents.readme().json().getString("content"),
            Matchers.is(body)
        );
    }

    /**
     * MkContents should be able to fetch readme from a branch.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchReadmeFromBranch() throws Exception {
        final String branch = "branch-1";
        final Contents contents = MkContentsTest.repo().contents();
        final String body = "Readme On Branch";
        contents.create(
            content("README.md", "readme on branch", body)
                .add("ref", branch)
                .build()
        );
        MatcherAssert.assertThat(
            contents.readme(branch).json().getString("content"),
            Matchers.is(body)
        );
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateFile() throws Exception {
        final String path = "file.txt";
        final Content.Smart content = new Content.Smart(
            this.createFile(MkContentsTest.repo(), path)
        );
        MatcherAssert.assertThat(
            content.path(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            content.name(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            content.sha(),
            Matchers.not(Matchers.isEmptyOrNullString())
        );
    }

    /**
     * MkContents can create new file in non default branch.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateFileInSomeBranch() throws Exception {
        final String path = "file-in-branch.txt";
        final String branch = "branch-2";
        final String body = "some file";
        final Content.Smart content = new Content.Smart(
            MkContentsTest.repo().contents().create(
                content(path, "some file", body)
                    .add("ref", branch)
                    .build()
            )
        );
        MatcherAssert.assertThat(
            content.path(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            content.name(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            content.sha(),
            Matchers.not(Matchers.isEmptyOrNullString())
        );
        MatcherAssert.assertThat(
            content.content(),
            Matchers.is(body)
        );
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canRemoveFile() throws Exception {
        final Repo repo = MkContentsTest.repo();
        final String path = "removeme.txt";
        this.createFile(repo, path);
        final JsonObject json = MkContentsTest
            .content(path, "theDeleteMessage")
            .add("committer", MkContentsTest.committer())
            .build();
        final RepoCommit commit = repo.contents().remove(json);
        MatcherAssert.assertThat(commit, Matchers.notNullValue());
        MatcherAssert.assertThat(
            commit.json().getString("message"),
            Matchers.equalTo("theDeleteMessage")
        );
    }

    /**
     * MkContents should be able to remove files from from non-default branches.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canRemoveFileFromBranch() throws Exception {
        final String branch = "branch-1";
        final Repo repo = MkContentsTest.repo();
        final String path = "removeme.txt";
        this.createFile(repo, path);
        final JsonObject json = MkContentsTest
            .content(path, "theDeleteMessage")
            .add("ref", branch)
            .add("committer", MkContentsTest.committer())
            .build();
        final RepoCommit commit = repo.contents().remove(json);
        MatcherAssert.assertThat(commit, Matchers.notNullValue());
        MatcherAssert.assertThat(
            commit.json().getString("message"),
            Matchers.equalTo("theDeleteMessage")
        );
    }

    /**
     * MkContents should be able to update a file.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void updatesFile() throws Exception {
        final String path = "file.txt";
        final String message = "content message";
        final String initial = "initial text";
        final String updated = "updated text";
        final String cont = "content";
        final Contents contents = MkContentsTest.repo().contents();
        MatcherAssert.assertThat(
            contents.create(
                MkContentsTest.content(path, message, initial).build()
            ).json().getString(cont),
            Matchers.is(initial)
        );
        contents.update(
            path, MkContentsTest.content(path, message, updated).build()
        );
        MatcherAssert.assertThat(
            contents.get(path, "master").json().getString(cont),
            Matchers.is(updated)
        );
    }

    /**
     * MkContents is able to update the file content.
     * During update new commit is created
     * @throws Exception Exception if some problem inside
     */
    @Test
    public void updatesFileCreateCommit() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final Contents contents = MkContentsTest.repo(storage).contents();
        final String path = "file.txt";
        final JsonObject json = MkContentsTest
            .content(path, "theCreateMessage", "newContent")
            .add("committer", MkContentsTest.committer())
            .build();
        contents.create(json);
        final String xpath = "/github/repos/repo/commits/commit";
        MatcherAssert.assertThat(
            storage.xml().nodes(xpath),
            Matchers.<XML>iterableWithSize(1)
        );
        final JsonObject update = MkContentsTest
            .content(path, "theMessage", "blah")
            .build();
        MatcherAssert.assertThat(
            new RepoCommit.Smart(contents.update(path, update)).sha(),
            Matchers.not(Matchers.isEmptyOrNullString())
        );
        MatcherAssert.assertThat(
            new Content.Smart(contents.get(path, "master")).path(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            storage.xml().nodes(xpath),
            Matchers.<XML>iterableWithSize(2)
        );
    }

    /**
     * MkContents can update an content.
     * @throws Exception if any problem inside
     */
    @Test
    public void updateContent() throws Exception {
        final String path = "content-to-update.txt";
        final String message = "commit message";
        final String initial = "Hello World!";
        final String updated = "update content";
        final String branch = "master";
        final Contents contents = MkContentsTest.repo().contents();
        final JsonObject content = MkContentsTest
            .content(path, message, initial)
            .add("ref", branch)
            .build();
        MatcherAssert.assertThat(
            new Content.Smart(contents.create(content)).content(),
            Matchers.is(initial)
        );
        final JsonObject jsonPatch = MkContentsTest
            .content(path, message, updated)
            .add("ref", branch)
            .build();
        contents.update(path, branch, jsonPatch);
        MatcherAssert.assertThat(
            new Content.Smart(contents.get(path, branch)).content(),
            Matchers.is(updated)
        );
    }

    /**
     * MkContents can get content from default branch.
     * @throws Exception if any problem inside
     */
    @Test
    public void getContentFromDefaultBranch() throws Exception {
        final String path = "content-default-branch.txt";
        final String message = "content default branch created";
        final String text = "I'm content of default branch";
        final Contents contents = MkContentsTest.repo().contents();
        final JsonObject content = MkContentsTest
            .content(path, message, text)
            .build();
        MatcherAssert.assertThat(
            new Content.Smart(contents.create(content)).content(),
            Matchers.is(text)
        );
        MatcherAssert.assertThat(
            new Content.Smart(contents.get(path)).content(),
            Matchers.is(text)
        );
    }

    /**
     * Tests if MkContents is iterable by path.
     * @throws IOException if any error occurs.
     */
    @Test
    public void canIterate() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = repo(storage);
        final Content[] correct = this.addContent(
            repo, "foo/bar/1", "foo/bar/2"
        );
        this.addContent(repo, "foo/baz", "foo/boo");
        MatcherAssert.assertThat(
            repo.contents().iterate("foo/bar", "ref-1"),
            Matchers.contains(correct)
        );
    }

    /**
     * Adds colection of test content items.
     * @param repo The repo.
     * @param paths Test items to be created inside the repo.
     * @return Iterable with created items.
     * @throws IOException If any I/O error occurs.
     */
    private Content[] addContent(final Repo repo,
        final String... paths) throws IOException {
        final Content[] result = new Content[paths.length];
        int index = 0;
        for (final String path : paths) {
            result[index] = repo.contents().create(
                Json.createObjectBuilder().add("ref", "ref-1")
                    .add("path", path).add("content", path)
                    .add("message", "msg").build()
            );
            index += 1;
        }
        return result;
    }

    /**
     * Creates a new file.
     * @param repo The repository
     * @param path Content path
     * @return Created content
     * @throws Exception if some problem inside
     */
    private Content createFile(
        final Repo repo, final String path) throws Exception {
        final Contents contents = repo.contents();
        final JsonObject json = MkContentsTest
            .content(path, "theCreateMessage", "newContent")
            .add("committer", MkContentsTest.committer())
            .build();
        return contents.create(json);
    }

    /**
     * Create content JsonObjectBuilder.
     * @param path Content path
     * @param message Commit message
     * @return JsonObjectBuilder
     * @throws Exception If some problem inside
     */
    private static JsonObjectBuilder content(
        final String path, final String message)
        throws Exception {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message);
    }

    /**
     * Create content JsonObjectBuilder.
     * @param path Content path
     * @param message Commit message
     * @param content Base64 encoded content
     * @return JsonObjectBuilder
     * @throws Exception If some problem inside
     */
    private static JsonObjectBuilder content(
        final String path, final String message, final String content)
        throws Exception {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add("content", content);
    }

    /**
     * Creates default committer.
     * @return JsonObjectBuilder
     */
    private static JsonObjectBuilder committer() {
        return Json.createObjectBuilder()
            .add("name", "joe")
            .add("email", "joe@contents.com");
    }

    /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }

    /**
     * Create a test repo with custom {@code MkStorage}.
     * @param storage The storage
     * @return Test repo
     * @throws IOException If any I/O error occurs.
     */
    private static Repo repo(
        final MkStorage storage) throws IOException {
        final String login = "test";
        return new MkGithub(storage, login).repos().create(
            Json.createObjectBuilder().add("name", login).build()
        );
    }

}
