/**
 * Copyright (c) 2013-2017, jcabi.com
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
import javax.json.JsonObject;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface Contents {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get the Readme file of the default branch (usually master).
     *
     * @return The Content of the readme file.
     * @throws IOException If an IO Exception occurs.
     * @see <a href="http://http://developer.github.com/v3/repos/contents/#get-the-readme">Get the README</a>
     */
    Content readme() throws IOException;

    /**
     * Get the Readme file of the specified branch.
     *
     * @param branch The branch name
     * @return The Content of the readme file.
     * @throws IOException If an IO Exception occurs.
     * @see <a href="http://http://developer.github.com/v3/repos/contents/#get-the-readme">Get the README</a>
     */
    Content readme(String branch) throws IOException;

    /**
     * Create new file.
     * @param content Parameters to create new content
     * @return Content just created
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#create-a-file">Create a file</a>
     */
    Content create(JsonObject content) throws IOException;

    /**
     * Get the contents of a single file or symbolic link in a repository.
     * @param path The content path
     * @param ref The name of the commit/branch/tag.
     * @return Content fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Content get(String path, String ref) throws IOException;

    /**
     * Get the contents of a single file or symbolic link.
     * in a repository's default branch (usually master).
     * @param path The content path
     * @return Content fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Content get(String path) throws IOException;

    /**
     * Get the contents of a directory in a repository.
     * @param path The content path
     * @param ref The name of the commit/branch/tag. Default: the repository's default branch (usually master)
     * @return Contents fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Iterable<Content> iterate(String path, String ref) throws IOException;

    /**
     * Removes a file.
     * @param content Parameters to remove a file
     * @return RepoCommit referring to this operation
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#delete-a-file">Delete a file</a>
     */
    RepoCommit remove(JsonObject content) throws IOException;

    /**
     * Updates a file.
     * @param path The content path.
     * @param json JSON object containing updates to the content.
     * @return Commit referring to this operation
     * @throws IOException If any I/O problems occur.
     * @see <a href="http://developer.github.com/v3/repos/contents/#update-a-file">Update a file</a>
     */
    RepoCommit update(String path, JsonObject json) throws IOException;

    /**
     * Check whether content exists or not.
     * @param path The content path
     * @param ref The name of the commit/branch/tag.
     * @return True if content exists, false otherwise.
     * @throws IOException If there is any I/O problem
     */
    boolean exists(String path, String ref) throws IOException;

}
