/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Reaction;
import java.io.IOException;
import java.util.Collection;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github comment.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "repo", "ticket", "num" })
final class MkComment implements Comment {

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
     * Comment number.
     */
    private final transient long num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param issue Issue number
     * @param number Comment number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkComment(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int issue,
        final long number
    ) {
        this.storage = stg;
        this.self = login;
        this.repo = rep;
        this.ticket = issue;
        this.num = number;
    }

    @Override
    public Issue issue() {
        return new MkIssue(this.storage, this.self, this.repo, this.ticket);
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).strict(1).remove()
        );
    }

    @Override
    public int compareTo(
        final Comment comment
    ) {
        return Long.compare(this.number(), comment.number());
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void react(final Reaction reaction) {
        throw new UnsupportedOperationException("react() not implemented");
    }

    @Override
    public Collection<Reaction> reactions() {
        throw new UnsupportedOperationException("reactions() not implemented");
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/comments/comment[number='%d']",
            this.repo, this.ticket, this.num
        );
    }

}
