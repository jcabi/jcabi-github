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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
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
     * The storage.
     */
    private final transient MkStorage storage;
    /**
     * The repository.
     */
    private final transient Repo repository;

    /**
     * Public ctor.
     * @param stg The storage
     * @param repo The repository
     * @param sha Commit SHA
     */
    MkRepoCommit(
        @NotNull(message = "storage must not be NULL")
        final MkStorage stg,
        @NotNull(message = "repo must not be null")
        final Repo repo,
        @NotNull(message = "sha must not be NULL")
        final String sha) {
        this.storage = stg;
        this.repository = repo;
        this.hash = sha;
    }

    @Override
    public int compareTo(
        @NotNull(message = "other can't be NULL") final RepoCommit other
    ) {
        throw new UnsupportedOperationException("MkRepoCommit#compareTo()");
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(
                String.format(
                    "/github/repos/repo[@coords='%s']/commits/commit[sha='%s']",
                        this.repo().coordinates(), this.hash
                )
            ).get(0)
        ).json();
    }

    @Override
    @NotNull(message = "repository is never NULL")
    public Repo repo() {
        return this.repository;
    }

    @Override
    @NotNull(message = "sha should no be NULL")
    public String sha() {
        return this.hash;
    }

}
