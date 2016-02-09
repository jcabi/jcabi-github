/**
 * Copyright (c) 2013-2015, jcabi.com
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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * Mock commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
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
     * @throws IOException If something goes wrong.
     */
    MkRepoCommits(
        final MkStorage stg,
        final String login,
        final Coordinates repo
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
    public Iterable<RepoCommit> iterate(
        final Map<String, String> params
    ) {
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
    public RepoCommit get(
        final String sha
    ) {
        return new MkRepoCommit(
            this.storage, new MkRepo(this.storage, this.self, this.coords), sha
        );
    }

    @Override
    public CommitsComparison compare(
        final String base,
        final String head
    ) {
        return new MkCommitsComparison(this.storage, this.self, this.coords);
    }

    @Override
    public String diff(
        final String base,
        final String head
    ) {
        return
        String.format(
            "%s%sindex %s..%s",
            "diff --git a/README b/README",
            System.getProperty("line.separator"), base, head
        );
    }

    @Override
    public String patch(
        final String base,
        final String head
    ) {
        return StringUtils.join(
            String.format("From %s Mon Sep 17 00:00:00 2001\n", head),
            "From: Some Author <some_author@email.com>\n",
            "Date: Tue, 11 Feb 2014 20:33:49 +0200\n",
            "Subject: Some subject\n", "\n", "---\n",
            " .../java/com/jcabi/github/CommitsComparison.java   |   6 +-\n",
            " src/main/java/com/jcabi/github/RepoCommit.java     | 131 +++++",
            "++++++++++++++++\n",
            " src/main/java/com/jcabi/github/RepoCommits.java    |  15 +--\n",
            " src/main/java/com/jcabi/github/RtRepoCommit.java   | 110 +++++",
            "++++++++++++\n",
            " src/main/java/com/jcabi/github/RtRepoCommits.java  |   6 +-\n",
            " .../java/com/jcabi/github/mock/MkRepoCommits.java  |   6 +-\n",
            " src/test/java/com/jcabi/github/RepoCommitTest.java |  84 +++++",
            "++++++++\n",
            " .../java/com/jcabi/github/RtRepoCommitsITCase.java |   7 +-\n",
            " 8 files changed, 346 insertions(+), 19 deletions(-)\n",
            " create mode 100644 src/main/java/com/jcabi/github/",
            "RepoCommit.java\n",
            " create mode 100644 src/main/java/com/jcabi/github/RtRepoCommit",
            ".java\n",
            " create mode 100644 src/test/java/com/jcabi/github/",
            "RepoCommitTest.java"
        );
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
