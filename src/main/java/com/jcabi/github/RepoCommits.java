/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * Commits of a Github repository.
 * @see <a href="https://developer.github.com/v3/repos/commits/">Commits API</a>
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface RepoCommits extends JsonReadable {

    /**
     * Iterate all repository's commits.
     * @return All commits
     * @param params Url's parameters
     * @see <a href="https://developer.github.com/v3/repos/commits/#list-commits-on-a-repository">List commits on a repository</a>
     */
    Iterable<RepoCommit> iterate(
        final Map<String, String> params
    );

    /**
     * Get single repository's commits.
     *
     * @param sha SHA of a commit
     * @return RepoCommit
     * @see <a href="https://developer.github.com/v3/repos/commits/#get-a-single-commit">Get a single commit</a>
     */
    RepoCommit get(String sha);

    /**
     * Compare two commits.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     */
    CommitsComparison compare(
        String base,
        String head);

    /**
     * Compare two commits and provide result in diff format.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     * @throws IOException If there is any I/O problem
     * @since 0.8
     */
    String diff(
        String base,
        String head
    ) throws IOException;

    /**
     * Compare two commits and provide result in patch format.
     * @param base SHA of the base repo commit
     * @param head SHA of the head repo commit
     * @return Commits comparison
     * @throws IOException If there is any I/O problem
     * @since 0.8
     */
    String patch(
        String base,
        String head
    ) throws IOException;
}
