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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import javax.json.JsonObject;
import javax.json.JsonValue;
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
public interface Repo extends JsonReadable, JsonPatchable, Comparable<Repo> {

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
    @NotNull(message = "Collaborators is never NULL")
    Collaborators collaborators();

    /**
     * Get the Git API entry point.
     * @return Collaborators
     * @see <a href="http://developer.github.com/v3/git/">Git Data API</a>
     */
    @NotNull(message = "Git is never NULL")
    Git git();

    /**
     * Get Starring API.
     * @return Stars
     * @see <a href="https://developer.github.com/v3/activity/starring/">Starring API</a>
     * @since 0.15
     */
    @NotNull(message = "Stars is never NULL")
    Stars stars();

    /**
     * Get Notifications API.
     * @return Stars
     * @see <a href="https://developer.github.com/v3/activity/notifications/">Notifications API</a>
     * @since 0.15
     */
    @NotNull(message = "Notifications is never NULL")
    Notifications notifications();

    /**
     * Get languages for the specified repository.
     * @return Languages
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/#list-languages">List languages</a>
     * @since 0.15
     */
    @NotNull(message = "Notifications is never NULL")
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
            @NotNull(message = "rep can't be NULL") final Repo rep
        ) {
            this.repo = rep;
            this.jsn = new SmartJson(rep);
        }
        /**
         * Get its description.
         * @return Description
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "description is never NULL")
        public String description() throws IOException {
            return this.jsn.text("description");
        }
        /**
         * Is it private?.
         * @return TRUE if it's private
         * @throws IOException If there is any I/O problem
         */
        public boolean isPrivate() throws IOException {
            return JsonValue.TRUE.equals(this.json().get("private"));
        }
        @Override
        @NotNull(message = "github is never NULL")
        public Github github() {
            return this.repo.github();
        }
        @Override
        @NotNull(message = "coordinates is never NULL")
        public Coordinates coordinates() {
            return this.repo.coordinates();
        }
        @Override
        @NotNull(message = "issues is never NULL")
        public Issues issues() {
            return this.repo.issues();
        }
        @Override
        @NotNull(message = "milestones is never NULL")
        public Milestones milestones() {
            return this.repo.milestones();
        }
        @Override
        @NotNull(message = "pulls is never NULL")
        public Pulls pulls() {
            return this.repo.pulls();
        }

        @Override
        @NotNull(message = "hooks is never NULL")
        public Hooks hooks() {
            return this.repo.hooks();
        }

        @Override
        @NotNull(message = "Iterable of events is never NULL")
        public Iterable<Event> events() {
            return this.repo.events();
        }
        @Override
        @NotNull(message = "labels is never NULL")
        public Labels labels() {
            return this.repo.labels();
        }
        @Override
        @NotNull(message = "assignees is never NULL")
        public Assignees assignees() {
            return this.repo.assignees();
        }
        @Override
        @NotNull(message = "releases is never NULL")
        public Releases releases() {
            return this.repo.releases();
        }
        @Override
        @NotNull(message = "keys is never NULL")
        public DeployKeys keys() {
            return this.repo.keys();
        }
        @Override
        @NotNull(message = "forks is never NULL")
        public Forks forks() {
            return this.repo.forks();
        }
        @Override
        @NotNull(message = "contents is never NULL")
        public Contents contents() {
            return this.repo.contents();
        }
        @Override
        @NotNull(message = "collaborators is never NULL")
        public Collaborators collaborators() {
            return this.repo.collaborators();
        }
        @Override
        @NotNull(message = "git is never NULL")
        public Git git() {
            return this.repo.git();
        }
        @Override
        @NotNull(message = "stars is never NULL")
        public Stars stars() {
            return this.repo.stars();
        }
        @Override
        @NotNull(message = "notifications is never NULL")
        public Notifications notifications() {
            return this.repo.notifications();
        }
        @Override
        @NotNull(message = "languages is never NULL")
        public Iterable<Language> languages() throws IOException {
            return this.repo.languages();
        }
        @Override
        public void patch(
            @NotNull(message = "json can't be NULL") final JsonObject json
        ) throws IOException {
            this.repo.patch(json);
        }
        @Override
        @NotNull(message = "commits is never NULL")
        public RepoCommits commits() {
            return this.repo.commits();
        }
        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.repo.json();
        }

        @Override
        public int compareTo(final Repo repos) {
            return this.repo.compareTo(repos);
        }
    }

}
