/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.Reaction;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github pull comment.
 *
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "storage", "repo", "owner", "num" })
final class MkPullComment implements PullComment {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Repo name.
     */
    private final transient Coordinates repo;

    /**
     * Owner of comments.
     */
    private final transient Pull owner;

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param rep Repo
     * @param pull Pull
     * @param number Comment number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkPullComment(
        final MkStorage stg,
        final Coordinates rep,
        final Pull pull,
        final int number
    ) {
        this.storage = stg;
        this.repo = rep;
        this.owner = pull;
        this.num = number;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public Pull pull() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public void react(final Reaction reaction) {
        throw new UnsupportedOperationException("React not implemented");
    }

    @Override
    public Collection<Reaction> reactions() {
        throw new UnsupportedOperationException(
            "reactions() not implemented"
        );
    }

    @Override
    public int compareTo(
        final PullComment other
    ) {
        return this.number() - other.number();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/comments/comment[id='%d']",
            this.repo, this.owner.number(), this.num
        );
    }
}
