/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoComment;
import com.jcabi.github.RepoComments;
import com.jcabi.log.Logger;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub commit comments of a repository.
 *
 * @since 2.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkRepoComments implements RepoComments {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     *
     * @param stg Storage
     * @param login User to login
     * @param repo Repository coordinates
     * @throws IOException If there is any I/O problem
     */
    MkRepoComments(
        final MkStorage stg,
        final String login,
        final Coordinates repo
    ) throws IOException {
        this(MkRepoComments.bootstrap(stg, repo), repo, login);
    }

    private MkRepoComments(final MkStorage stg, final Coordinates repo, final String login) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public RepoComment get(final long number) {
        return new MkRepoComment(this.storage, this.self, this.coords, number);
    }

    @Override
    public Iterable<RepoComment> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/comment"),
            xml -> this.get(Long.parseLong(xml.xpath("id/text()").get(0)))
        );
    }

    @Override
    public Iterable<RepoComment> iterate(final String sha) {
        return new MkIterable<>(
            this.storage,
            String.format("%s/comment[commit_id='%s']", this.xpath(), sha),
            xml -> this.get(Long.parseLong(xml.xpath("id/text()").get(0)))
        );
    }

    @Override
    public RepoComment post(final String sha, final String body)
        throws IOException {
        return this.post(sha, body, new Directives());
    }

    @Override
    public RepoComment post(
        final String sha,
        final String body,
        final String path,
        final int position
    ) throws IOException {
        return this.post(
            sha,
            body,
            new Directives()
                .add("path").set(path).up()
                .add("position").attr("type", "number")
                .set(Integer.toString(position)).up()
        );
    }

    private RepoComment post(
        final String sha,
        final String body,
        final Directives extra
    ) throws IOException {
        this.storage.lock();
        final long number;
        try {
            final String timestamp = new GitHub.Time().toString();
            number = 1L + this.storage.xml()
                .xpath(this.xpath().concat("/last/text()"))
                .stream()
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);
            this.storage.apply(
                new Directives().xpath(this.xpath())
                    .addIf("last").set(Long.toString(number)).up()
                    .add("comment")
                    .add("id").attr("type", "number")
                    .set(Long.toString(number)).up()
                    .add("url").set(
                        String.format(
                            "https://api.jcabi-github.invalid/repos/%s/%s/comments/%d",
                            this.coords.user(),
                            this.coords.repo(),
                            number
                        )
                    ).up()
                    .add("commit_id").set(sha).up()
                    .add("body").set(body).up()
                    .add("user").add("login").set(this.self).up().up()
                    .add("created_at").set(timestamp).up()
                    .add("updated_at").set(timestamp).up()
                    .append(extra)
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "comment #%d posted to commit %s by %s",
            number, sha, this.self
        );
        return this.get(number);
    }

    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/comments",
            this.coords
        );
    }

    private static MkStorage bootstrap(final MkStorage stg, final Coordinates repo)
        throws IOException {
        stg.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", repo)
            ).addIf("comments")
        );
        return stg;
    }
}
