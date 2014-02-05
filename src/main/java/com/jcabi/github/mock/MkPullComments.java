/**
 * Copyright (c) 2012-2013, JCabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import java.io.IOException;
import java.util.Map;

/**
 * Mock Github pull comments.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/pulls/comments/">Review Comments API</a>
 */
@Immutable
public final class MkPullComments implements PullComments {

    @Override
    public Pull pull() {
        throw new UnsupportedOperationException("Pull not yet implemented.");
    }

    @Override
    public PullComment get(final int number) {
        throw new UnsupportedOperationException("Get not yet implemented.");
    }

    @Override
    public Iterable<PullComment> iterate(final Map<String, String> params) {
        //@checkstyle MultipleStringLiteralsCheck (1 line)
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    @Override
    public Iterable<PullComment> iterate(final int number,
        final Map<String, String> params) {
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    // @checkstyle ParameterNumberCheck (3 lines)
    @Override
    public PullComment post(final String body, final String commit,
        final String path, final int position) throws IOException {
        throw new UnsupportedOperationException("Post not yet implemented.");
    }

    @Override
    public PullComment reply(final String body,
        final int comment) throws IOException {
        throw new UnsupportedOperationException("Reply not yet implemented.");
    }

    @Override
    public void remove(final int number) throws IOException {
        throw new UnsupportedOperationException("Remove not yet implemented.");
    }
}
