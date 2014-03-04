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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github commit.
 * @author Carlos Crespo (carlos.a.crespo@gmail.com)
 * @version $Id$
 * @todo #166 Should implement the compareTo method in MkRepoCommit.
 *  Once implemented please remove this puzzle.
 * @todo #166 Should create test class for MkRepoCommit.
 *  Once created please remove this puzzle.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "repository", "hash" })
final class MkRepoCommit implements RepoCommit {

    /**
     * Commit SHA.
     */
    private final transient String hash;

    /**
     * The repository.
     */
    private final transient Repo repository;

    /**
     * Public ctor.
     * @param repo The repository
     * @param sha Commit SHA
     */
    MkRepoCommit(final Repo repo, final String sha) {
        this.repository = repo;
        this.hash = sha;
    }

    @Override
    public int compareTo(final RepoCommit other) {
        throw new UnsupportedOperationException("MkRepoCommit#compareTo()");
    }

    @Override
    public JsonObject json() throws IOException {
        return Json.createObjectBuilder().add("hash", this.hash).build();
    }

    @Override
    public Repo repo() {
        return this.repository;
    }

    @Override
    public String sha() {
        return this.hash;
    }

}
