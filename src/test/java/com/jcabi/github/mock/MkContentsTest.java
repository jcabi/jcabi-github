/**
 * Copyright (c) 2013-2014, JCabi.com
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
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #524 MkContents should be able to handle branches.
 *  In a request for file update or create you may specify a branch.
 *  Also, a branch might be specified in a reading request.
 *  So, if you changed some file in branch-1, you shouldn't get these
 *  changes in the master branch, only in branch-1.
 *  Implementation of create, update and get methods of MkContents
 *  should be changed.
 *  See
 *  http://developer.github.com/v3/repos/contents/#update-a-file for details
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MkContentsTest {
    /**
     * MkContents can fetch the default branch readme file.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchReadmeFile() throws Exception {
        final Contents contents = MkContentsTest.repo().contents();
        MatcherAssert.assertThat(
            contents.readme(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #323 MkContents should be able to fetch the readme of the specified
     *  branch. This method should create a new instance of MkContent. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
    @Test
    @Ignore
    public void canFetchReadmeFromBranch() throws Exception {
        //To be implemented.
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateFile() throws Exception {
        final Contents contents = MkContentsTest.repo().contents();
        final String path = "file.txt";
        final JsonObject json = MkContentsTest
            .content(path, "theCreateMessage", "newContent")
            .add("committer", MkContentsTest.committer())
            .build();
        final Content.Smart content = new Content.Smart(contents.create(json));
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
    @Ignore
    public void canCreateFileInSomeBranch() throws Exception {
        //To be implemented.
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #311 MkContents should support the removal of mock contents.
     *  This method should return a new instance of MkCommit. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
    @Test
    @Ignore
    public void canRemoveFile() throws Exception {
        //To be implemented.
    }

    /**
     * MkContents should be able to update a file.
     * @throws Exception - if anything goes wrong.
     * @todo #444 Methods create() in MkContents and json() in MkContent
     *  should be implemented in order for this test to work.
     */
    @Test
    @Ignore
    public void updatesFile() throws Exception {
        final String username = "jeff";
        final String path = "file.txt";
        final String message = "content message";
        final String initial = "abcdef";
        final String update = "test update content";
        final String cont = "content";
        final Contents contents = MkContentsTest.repo().contents();
        final ConcurrentMap<String, String> commiter =
            new ConcurrentHashMap<String, String>();
        commiter.put("login", username);
        final ConcurrentMap<String, String> author =
            new ConcurrentHashMap<String, String>();
        author.put("login", username);
        final JsonObject content = MkContentsTest
            .content(path, "theMessage", "blah")
            .build();
        MatcherAssert.assertThat(
            content.getString(cont),
            Matchers.is(initial)
        );
        final JsonObject jsonPatch = MkContentsTest
            .content(path, message, update)
            .build();
        contents.update(path, jsonPatch);
        MatcherAssert.assertThat(
            content.getString(cont),
            Matchers.is(update)
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
        contents.update(path, update);
        final Content.Smart content =
            new Content.Smart(contents.get(path, "master"));
        MatcherAssert.assertThat(
            content.path(),
            Matchers.is(path)
        );
        MatcherAssert.assertThat(
            storage.xml().nodes(xpath),
            Matchers.<XML>iterableWithSize(2)
        );
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
