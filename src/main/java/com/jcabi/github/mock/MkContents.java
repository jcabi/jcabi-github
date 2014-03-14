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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.xembly.Directives;

/**
 * Mock Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkContents implements Contents {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    public MkContents(final MkStorage stg, final String login,
        final Coordinates rep) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", this.coords)
            ).addIf("contents").up().addIf("commits")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Content readme() throws IOException {
        return new MkContent(this.storage, this.self, this.coords, "README.md");
    }

    @Override
    public Content readme(final String branch)
        throws IOException {
        return new MkContent(this.storage, this.self, this.coords, branch);
    }

    @Override
    public Content create(final JsonObject json)
        throws IOException {
        this.storage.lock();
        // @checkstyle MultipleStringLiterals (40 lines)
        final String path = json.getString("path");
        try {
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("content")
                    .add("name").set(path).up()
                    .add("path").set(path).up()
                    .add("content").set(json.getString("content")).up()
                    .add("type").set("file").up()
                    .add("encoding").set("base64").up()
                    .add("sha").set(fakeSha()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("git_url").set("http://localhost/2").up()
                    .add("html_url").set("http://localhost/3").up()
            );
            this.commit(json);
        } finally {
            this.storage.unlock();
        }
        return new MkContent(this.storage, this.self, this.coords, path);
    }

    @Override
    public Content get(final String path, final String ref)
        throws IOException {
        return new MkContent(this.storage, this.self, this.coords, path);
    }

    @Override
    public RepoCommit remove(final JsonObject content)
        throws IOException {
        throw new UnsupportedOperationException("Remove not yet implemented.");
    }

    /**
     * Updates a file.
     * @param path The content path.
     * @param json JSON object containing updates to the content.
     * @return Commit related to this update.
     * @throws IOException If any I/O problem occurs.
     */
    @Override
    public RepoCommit update(final String path, final JsonObject json)
        throws IOException {
        try {
            new JsonPatch(this.storage).patch(path, json);
            return this.commit(json);
        } finally {
            this.storage.unlock();
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return The XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/contents",
            this.coords
        );
    }

    /**
     * Xpath of the commits element in XML tree.
     * @return Xpath
     */
    private String commitXpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/commits",
            this.coords
        );
    }

    /**
     * XML Directives for commit creation.
     * @param json Source
     * @return SHA string
     * @throws IOException If an IO Exception occurs
     */
    private MkRepoCommit commit(final JsonObject json) throws IOException {
        final String sha = fakeSha();
        // @checkstyle MultipleStringLiterals (40 lines)
        final Directives commit = new Directives().xpath(this.commitXpath())
            .add("commit")
            .add("sha").set(sha).up()
            .add("url").set("http://localhost/4").up()
            .add("html_url").set("http://localhost/5").up()
            .add("message").set(json.getString("message")).up();
        if (json.containsKey("committer")) {
            final JsonObject committer = json.getJsonObject("committer");
            commit.add("committer")
                .add("email").set(committer.getString("email")).up()
                .add("name").set(committer.getString("name")).up();
        }
        if (json.containsKey("author")) {
            final JsonObject author = json.getJsonObject("author");
            commit.add("author")
                .add("email").set(author.getString("email")).up()
                .add("name").set(author.getString("name")).up();
        }
        this.storage.apply(commit);
        return new MkRepoCommit(this.repo(), sha);
    }

    /**
     * Generate a random fake SHA hex string.
     *
     * @return Fake SHA string.
     */
    private static String fakeSha() {
        // @checkstyle MagicNumberCheck (1 line)
        return RandomStringUtils.random(40, "0123456789abcdef");
    }
}
