/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comment;
import com.jcabi.github.Comments;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub comments.
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "repo", "ticket" })
final class MkComments implements Comments {

    /**
     * XPath suffix for comment.
     */
    private static final String COMMENT_PATH = "/comment";

    /**
     * XPath for comment number.
     */
    private static final String COMMENT_NUM_XPATH = "//comment/number";

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
     * Issue number.
     */
    private final transient int ticket;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param issue Issue number
     * @throws IOException If there is any I/O problem
     */
    MkComments(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int issue
    ) throws IOException {
        this(MkComments.bootstrap(stg, rep, issue), login, issue, rep);
    }

    private MkComments(
        final MkStorage stg,
        final String login,
        final int issue,
        final Coordinates rep
    ) {
        this.storage = stg;
        this.self = login;
        this.repo = rep;
        this.ticket = issue;
    }

    @Override
    public Issue issue() {
        return new MkIssue(this.storage, this.self, this.repo, this.ticket);
    }

    @Override
    public Comment get(final long number) {
        return new MkComment(
            this.storage, this.self, this.repo, this.ticket, number
        );
    }

    @Override
    public Iterable<Comment> iterate(final Instant since) {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat(MkComments.COMMENT_PATH),
            xml -> this.get(
                Long.parseLong(xml.xpath("number/text()").get(0))
            )
        );
    }

    @Override
    public Comment post(final String text) throws IOException {
        this.storage.lock();
        final long number;
        try {
            final String timestamp = new GitHub.Time().toString();
            number = 1L + this.storage.xml()
                .nodes(MkComments.COMMENT_NUM_XPATH).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("comment")
                    .add("number").set(Long.toString(number)).up()
                    .add("url").set(
                        String.format(
                            "https://api.jcabi-github.invalid/repos/%s/%s/issues/comments/%d",
                            this.repo.user(),
                            this.repo.repo(),
                            number
                    )
                )
                .up()
                .add("body").set(text).up()
                .add("user")
                    .add("login").set(this.self).up()
                .up()
                .add("created_at").set(timestamp).up()
                .add("updated_at").set(timestamp)
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "comment #%d posted to issue #%d by %s: %[text]s",
            number, this.issue().number(), this.self, text
        );
        return this.get(number);
    }

    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/comments",
            this.repo, this.ticket
        );
    }

    private static MkStorage bootstrap(final MkStorage stg, final Coordinates rep, final int issue)
        throws IOException {
        stg.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/issues/issue[number='%d']",
                    rep, issue
                )
            ).addIf("comments")
        );
        return stg;
    }
}
