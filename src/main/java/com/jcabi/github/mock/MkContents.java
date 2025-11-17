/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import com.jcabi.xml.XML;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.xembly.Directives;

/**
 * Mock Github contents.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
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
        final MkStorage stg,
        final String login,
        final Coordinates rep
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
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Content readme() throws IOException {
        // @checkstyle MultipleStringLiterals (1 line)
        return this.readme("master");
    }

    @Override
    public Content readme(
        final String branch
    ) {
        return new MkContent(
            this.storage, this.self, this.coords, "README.md", branch
        );
    }

    @Override
    public Content create(
        final JsonObject json
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
                    .add("sha").set(MkContents.fakeSha()).up()
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
    public Content get(
        final String path,
        final String ref
    ) {
        return new MkContent(this.storage, this.self, this.coords, path, ref);
    }

    @Override
    public Content get(
        final String path
    ) {
        return new MkContent(
            this.storage, this.self, this.coords, path, "master"
        );
    }

    @Override
    public Iterable<Content> iterate(
        final String pattern,
        final String ref
    ) throws IOException {
        final Collection<XML> nodes = this.storage.xml().nodes(
            String.format("%s/content[@ref='%s']", this.xpath(), ref)
        );
        final Collection<Content> result = new ArrayList<>(nodes.size());
        for (final XML node : nodes) {
            final String path = node.xpath("path/text()").get(0);
            if (path.startsWith(pattern)) {
                result.add(
                    this.mkContent(ref, path)
                );
            }
        }
        return result;
    }

    @Override
    public RepoCommit remove(
        final JsonObject content
    ) throws IOException {
        this.storage.lock();
        final String path = content.getString("path");
        // @checkstyle MultipleStringLiterals (20 lines)
        final String branch;
        try {
            if (content.containsKey("ref")) {
                branch = content.getString("ref");
            } else {
                branch = "master";
            }
            this.storage.apply(
                new Directives()
                    .xpath(this.xpath())
                    .xpath(String.format("content[path='%s']", path))
                    .attr("ref", branch)
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
    public RepoCommit update(
        final String path,
        final JsonObject json
    ) throws IOException {
        this.storage.lock();
        try {
            final String ref = "ref";
            final String branch;
            if (json.containsKey(ref)) {
                branch = json.getString(ref);
            } else {
                branch = "master";
            }
            final String xpath = String.format(
                // @checkstyle LineLengthCheck (1 line)
                "/github/repos/repo[@coords='%s']/contents/content[path='%s' and @ref='%s']",
                this.coords, path, branch
            );
            new JsonPatch(this.storage).patch(xpath, json);
            return this.commit(json);
        } finally {
            this.storage.unlock();
        }
    }

    @Override
    public boolean exists(final String path, final String ref)
        throws IOException {
        return this.storage.xml().nodes(
            String.format("%s/content[path='%s']", this.xpath(), path)
        ).size() > 0;
    }

    /**
     * Builder method for MkContent.
     * @param ref Branch name.
     * @param path Path to MkContent.
     * @return MkContent instance.
     */
    private MkContent mkContent(final String ref, final String path) {
        return new MkContent(this.storage, this.self, this.coords, path, ref);
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
    private MkRepoCommit commit(
        final JsonObject json
    ) throws IOException {
        final String sha = MkContents.fakeSha();
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
    private static String fakeSha() {
        // @checkstyle MagicNumberCheck (1 line)
        return RandomStringUtils.random(40, "0123456789abcdef");
    }
}
