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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtRepoCommits implements RepoCommits {

    /**
     * RESTful request for thTe commits.
     */
    private final transient Request request;

    /**
     * RESTful request, an entry point to the Github API.
     */
    private final transient Request entry;

    /**
     * Github.
     */
    private final transient Github github;

    /**
     * Repository.
     */
    private final transient Repo repo;

    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository coordinates
     */
    RtRepoCommits(final Request req, final Coordinates repo) {
        this.entry = req;
        this.request = req.uri()
            .path("/repos")
            .path(repo.user())
            .path(repo.repo())
            .path("/commits")
            .back();
        this.github = new RtGithub(this.request);
        this.repo = new RtRepo(this.github, this.request, repo);
    }

    @Override
    public Iterable<Commit> iterate() {
        return new RtPagination<Commit>(
            this.request,
            new RtPagination.Mapping<Commit>() {
                @Override
                public Commit map(final JsonObject object) {
                    return get(object.getString("sha"));
                }
            }
        );
    }

    @Override
    public Commit get(final String sha) {
        return new RtCommit(this.entry, this.repo, sha);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
