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
import javax.validation.constraints.NotNull;
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
final class MkContents implements Contents {

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
    public MkContents(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep
    ) throws IOException {
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
    @NotNull(message = "Repo is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "the content is never NULL")
    public Content readme() throws IOException {
        // @checkstyle MultipleStringLiterals (2 lines)
        return new MkContent(
            this.storage, this.self, this.coords, "README.md", "master"
        );
    }

    @Override
    @NotNull(message = "the content is never NULL")
    public Content readme(final String branch) throws IOException {
        return new MkContent(
            this.storage, this.self, this.coords, "README.md", branch
        );
    }

    @Override
    @NotNull(message = "created content is never NULL")
    public Content create(
        @NotNull(message = "json can't be NULL") final JsonObject json
    ) throws IOException {
        this.storage.lock();
        // @checkstyle MultipleStringLiterals (20 lines)
        final String branch;
        try {
            if (json.containsKey("ref")) {
                branch = json.getString("ref");
            } else {
                branch = "master";
            }
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("content")
                    .attr("ref", branch)
                    .add("name").set(json.getString("path")).up()
                    .add("path").set(json.getString("path")).up()
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
        return new MkContent(
            this.storage, this.self, this.coords, json.getString("path"), branch
        );
    }

    @Override
    @NotNull(message = "retrieved content is never NULL")
    public Content get(
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "ref can't be NULL") final String ref
    ) throws IOException {
        return new MkContent(this.storage, this.self, this.coords, path, ref);
    }

    @Override
    @NotNull(message = "commit is never NULL")
    public RepoCommit remove(
        @NotNull(message = "content should not be NULL")
        final JsonObject content
    ) throws IOException {
        this.storage.lock();
        final String path = content.getString("path");
        try {
            this.storage.apply(
                new Directives()
                    .xpath(this.xpath())
                    .xpath(String.format("content[path='%s']", path))
                    .remove()
            );
            return this.commit(content);
        } finally {
            this.storage.unlock();
        }
    }

    /**
     * Updates a file.
     * @param path The content path.
     * @param json JSON object containing updates to the content.
     * @return Commit related to this update.
     * @throws IOException If any I/O problem occurs.
     */
    @Override
    @NotNull(message = "updated commit is never NULL")
    public RepoCommit update(
        @NotNull(message = "path cannot be NULL") final String path,
        @NotNull(message = "json should not be NULL") final JsonObject json
    ) throws IOException {
        this.storage.lock();
        try {
            final String xpath = String.format(
                "/github/repos/repo[@coords='%s']/contents/content[path='%s']",
                this.coords, path
            );
            new JsonPatch(this.storage).patch(xpath, json);
            return this.commit(json);
        } finally {
            this.storage.unlock();
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return The XPath
     */
    @NotNull(message = "Xpath is never NULL")
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
    @NotNull(message = "commit xpath is never NULL")
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
    @NotNull(message = "MkRepoCommit is never NULL")
    private MkRepoCommit commit(
        @NotNull(message = "json can't be NULL") final JsonObject json
    ) throws IOException {
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
        return new MkRepoCommit(this.storage, this.repo(), sha);
    }

    /**
     * Generate a random fake SHA hex string.
     *
     * @return Fake SHA string.
     */
    @NotNull(message = "fake sha can't be NULL")
    private static String fakeSha() {
        // @checkstyle MagicNumberCheck (1 line)
        return RandomStringUtils.random(40, "0123456789abcdef");
    }
}
