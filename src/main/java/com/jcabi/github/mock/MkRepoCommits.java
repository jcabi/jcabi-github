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
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
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
     * The repo commits comparison the templates of diff format.
     *
     */
    private static final  String DIFF_FORMAT = "diff --git a/README b/README";

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
     * @throws IOException If something goes wrong.
     */
    MkRepoCommits(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL")  final String login,
        @NotNull(message = "repo can't be NULL")final Coordinates repo
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
        this.storage.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", this.coords)
            ).addIf("commits")
        );
    }

    @Override
    @NotNull(message = "Iterable of commits can't be NULL")
    public Iterable<RepoCommit> iterate(final Map<String, String> params) {
        return new MkIterable<RepoCommit>(
            this.storage, String.format("%s/commit", this.xpath()),
            new MkIterable.Mapping<RepoCommit>() {
                @Override
                public RepoCommit map(final XML xml) {
                    return MkRepoCommits.this.get(
                        xml.xpath("sha/text()").get(0)
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "repocommit can't be NULL")
    public RepoCommit get(
        @NotNull(message = "sha shouldn't be NULL") final String sha
    ) {
        return new MkRepoCommit(
            this.storage, new MkRepo(this.storage, this.self, this.coords), sha
        );
    }

    @Override
    @NotNull(message = "comparison is never NULL")
    public CommitsComparison compare(
        @NotNull(message = "base can't be NULL") final String base,
        @NotNull(message = "head can't be NULL") final String head
    ) {
        return new MkCommitsComparison(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "diff is never NULL")
    public String diff(
        @NotNull(message = "base should not be NULL") final String base,
        @NotNull(message = "head should not be NULL") final String head
    ) throws IOException {
        return
        String.format(
            "%s%sindex %s..%s",
            DIFF_FORMAT,
            System.lineSeparator(), base, head
        );
    }

    @Override
    @NotNull(message = "patch is never NULL")
    public String patch(
        @NotNull(message = "base shouldn't be NULL") final String base,
        @NotNull(message = "head shouldn't be NULL") final String head
    ) throws IOException {
        throw new UnsupportedOperationException("MkRepoCommits#patch()");
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    /**
     * Xpath of this element in XML tree.
     * @return Xpath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/commits",
            this.coords
        );
    }
}
