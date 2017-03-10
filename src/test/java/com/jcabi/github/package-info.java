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

/**
 * Object Oriented Github API, tests.
 * <p>The individual integration tests require following <a href=
 * "https://developer.github.com/v3/oauth/#scopes">OAuth scopes</a>:</p>
 * <ul>
 *     <li>RtAssigneesITCase - read:org </li>
 *     <li>RtBlobsITCase - repo</li>
 *     <li>RtContentsITCase - repo</li>
 *     <li>RtDeployKeysITCase - admin:public_key</li>
 *     <li>RtForksITCase - repo</li>
 *     <li>RtGistCommentITCase - gist</li>
 *     <li>RtGistCommentsITCase - gist</li>
 *     <li>RtGistITCase - gist</li>
 *     <li>RtGistsITCase - gist</li>
 *     <li>RtGithubITCase - repo </li>
 *     <li>RtGitignoresITCase - ?</li>
 *     <li>RtHooksITCase - admin:repo_hook</li>
 *     <li>RtIssueITCase - repo</li>
 *     <li>RtIssueLabelsITCase - repo </li>
 *     <li>RtIssuesITCase - repo</li>
 *     <li>RtLabelsITCase - repo</li>
 *     <li>RtLimitsITCase - repo</li>
 *     <li>RtMarkdownITCase - no scope</li>
 *     <li>RtMilestonesITCase - repo</li>
 *     <li>RtOrganizationsITCase - read:org</li>
 *     <li>RtPublicKeyITCase - admin:public_key</li>
 *     <li>RtPublicKeysITCase - admin:public_key</li>
 *     <li>RtReferencesITCase - repo</li>
 *     <li>RtReleaseAssetITCase - repo</li>
 *     <li>RtReleaseAssetsITCase - repo</li>
 *     <li>RtReleaseITCase - repo</li>
 *     <li>RtReleasesITCase - repo</li>
 *     <li>RtRepoCommitsITCase - repo</li>
 *     <li>RtRepoITCase - repo</li>
 *     <li>RtReposITCase - repo, delete_repo</li>
 *     <li>RtSearchITCase - repo, user</li>
 *     <li>RtStarsITCase - user </li>
 *     <li>RtTagITCase - repo</li>
 *     <li>RtTagsITCase - repo</li>
 *     <li>RtTreesITCase - repo</li>
 *     <li>RtUserEmailsITCase - user:email</li>
 *     <li>RtUserITCase - user</li>
 * </ul>
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
package com.jcabi.github;
