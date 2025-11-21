/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub repository.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@Immutable
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessivePublicCount", "PMD.CouplingBetweenObjects"})
public interface Repo extends JsonReadable, JsonPatchable, Comparable<Repo> {

    /**
     * Get its owner.
     * @return GitHub
     */
    GitHub github();

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
     * @see <a href="https://developer.github.com/v3/issues/events/#list-events-for-a-repository">List Events for a Repository</a>
     */
    IssueEvents issueEvents();

    /**
     * Get all labels of the repo.
     * @return Labels
     * @see <a href="https://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    Labels labels();

    /**
     * Get all available assignees to which issues may be assigned.
     * @return Assignees
     * @see <a href="https://developer.github.com/v3/issues/assignees/">Assignees API</a>
     */
    Assignees assignees();

    /**
     * Get all releases of the repo.
     * @return Releases
     * @see <a href="https://developer.github.com/v3/repos/releases/">Releases API</a>
     */
    Releases releases();

    /**
     * Get all deploy keys of the repo.
     * @return DeployKeys
     * @see <a href="https://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
     */
    DeployKeys keys();

    /**
     * Get all forks of the repo.
     * @return Forks
     * @see <a href="https://developer.github.com/v3/repos/forks/">Forks API</a>
     */
    Forks forks();

    /**
     * Get repository's commits.
     * @return Commits
     * @see <a href="https://developer.github.com/v3/repos/commits/">Commits API</a>
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
     * @see <a href="https://developer.github.com/v3/repos/contents/">Contents API</a>
     */
    Contents contents();

    /**
     * Gel all collaborators.
     * @return Collaborators
     * @see <a href="https://developer.github.com/v3/repos/collaborators/">Collaborators API</a>
     */
    Collaborators collaborators();

    /**
     * Get the Git API entry point.
     * @return Collaborators
     * @see <a href="https://developer.github.com/v3/git/">Git Data API</a>
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
     * Get default branch.
     *
     * @return Default branch.
     * @throws IOException If there is any I/O problem.
     */
    Branch defaultBranch() throws IOException;

    /**
     * Lists the people that have starred the repository.
     * @return Lists the people that have starred the repository.
     */
    Stargazers stargazers();

    /**
     * Smart Repo with extra features.
     * @since 0.1
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"repo", "jsn"})
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
         * Does this Repo actually exist in GitHub?
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
        public GitHub github() {
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
        public Branch defaultBranch() throws IOException {
            return this.repo.defaultBranch();
        }

        @Override
        public Stargazers stargazers() {
            throw new UnsupportedOperationException(
                "stargazers() not yet implemented"
            );
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
