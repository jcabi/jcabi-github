/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.safe;

import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comment;
import com.jcabi.github.Comments;
import com.jcabi.github.Issue;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Date;
import lombok.EqualsAndHashCode;

/**
 * Safe comments.
 *
 * @since 0.34
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "origin")
public final class SfComments implements Comments {

    /**
     * Original comments.
     */
    private final transient Comments origin;

    /**
     * Public ctor.
     * @param cmt The original comment
     */
    public SfComments(final Comments cmt) {
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
    public Comment get(final long number) {
        return new SfComment(this.origin.get(number));
    }

    @Override
    public Iterable<Comment> iterate(final Date since) {
        return Iterables.transform(
            this.origin.iterate(since),
            input -> new SfComment(input)
        );
    }

    @Override
    public Comment post(final String text) throws IOException {
        Comment cmt;
        try {
            cmt = this.origin.post(text);
        } catch (final AssertionError ex) {
            Logger.warn(this, "Failed to post to GitHub: %[exception]s", ex);
            cmt = new MkGithub().randomRepo()
                .issues().create("", "")
                .comments().post(text);
        }
        return cmt;
    }
}
