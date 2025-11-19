/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * GitHub contents.
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/contents/">Contents API</a>
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
     * @return The Content of the readme file.
     * @throws IOException If an IO Exception occurs.
     * @see <a href="http://https://developer.github.com/v3/repos/contents/#get-the-readme">Get the README</a>
     */
    Content readme() throws IOException;

    /**
     * Get the Readme file of the specified branch.
     * @param branch The branch name
     * @return The Content of the readme file.
     * @throws IOException If an IO Exception occurs.
     * @see <a href="http://https://developer.github.com/v3/repos/contents/#get-the-readme">Get the README</a>
     */
    Content readme(String branch) throws IOException;

    /**
     * Create new file.
     * @param content Parameters to create new content
     * @return Content just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#create-a-file">Create a file</a>
     */
    Content create(JsonObject content) throws IOException;

    /**
     * Get the contents of a single file or symbolic link in a repository.
     * @param path The content path
     * @param ref The name of the commit/branch/tag.
     * @return Content fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Content get(String path, String ref) throws IOException;

    /**
     * Get the contents of a single file or symbolic link.
     * in a repository's default branch (usually master).
     * @param path The content path
     * @return Content fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Content get(String path) throws IOException;

    /**
     * Get the contents of a directory in a repository.
     * @param path The content path
     * @param ref The name of the commit/branch/tag. Default: the repository's default branch (usually master)
     * @return Contents fetched
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#get-contents">Get contents</a>
     */
    Iterable<Content> iterate(String path, String ref) throws IOException;

    /**
     * Removes a file.
     * @param content Parameters to remove a file
     * @return RepoCommit referring to this operation
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/contents/#delete-a-file">Delete a file</a>
     */
    RepoCommit remove(JsonObject content) throws IOException;

    /**
     * Updates a file.
     * @param path The content path.
     * @param json JSON object containing updates to the content.
     * @return Commit referring to this operation
     * @throws IOException If any I/O problems occur.
     * @see <a href="https://developer.github.com/v3/repos/contents/#update-a-file">Update a file</a>
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
