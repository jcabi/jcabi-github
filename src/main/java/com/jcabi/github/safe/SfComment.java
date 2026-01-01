/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.safe;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comment;
import com.jcabi.github.Issue;
import com.jcabi.github.Reaction;
import com.jcabi.github.mock.MkGitHub;
import com.jcabi.log.Logger;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Safe comment.
 *
 * @since 0.34
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "origin")
public final class SfComment implements Comment {

    /**
     * Original comment.
     */
    private final transient Comment origin;

    /**
     * Public ctor.
     * @param cmt The original comment
     */
    public SfComment(final Comment cmt) {
        this.origin = cmt;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public Issue issue() {
        return this.origin.issue();
    }

    @Override
    public long number() {
        return this.origin.number();
    }

    @Override
    public void remove() throws IOException {
        try {
            this.origin.remove();
        } catch (final AssertionError ex) {
            Logger.warn(this, "Failed to remove comment: %[exception]s", ex);
        }
    }

    @Override
    public void react(final Reaction reaction) throws IOException {
        this.origin.react(reaction);
    }

    @Override
    public Iterable<Reaction> reactions() {
        return this.origin.reactions();
    }

    @Override
    public int compareTo(final Comment cmt) {
        return this.origin.compareTo(cmt);
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        try {
            this.origin.patch(json);
        } catch (final AssertionError ex) {
            Logger.warn(this, "Failed to path comment: %[exception]s", ex);
        }
    }

    @Override
    public JsonObject json() throws IOException {
        JsonObject json;
        try {
            json = this.origin.json();
        } catch (final AssertionError ex) {
            final String author = new Issue.Smart(
                new SfIssue(this.origin.issue())
            ).author().login();
            json = new MkGitHub(author).randomRepo()
                .issues().create("", "")
                .comments().post("deleted comment").json();
            Logger.warn(this, "failed to fetch comment: %[exception]s", ex);
        }
        return json;
    }
}
