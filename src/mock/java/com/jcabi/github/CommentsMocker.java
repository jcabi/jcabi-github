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
package com.jcabi.github;

import com.jcabi.log.Logger;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Mocker of {@link Comments}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class CommentsMocker implements Comments {

    /**
     * Issue.
     */
    private final transient Issue owner;

    /**
     * All comments.
     */
    private final transient ConcurrentMap<Integer, Comment> map =
        new ConcurrentSkipListMap<Integer, Comment>();

    /**
     * Public ctor.
     * @param issue Owner of it
     */
    public CommentsMocker(final Issue issue) {
        this.owner = issue;
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public Comment get(final int number) {
        return this.map.get(number);
    }

    @Override
    public Comment post(final String text) {
        final int number = this.map.size() + 1;
        final Comment comment = new CommentMocker(
            number, this.owner, this.owner.repo().github().self(), text
        );
        this.map.put(number, comment);
        Logger.info(
            this, "Github comment #%d posted to issue #%d: %s",
            number, this.owner.number(), text
        );
        return comment;
    }

    @Override
    public Iterator<Comment> iterator() {
        return this.map.values().iterator();
    }

}
