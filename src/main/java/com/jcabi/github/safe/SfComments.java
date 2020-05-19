/**
 * Copyright (c) 2013-2020, jcabi.com
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
package com.jcabi.github.safe;

import com.google.common.base.Function;
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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
    public Comment get(final int number) {
        return new SfComment(this.origin.get(number));
    }

    @Override
    public Iterable<Comment> iterate(final Date since) {
        return Iterables.transform(
            this.origin.iterate(since),
            new Function<Comment, Comment>() {
                @Override
                public Comment apply(final Comment input) {
                    return new SfComment(input);
                }
            }
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
