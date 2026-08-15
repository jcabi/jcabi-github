/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkContents}.
 * @since 0.8
 */
final class MkContentsTest {

    /**
     * Name of the default branch.
     */
    private static final String MASTER = "master";

    /**
     * Path of the file under test.
     */
    private static final String PATH = "file.txt";

    /**
     * Message of the commit that removes a file.
     */
    private static final String DELETE = "theDeleteMessage";

    /**
     * XPath of the commits in the storage.
     */
    private static final String XPATH = "/github/repos/repo/commits/commit";

    /**
     * Name of the JSON attribute with the content of a file.
     */
    private static final String CONTENT = "content";

    @Test
    void canFetchReadmeFile() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String body = "Readme On Master";
        contents.create(
            MkContentsTest.content("README.md", "readme on master", body).build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            contents.readme().json().getString(MkContentsTest.CONTENT),
            Matchers.is(body)
        );
    }

    @Test
    void canFetchReadmeFromBranch() throws IOException {
        final String branch = "branch-1";
        final Contents contents = new MkGitHub().randomRepo().contents();
        final String body = "Readme On Branch";
        contents.create(
            MkContentsTest.content("README.md", "readme on branch", body)
                .add("ref", branch)
                .build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            contents.readme(branch).json().getString(MkContentsTest.CONTENT),
            Matchers.is(body)
        );
    }

    /**
     * MkContents should give a path to a new file.
     * @throws Exception if some problem inside
     */
    @Test
    void createsFileWithPath() throws Exception {
        MatcherAssert.assertThat(
            "Created file has a wrong path",
            MkContentsTest.created().path(),
            Matchers.is(MkContentsTest.PATH)
        );
    }

    /**
     * MkContents should give a name to a new file.
     * @throws Exception if some problem inside
     */
    @Test
    void createsFileWithName() throws Exception {
        MatcherAssert.assertThat(
            "Created file has a wrong name",
            MkContentsTest.created().name(),
            Matchers.is(MkContentsTest.PATH)
        );
    }

    /**
     * MkContents should give a SHA to a new file.
     * @throws Exception if some problem inside
     */
    @Test
    void createsFileWithSha() throws Exception {
        MatcherAssert.assertThat(
            "Created file has no SHA",
            MkContentsTest.created().sha(),
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
    }

    @Test
    void createsFileInBranchWithPath() throws IOException {
        MatcherAssert.assertThat(
            "File created in a branch has a wrong path",
            MkContentsTest.branched().path(),
            Matchers.is(MkContentsTest.PATH)
        );
    }

    @Test
    void createsFileInBranchWithName() throws IOException {
        MatcherAssert.assertThat(
            "File created in a branch has a wrong name",
            MkContentsTest.branched().name(),
            Matchers.is(MkContentsTest.PATH)
        );
    }

    @Test
    void createsFileInBranchWithSha() throws IOException {
        MatcherAssert.assertThat(
            "File created in a branch has no SHA",
            MkContentsTest.branched().sha(),
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
    }

    @Test
    void createsFileInBranchWithContent() throws IOException {
        MatcherAssert.assertThat(
            "File created in a branch has a wrong content",
            MkContentsTest.branched().content(),
            Matchers.is("some file")
        );
    }

    /**
     * MkContents should be able to remove files.
     * @throws Exception if some problem inside
     */
    @Test
    void removesFile() throws Exception {
        MatcherAssert.assertThat(
            "File is not removed",
            MkContentsTest.removed(Json.createObjectBuilder()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkContents should commit the removal of a file with a message.
     * @throws Exception if some problem inside
     */
    @Test
    void removesFileWithMessage() throws Exception {
        MatcherAssert.assertThat(
            "Removal is committed with a wrong message",
            MkContentsTest.removed(Json.createObjectBuilder())
                .json().getString("message"),
            Matchers.equalTo(MkContentsTest.DELETE)
        );
    }

    /**
     * MkContents should be able to remove files from non-default branches.
     * @throws Exception if some problem inside
     */
    @Test
    void removesFileFromBranch() throws Exception {
        MatcherAssert.assertThat(
            "File is not removed from the branch",
            MkContentsTest.removed(
                Json.createObjectBuilder().add("ref", "branch-1")
            ),
            Matchers.notNullValue()
        );
    }

    /**
     * MkContents should commit the removal from a branch with a message.
     * @throws Exception if some problem inside
     */
    @Test
    void removesFileFromBranchWithMessage() throws Exception {
        MatcherAssert.assertThat(
            "Removal from the branch is committed with a wrong message",
            MkContentsTest.removed(
                Json.createObjectBuilder().add("ref", "branch-1")
            ).json().getString("message"),
            Matchers.equalTo(MkContentsTest.DELETE)
        );
    }

    @Test
    void createsFileWithContent() throws IOException {
        final String initial = "initial text";
        MatcherAssert.assertThat(
            "Created file has a wrong content",
            new MkGitHub().randomRepo().contents().create(
                MkContentsTest.content(
                    MkContentsTest.PATH, "content message", initial
                ).build()
            ).json().getString(MkContentsTest.CONTENT),
            Matchers.is(initial)
        );
    }

    @Test
    void updatesFile() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        contents.create(
            MkContentsTest.content(
                MkContentsTest.PATH, "content message", "initial text"
            ).build()
        );
        final String updated = "updated text";
        contents.update(
            MkContentsTest.PATH,
            MkContentsTest.content(
                MkContentsTest.PATH, "content message", updated
            ).build()
        );
        MatcherAssert.assertThat(
            "Updated file has a wrong content",
            contents.get(MkContentsTest.PATH, MkContentsTest.MASTER)
                .json().getString(MkContentsTest.CONTENT),
            Matchers.is(updated)
        );
    }

    @Test
    void createsCommitWhileCreatingFile() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MkContentsTest.stored(storage);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            storage.xml().nodes(MkContentsTest.XPATH),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void createsCommitWhileUpdatingFile() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MkContentsTest.update(MkContentsTest.stored(storage));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            storage.xml().nodes(MkContentsTest.XPATH),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void updatesFileWithSha() throws IOException {
        MatcherAssert.assertThat(
            "Commit of the update has no SHA",
            new RepoCommit.Smart(
                MkContentsTest.update(
                    MkContentsTest.stored(new MkStorage.InFile())
                )
            ).sha(),
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
    }

    @Test
    void updatesFileKeepingItsPath() throws IOException {
        final Contents contents =
            MkContentsTest.stored(new MkStorage.InFile());
        MkContentsTest.update(contents);
        MatcherAssert.assertThat(
            "Updated file has a wrong path",
            new Content.Smart(
                contents.get(MkContentsTest.PATH, MkContentsTest.MASTER)
            ).path(),
            Matchers.is(MkContentsTest.PATH)
        );
    }

    @Test
    void createsContentInBranch() throws IOException {
        final String initial = "Hello World!";
        MatcherAssert.assertThat(
            "Created content is wrong",
            new Content.Smart(
                new MkGitHub().randomRepo().contents().create(
                    MkContentsTest.content(
                        MkContentsTest.PATH, "commit message", initial
                    ).add("ref", MkContentsTest.MASTER).build()
                )
            ).content(),
            Matchers.is(initial)
        );
    }

    @Test
    void updatesContentInBranch() throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        contents.create(
            MkContentsTest.content(
                MkContentsTest.PATH, "commit message", "Hello World!"
            ).add("ref", MkContentsTest.MASTER).build()
        );
        final String updated = "update content";
        contents.update(
            MkContentsTest.PATH,
            MkContentsTest.content(
                MkContentsTest.PATH, "commit message", updated
            ).add("ref", MkContentsTest.MASTER).build()
        );
        MatcherAssert.assertThat(
            "Updated content is wrong",
            new Content.Smart(
                contents.get(MkContentsTest.PATH, MkContentsTest.MASTER)
            ).content(),
            Matchers.is(updated)
        );
    }

    @Test
    void checksExistingContent() throws IOException {
        final String branch = "rel.08";
        MatcherAssert.assertThat(
            "Existing content is not found",
            MkContentsTest.existing(branch)
                .exists(MkContentsTest.PATH, branch),
            Matchers.is(true)
        );
    }

    @Test
    void checksAbsentContent() throws IOException {
        final String branch = "rel.08";
        MatcherAssert.assertThat(
            "Absent content is found",
            MkContentsTest.existing(branch)
                .exists("content-not-exist.txt", branch),
            Matchers.is(false)
        );
    }

    @Test
    void createsContentInDefaultBranch() throws IOException {
        final String text = "I'm content of default branch";
        MatcherAssert.assertThat(
            "Content of the default branch is wrong",
            new Content.Smart(
                new MkGitHub().randomRepo().contents().create(
                    MkContentsTest.content(
                        MkContentsTest.PATH, "content created", text
                    ).build()
                )
            ).content(),
            Matchers.is(text)
        );
    }

    @Test
    void fetchesContentFromDefaultBranch() throws IOException {
        final String text = "I'm content of default branch";
        final Contents contents = new MkGitHub().randomRepo().contents();
        contents.create(
            MkContentsTest.content(
                MkContentsTest.PATH, "content created", text
            ).build()
        );
        MatcherAssert.assertThat(
            "Content of the default branch is not fetched",
            new Content.Smart(contents.get(MkContentsTest.PATH)).content(),
            Matchers.is(text)
        );
    }

    /**
     * Tests if MkContents is iterable by path.
     * @throws IOException if any error occurs.
     */
    @Test
    void canIterate() throws IOException {
        final Repo repo = MkContentsTest.repo(new MkStorage.InFile());
        final Content[] correct = MkContentsTest.addContent(
            repo, "foo/bar/1", "foo/bar/2"
        );
        MkContentsTest.addContent(repo, "foo/baz", "foo/boo");
        MatcherAssert.assertThat(
            "Assertion failed",
            repo.contents().iterate("foo/bar", "ref-1"),
            Matchers.contains(correct)
        );
    }

    /**
     * Adds collection of test content items.
     * @param repo The repo
     * @param paths Test items to be created inside the repo
     * @return Iterable with created items
     * @throws IOException If any I/O error occurs.
     */
    private static Content[] addContent(final Repo repo,
        final String... paths) throws IOException {
        final Content[] result = new Content[paths.length];
        int index = 0;
        for (final String path : paths) {
            result[index] = repo.contents().create(
                Json.createObjectBuilder().add("ref", "ref-1")
                    .add("path", path).add(MkContentsTest.CONTENT, path)
                    .add("message", "msg").build()
            );
            index += 1;
        }
        return result;
    }

    /**
     * A file created in a random repo.
     * @return Created content
     * @throws IOException If any I/O error occurs.
     */
    private static Content.Smart created() throws IOException {
        return new Content.Smart(
            MkContentsTest.createFile(new MkGitHub().randomRepo())
        );
    }

    /**
     * A file created in a branch of a random repo.
     * @return Created content
     * @throws IOException If any I/O error occurs.
     */
    private static Content.Smart branched() throws IOException {
        return new Content.Smart(
            new MkGitHub().randomRepo().contents().create(
                MkContentsTest.content(
                    MkContentsTest.PATH, "some file", "some file"
                ).add("ref", "branch-2").build()
            )
        );
    }

    /**
     * A commit that removes a file from a random repo.
     * @param extra Extra attributes of the removal request
     * @return Commit of the removal
     * @throws IOException If any I/O error occurs.
     */
    private static RepoCommit removed(final JsonObjectBuilder extra)
        throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MkContentsTest.createFile(repo);
        return repo.contents().remove(
            extra.add("path", MkContentsTest.PATH)
                .add("message", MkContentsTest.DELETE)
                .add("committer", MkContentsTest.committer())
                .build()
        );
    }

    /**
     * Contents with one file in the given branch.
     * @param branch Name of the branch
     * @return Contents
     * @throws IOException If any I/O error occurs.
     */
    private static Contents existing(final String branch) throws IOException {
        final Contents contents = new MkGitHub().randomRepo().contents();
        contents.create(
            MkContentsTest.content(
                MkContentsTest.PATH, "commit", "content exists"
            ).add("ref", branch).build()
        );
        return contents;
    }

    /**
     * Contents with one file in them, kept in the given storage.
     * @param storage The storage
     * @return Contents
     * @throws IOException If any I/O error occurs.
     */
    private static Contents stored(final MkStorage storage) throws IOException {
        final Contents contents = MkContentsTest.repo(storage).contents();
        contents.create(
            MkContentsTest
                .content(MkContentsTest.PATH, "theCreateMessage", "newContent")
                .add("committer", MkContentsTest.committer())
                .build()
        );
        return contents;
    }

    /**
     * Updates the file under test.
     * @param contents Contents to update the file in
     * @return Commit of the update
     * @throws IOException If any I/O error occurs.
     */
    private static RepoCommit update(final Contents contents)
        throws IOException {
        return contents.update(
            MkContentsTest.PATH,
            MkContentsTest.content(
                MkContentsTest.PATH, "theMessage", "blah"
            ).build()
        );
    }

    /**
     * Creates a new file.
     * @param repo The repository
     * @return Created content
     * @throws IOException If any I/O error occurs.
     */
    private static Content createFile(final Repo repo) throws IOException {
        return repo.contents().create(
            MkContentsTest
                .content(MkContentsTest.PATH, "theCreateMessage", "newContent")
                .add("committer", MkContentsTest.committer())
                .build()
        );
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
            .add(MkContentsTest.CONTENT, content);
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
    private static Repo repo(final MkStorage storage) throws IOException {
        return new MkGitHub(storage, "test").randomRepo();
    }
}
