/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github pull comments.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/pulls/comments/">Review Comments API</a>
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "storage", "self", "repo", "owner" })
final class MkPullComments implements PullComments {
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
    private final transient Coordinates repo;

    /**
     * Owner of comments.
     */
    private final transient Pull owner;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param pull Pull
     * @throws IOException If there is any I/O problem
     * @checkstyle ParameterNumber (5 lines)
     */
    MkPullComments(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final Pull pull
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.repo = rep;
        this.owner = pull;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']",
                    this.repo, this.owner.number()
                )
            ).addIf("comments")
        );
    }
    @Override
    public Pull pull() {
        return this.owner;
    }

    @Override
    public PullComment get(final int number) {
        return new MkPullComment(this.storage, this.repo, this.owner, number);
    }

    @Override
    public Iterable<PullComment> iterate(
        final Map<String, String> params
    ) {
        return new MkIterable<>(
            this.storage,
            String.format(
                "/github/repos/repo[@coords='%s']/pulls/pull/comments",
                this.repo
            ),
            xml -> this.get(
                Integer.parseInt(xml.xpath("comment/id/text()").get(0))
            )
        );
    }

    @Override
    public Iterable<PullComment> iterate(
        final int number,
        final Map<String, String> params
    ) {
        return new MkIterable<>(
            this.storage, String.format("%s/comment", this.xpath()),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    // @checkstyle ParameterNumberCheck (7 lines)
    @Override
    public PullComment post(
        final String body,
        final String commit,
        final String path,
        final int position
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml()
                .nodes(String.format("%s/comment/id/text()", this.xpath()))
                .size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("comment")
                    .add("id").set(Integer.toString(number)).up()
                    .add("url").set("http://localhost/1").up()
                    .add("diff_hunk").set("@@ -16,33 +16,40 @@ public...").up()
                    // @checkstyle MultipleStringLiteralsCheck (4 lines)
                    .add("path").set(path).up()
                    .add("position").set(Integer.toString(position)).up()
                    .add("original_position").set(Integer.toString(number)).up()
                    .add("commit_id").set(commit).up()
                    .add("original_commit_id").set(commit).up()
                    .add("body").set(body).up()
                    .add("created_at").set(new Github.Time().toString()).up()
                    .add("published_at").set(new Github.Time().toString()).up()
                    .add("user").add("login").set(this.self).up()
                    .add("pull_request_url").set("http://localhost/2").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public PullComment reply(
        final String body,
        final int comment
    )
        throws IOException {
        this.storage.lock();
        try {
            final JsonObject orig = this.get(comment).json();
            final PullComment reply = this.post(
                body,
                orig.getString("commit_id"),
                orig.getString("path"),
                comment
            );
            reply.patch(
                Json.createObjectBuilder()
                    .add("original_position", String.valueOf(comment)).build()
            );
            return reply;
        } finally {
            this.storage.unlock();
        }
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/comment[id='%d']", this.xpath(), number)
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/comments",
            this.repo, this.owner.number()
        );
    }
}
