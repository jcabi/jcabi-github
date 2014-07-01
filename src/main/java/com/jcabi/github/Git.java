/**
 * Copyright (c) 2013-2014, jcabi.com
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
 * Github Git.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
public interface Git {

    /**
     * Owner of it.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get its blobs.
     * @return Blobs
     * @see <a href="http://developer.github.com/v3/git/blobs/">Blobs API</a>
     * @throws IOException If some io problem occurs
     */
    @NotNull(message = "Blobs is never NULL")
    Blobs blobs() throws IOException;

    /**
     * Get its commits.
     * @return Commits
     * @see <a href="http://developer.github.com/v3/git/commits/">Commits API</a>
     * @todo #115:30min Implement the Git Data Commits API. We need to
     *  implement the following:
     *  1) Add the operations in http://developer.github.com/v3/git/commits/
     *  to the Commits interface. Fetch operations should return Commit
     *  instances.
     *  2) A class RtCommits, which performs RESTful request operations
     *  against the Commits API.
     *  3) A class MkCommits, which mocks the Commits interface.
     *  4) MkCommit, which mocks a single Commit object.
     *  Don't forget to implement unit tests and integration tests.
     */
    @NotNull(message = "Commits is never NULL")
    Commits commits();

    /**
     * Get its references.
     * @return References
     * @see <a href="http://developer.github.com/v3/git/references/">References API</a>
     */
    @NotNull(message = "References is never NULL")
    References references();

    /**
     * Get its tags.
     * @return Tags
     * @see <a href="http://developer.github.com/v3/git/tags/">Tags API</a>
     */
    @NotNull(message = "Tags is never NULL")
    Tags tags();

    /**
     * Get its trees.
     * @return Trees
     * @see <a href="http://developer.github.com/v3/git/trees/">Trees API</a>
     * @todo #440 Implement the Git Data Trees Mock classes:
     *  1) A class MkTrees, which mocks the Trees interface.
     *  2) MkTree, which mocks a single Tree object.
     *  Don't forget to implement unit tests
     */
    @NotNull(message = "Trees is never NULL")
    Trees trees();
}
