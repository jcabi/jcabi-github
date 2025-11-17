/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import com.jcabi.xml.XML;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkContents}.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" })
public final class MkContentsTest {
    /**
     * MkContents can fetch the default branch readme file.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchReadmeFile() throws Exception {
        final Contents contents = new MkGithub().randomRepo().contents();
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
        final Contents contents = new MkGithub().randomRepo().contents();
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
            this.createFile(new MkGithub().randomRepo(), path)
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
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
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
            new MkGithub().randomRepo().contents().create(
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
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
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
        final Repo repo = new MkGithub().randomRepo();
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
        final Repo repo = new MkGithub().randomRepo();
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
        final Contents contents = new MkGithub().randomRepo().contents();
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
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
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
        final Contents contents = new MkGithub().randomRepo().contents();
        final JsonObject content = MkContentsTest
            .content(path, message, initial)
            .add("ref", branch)
            .build();
        MatcherAssert.assertThat(
            new Content.Smart(contents.create(content)).content(),
            Matchers.is(initial)
        );
        contents.update(
            path, MkContentsTest.content(path, message, updated)
                .add("ref", branch).build()
        );
        MatcherAssert.assertThat(
            new Content.Smart(contents.get(path, branch)).content(),
            Matchers.is(updated)
        );
    }

    /**
     * MkContents can check whether content exists or not.
     * @throws Exception if any problem inside.
     */
    @Test
    public void checkExists() throws Exception {
        final String path = "content-exist.txt";
        final String branch = "rel.08";
        final Contents contents = new MkGithub().randomRepo().contents();
        contents.create(
            MkContentsTest.content(path, "commit", "content exists")
                .add("ref", branch)
                .build()
        );
        MatcherAssert.assertThat(
            contents.exists(path, branch),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            contents.exists("content-not-exist.txt", branch),
            Matchers.is(false)
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
        final Contents contents = new MkGithub().randomRepo().contents();
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
     */
    private static JsonObjectBuilder content(
        final String path, final String message) {
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
     */
    private static JsonObjectBuilder content(
        final String path, final String message, final String content) {
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
     * Create a test repo with custom {@code MkStorage}.
     * @param storage The storage
     * @return Test repo
     * @throws IOException If any I/O error occurs.
     */
    private static Repo repo(
        final MkStorage storage) throws IOException {
        final String login = "test";
        return new MkGithub(storage, login).randomRepo();
    }

}
