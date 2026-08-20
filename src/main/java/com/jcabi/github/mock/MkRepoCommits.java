/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.CommitsComparison;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RepoCommit;
import com.jcabi.github.RepoCommits;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock commits of a GitHub repository.
 * @since 0.3
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkRepoCommits implements RepoCommits {

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
     * @param stg Storage
     * @param login User to login
     * @param repo Repository coordinates
     * @throws IOException If something goes wrong.
     */
    MkRepoCommits(
        final MkStorage stg,
        final String login,
        final Coordinates repo
    ) throws IOException {
        this(MkRepoCommits.bootstrap(stg, repo), repo, login);
    }

    private MkRepoCommits(final MkStorage stg, final Coordinates repo, final String login) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
    }

    @Override
    public Iterable<RepoCommit> iterate(final Map<String, String> params) {
        return new MkIterable<>(
            this.storage, this.xpath().concat("/commit"),
            xml -> this.get(
                xml.xpath("sha/text()").get(0)
            )
        );
    }

    @Override
    public RepoCommit get(final String sha) {
        return new MkRepoCommit(
            this.storage, new MkRepo(this.storage, this.self, this.coords), sha
        );
    }

    @Override
    public CommitsComparison compare(final String base, final String head) {
        return new MkCommitsComparison(this.storage, this.self, this.coords);
    }

    @Override
    public String diff(final String base, final String head) {
        return
        String.format(
            "%s%sindex %s..%s",
            "diff --git a/README b/README",
            System.getProperty("line.separator"), base, head
        );
    }

    @Override
    public String patch(final String base, final String head) {
        return String.join(
            System.lineSeparator(),
            String.format("From %s Mon Sep 17 00:00:00 2001", head),
            "From: Some Author <some_author@email.com>",
            "Date: Tue, 11 Feb 2014 20:33:49 +0200",
            "Subject: Some subject", "", "---",
            " .../java/com/jcabi/github/CommitsComparison.java   |   6 +-",
            " src/main/java/com/jcabi/github/RepoCommit.java     | 131 +++++++++++++++++++++",
            " src/main/java/com/jcabi/github/RepoCommits.java    |  15 +--",
            " src/main/java/com/jcabi/github/RtRepoCommit.java   | 110 +++++++++++++++++",
            " src/main/java/com/jcabi/github/RtRepoCommits.java  |   6 +-",
            " .../java/com/jcabi/github/mock/MkRepoCommits.java  |   6 +-",
            " src/test/java/com/jcabi/github/RepoCommitTest.java |  84 +++++++++++++",
            " .../java/com/jcabi/github/RtRepoCommitsITCase.java |   7 +-",
            " 8 files changed, 346 insertions(+), 19 deletions(-)",
            " create mode 100644 src/main/java/com/jcabi/github/RepoCommit.java",
            " create mode 100644 src/main/java/com/jcabi/github/RtRepoCommit.java",
            " create mode 100644 src/test/java/com/jcabi/github/RepoCommitTest.java"
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/commits",
            this.coords
        );
    }

    private static MkStorage bootstrap(final MkStorage stg, final Coordinates repo)
        throws IOException {
        stg.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", repo)
            ).addIf("commits")
        );
        return stg;
    }
}
