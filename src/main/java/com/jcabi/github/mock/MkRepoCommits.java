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
import com.jcabi.github.CommitsComparison;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RepoCommit;
import com.jcabi.github.RepoCommits;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @todo #117 MkRepoCommits should be able to fetch commits. Let's
 *  implement this method. When done, remove this puzzle and
 *  Ignore annotation from a test for the method.
 * @todo #273 MkRepoCommits should be able to compare two commits. Let's
 *  create a test for this method and implement the method. When done, remove
 *  this puzzle.
 * @todo #439 MkRepoCommits should be able to compare two commits and return
 *  comparison in diff format.
 *  Let's create a test for this method and implement the method.
 *  When done, remove this puzzle.
 *  See http://developer.github.com/v3/repos/commits/#compare-two-commits
 * @todo #439 MkRepoCommits should be able to compare two commits and return
 *  comparison in patch format.
 *  Let's create a test for this method and implement the method.
 *  When done, remove this puzzle.
 *  See http://developer.github.com/v3/repos/commits/#compare-two-commits
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkRepoCommits implements RepoCommits {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param repo Repository coordinates
     */
    MkRepoCommits(final MkStorage stg, final String login,
        final Coordinates repo) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
    }

    @Override
    public Iterable<RepoCommit> iterate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RepoCommit get(final String sha) {
        return new MkRepoCommit(
            new MkRepo(this.storage, this.self, this.coords), sha
        );
    }

    @Override
    public CommitsComparison compare(final String base, final String head) {
        return new MkCommitsComparison();
    }

    @Override
    public String diff(final String base, final String head)
        throws IOException {
        throw new UnsupportedOperationException("MkRepoCommits#diff()");
    }

    @Override
    public String patch(final String base, final String head)
        throws IOException {
        throw new UnsupportedOperationException("MkRepoCommits#patch()");
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    /**
     * Xpath of this element in XML tree.
     * @return Xpath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/commits",
            this.coords
        );
    }
}
