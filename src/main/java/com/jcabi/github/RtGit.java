/**
 * Copyright (c) 2013-2014, JCabi.com
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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Github Git.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "owner" })
public final class RtGit implements Git {

    /**
     * Repository.
     */
    private final transient Repo owner;
    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    public RtGit(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Blobs blobs() throws IOException {
        return new RtBlobs(this.entry, this.repo());
    }

    @Override
    public Commits commits() {
        throw new UnsupportedOperationException("Commits not yet implemented");
    }

    @Override
    public References references() {
        throw new UnsupportedOperationException(
            "References not yet implemented"
        );
    }

    @Override
    public Tags tags() {
        throw new UnsupportedOperationException("Tags not yet implemented.");
    }

    @Override
    public Trees trees() {
        throw new UnsupportedOperationException("Trees not yet implemented");
    }

}
