/**
 * Copyright (c) 2012-2013, JCabi.com
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
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github repository.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Repo extends JsonReadable, JsonPatchable {

    /**
     * Get its owner.
     * @return Github
     */
    @NotNull(message = "github is never NULL")
    Github github();

    /**
     * Get its coordinates.
     * @return Coordinates
     */
    @NotNull(message = "coordinates is never NULL")
    Coordinates coordinates();

    /**
     * Iterate issues.
     * @return Issues
     */
    @NotNull(message = "iterator of issues is never NULL")
    Issues issues();

    /**
     * Iterate milestones.
     * @return Milestones
     * @since 0.7
     */
    @NotNull(message = "iterator of milestones is never NULL")
    Milestones milestones();

    /**
     * Pull requests.
     * @return Pulls
     */
    @NotNull(message = "iterator of pull requests is never NULL")
    Pulls pulls();

    /**
     * Hooks.
     * @return Hooks
     * @since 0.8
     */
    @NotNull(message = "hooks are never NULL")
    Hooks hooks();

    /**
     * Get all events for the repository.
     * @return Events
     * @see <a href="http://developer.github.com/v3/issues/events/#list-events-for-a-repository">List Events for a Repository</a>
     */
    @NotNull(message = "iterable of events is never NULL")
    Iterable<Event> events();

    /**
     * Get all labels of the repo.
     * @return Labels
     * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    @NotNull(message = "labels are never NULL")
    Labels labels();

    /**
     * Get all available assignees to which issues may be assigned.
     * @return Assignees
     * @see <a href="http://developer.github.com/v3/issues/assignees/">Assignees API</a>
     */
    @NotNull(message = "assignees are never NULL")
    Assignees assignees();

    /**
     * Get all releases of the repo.
     * @return Releases
     * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
     */
    @NotNull(message = "releases are never NULL")
    Releases releases();

    /**
     * Get all deploy keys of the repo.
     * @return DeployKeys
     * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
     */
    @NotNull(message = "deploy keys are never NULL")
    DeployKeys keys();

    /**
     * Get all forks of the repo.
     * @return Forks
     * @see <a href="http://developer.github.com/v3/repos/forks/">Forks API</a>
     */
    @NotNull(message = "Forks are never NULL")
    Forks forks();

    /**
     * Get repository's commits.
     * @return Commits
     * @see <a href="http://developer.github.com/v3/repos/commits/">Commits API</a>
     */
    @NotNull(message = "RepoCommits are never NULL")
    RepoCommits commits();

    /**
     * Get all contents of the repo.
     * @return Contents
     * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
     */
    @NotNull(message = "Contents are never NULL")
    Contents contents();

    /**
     * Gel all collaborators.
     * @return Collaborators
     * @see <a href="http://developer.github.com/v3/repos/collaborators/">Collaborators API</a>
     */
    Collaborators collaborators();

    /**
     * Gel git object.
     * @return Git
     * @see <a href="http://developer.github.com/v3/git/">Git API</a>
     */
    Git git();

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
        public Smart(final Repo rep) {
            this.repo = rep;
            this.jsn = new SmartJson(rep);
        }
        /**
         * Get its description.
         * @return Description
         * @throws IOException If there is any I/O problem
         */
        public String description() throws IOException {
            return this.jsn.text("description");
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
        public Iterable<Event> events() {
            return this.repo.events();
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
        public void patch(final JsonObject json) throws IOException {
            this.repo.patch(json);
        }
        @Override
        public RepoCommits commits() {
            return this.repo.commits();
        }
        @Override
        public Git git() {
            return this.repo.git();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.repo.json();
        }
    }

}
