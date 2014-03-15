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
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/repos/commits/">Commits API</a>
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface RepoCommits extends JsonReadable {

    /**
     * Iterate all repository's commits.
     * @return All commits
     * @see <a href="http://developer.github.com/v3/repos/commits/#list-commits-on-a-repository">List commits on a repository</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<RepoCommit> iterate();

    /**
     * Get single repository's commits.
     *
     * @param sha SHA of a commit
     * @return RepoCommit
     * @see <a href="http://developer.github.com/v3/repos/commits/#get-a-single-commit">Get a single commit</a>
     */
    @NotNull(message = "RepoCommit is never NULL")
    RepoCommit get(@NotNull(message = "sha is never NULL") String sha);

    /**
     * Compare two commits.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     */
    @NotNull(message = "repo commits comparison is never NULL")
    CommitsComparison compare(
        @NotNull(message = "base is never NULL") String base,
        @NotNull(message = "base is never NULL") String head);

    /**
     * Compare two commits and provide result in diff format.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     * @throws IOException If there is any I/O problem
     * @since 0.8
     */
    @NotNull(message = "repo commits comparison is never NULL")
    String diff(
        @NotNull(message = "base is never NULL") String base,
        @NotNull(message = "head is never NULL") String head
    ) throws IOException;

    /**
     * Compare two commits and provide result in patch format.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     * @throws IOException If there is any I/O problem
     * @since 0.8
     */
    @NotNull(message = "repo commits comparison is never NULL")
    String patch(
        @NotNull(message = "base is never NULL") String base,
        @NotNull(message = "head is never NULL") String head
    ) throws IOException;
}
