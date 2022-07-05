/**
 * Copyright (c) 2013-2022, jcabi.com
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
import java.io.IOException;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github repository.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
public interface Repo extends JsonReadable, JsonPatchable, Comparable<Repo> {

    /**
     * Get its owner.
     * @return Github
     */
    Github github();

    /**
     * Get its coordinates.
     * @return Coordinates
     */
    Coordinates coordinates();

    /**
     * Iterate issues.
     * @return Issues
     */
    Issues issues();

    /**
     * Iterate milestones.
     * @return Milestones
     * @since 0.7
     */
    Milestones milestones();

    /**
     * Pull requests.
     * @return Pulls
     */
    Pulls pulls();

    /**
     * Hooks.
     * @return Hooks
     * @since 0.8
     */
    Hooks hooks();

    /**
     * Get all issue events for the repository.
     * @return Issue events
     * @see <a href="http://developer.github.com/v3/issues/events/#list-events-for-a-repository">List Events for a Repository</a>
     */
    IssueEvents issueEvents();

    /**
     * Get all labels of the repo.
     * @return Labels
     * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    Labels labels();

    /**
     * Get all available assignees to which issues may be assigned.
     * @return Assignees
     * @see <a href="http://developer.github.com/v3/issues/assignees/">Assignees API</a>
     */
    Assignees assignees();

    /**
     * Get all releases of the repo.
     * @return Releases
     * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
     */
    Releases releases();

    /**
     * Get all deploy keys of the repo.
     * @return DeployKeys
     * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
     */
    DeployKeys keys();

    /**
     * Get all forks of the repo.
     * @return Forks
     * @see <a href="http://developer.github.com/v3/repos/forks/">Forks API</a>
     */
    Forks forks();

    /**
     * Get repository's commits.
     * @return Commits
     * @see <a href="http://developer.github.com/v3/repos/commits/">Commits API</a>
     */
    RepoCommits commits();

    /**
     * Get repository's branches.
     * @return Branches
     * @see <a href="https://developer.github.com/v3/repos/#list-branches">List Branches API</a>
     */
    Branches branches();

    /**
     * Get all contents of the repo.
     * @return Contents
     * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
     */
    Contents contents();

    /**
     * Gel all collaborators.
     * @return Collaborators
     * @see <a href="http://developer.github.com/v3/repos/collaborators/">Collaborators API</a>
     */
    Collaborators collaborators();

    /**
     * Get the Git API entry point.
     * @return Collaborators
     * @see <a href="http://developer.github.com/v3/git/">Git Data API</a>
     */
    Git git();

    /**
     * Get Starring API.
     * @return Stars
     * @see <a href="https://developer.github.com/v3/activity/starring/">Starring API</a>
     * @since 0.15
     */
    Stars stars();

    /**
     * Get Notifications API.
     * @return Stars
     * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
     * @since 0.15
     */
    Notifications notifications();

    /**
     * Get languages for the specified repository.
     * @return Languages
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/#list-languages">List languages</a>
     * @since 0.15
     */
    Iterable<Language> languages() throws IOException;

    /**
     * Smart Repo with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "repo", "jsn" })
    final class Smart implements Repo {
        /**
         * Encapsulated Repo.
         */
        private final transient Repo repo;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param rep Repo
         */
        public Smart(
            final Repo rep
        ) {
            this.repo = rep;
            this.jsn = new SmartJson(rep);
        }

        /**
         * Does this Repo actually exist in Github?
         * @return True if it exists, false otherwise.
         * @throws IOException If there is any I/O problem.
         */
        public boolean exists() throws IOException {
            return new Existence(this.repo).check();
        }

        /**
         * Does it have a description.
         * @return TRUE if description is present
         * @throws IOException If there is any I/O problem
         */
        public boolean hasDescription() throws IOException {
            return this.jsn.hasNotNull("description");
        }
        /**
         * Get its description.
         * @return Description
         * @throws IOException If there is any I/O problem
         */
        public String description() throws IOException {
            return this.jsn.text("description");
        }
        /**
         * Is it private?.
         * @return TRUE if it's private
         * @throws IOException If there is any I/O problem
         */
        public boolean isPrivate() throws IOException {
            return Boolean.parseBoolean(
                this.json()
                    .getOrDefault("private", JsonValue.FALSE)
                    .toString().replace("\"", "")
            );
        }
        @Override
        public Github github() {
            return this.repo.github();
        }
        @Override
        public Coordinates coordinates() {
            return this.repo.coordinates();
        }
        @Override
        public Issues issues() {
            return this.repo.issues();
        }
        @Override
        public Milestones milestones() {
            return this.repo.milestones();
        }
        @Override
        public Pulls pulls() {
            return this.repo.pulls();
        }

        @Override
        public Hooks hooks() {
            return this.repo.hooks();
        }
        @Override
        public IssueEvents issueEvents() {
            return this.repo.issueEvents();
        }
        @Override
        public Labels labels() {
            return this.repo.labels();
        }
        @Override
        public Assignees assignees() {
            return this.repo.assignees();
        }
        @Override
        public Releases releases() {
            return this.repo.releases();
        }
        @Override
        public DeployKeys keys() {
            return this.repo.keys();
        }
        @Override
        public Forks forks() {
            return this.repo.forks();
        }
        @Override
        public Contents contents() {
            return this.repo.contents();
        }
        @Override
        public Collaborators collaborators() {
            return this.repo.collaborators();
        }
        @Override
        public Git git() {
            return this.repo.git();
        }
        @Override
        public Stars stars() {
            return this.repo.stars();
        }
        @Override
        public Notifications notifications() {
            return this.repo.notifications();
        }
        @Override
        public Iterable<Language> languages() throws IOException {
            return this.repo.languages();
        }
        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.repo.patch(json);
        }
        @Override
        public RepoCommits commits() {
            return this.repo.commits();
        }
        @Override
        public Branches branches() {
            return this.repo.branches();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.repo.json();
        }

        @Override
        public int compareTo(final Repo repos) {
            return this.repo.compareTo(repos);
        }
    }

}
