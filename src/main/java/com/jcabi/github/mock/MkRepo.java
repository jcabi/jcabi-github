/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Assignees;
import com.jcabi.github.Branch;
import com.jcabi.github.Branches;
import com.jcabi.github.Collaborators;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.DeployKeys;
import com.jcabi.github.Forks;
import com.jcabi.github.Git;
import com.jcabi.github.Github;
import com.jcabi.github.Hooks;
import com.jcabi.github.IssueEvents;
import com.jcabi.github.Issues;
import com.jcabi.github.Labels;
import com.jcabi.github.Language;
import com.jcabi.github.Milestones;
import com.jcabi.github.Notifications;
import com.jcabi.github.Pulls;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommits;
import com.jcabi.github.RtLanguage;
import com.jcabi.github.Stargazers;
import com.jcabi.github.Stars;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github repo.
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexity (500 lines)
 * @todo #1061 Fix code to avoid CouplingBetweenObjects
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = {"storage", "self", "coords"})
@SuppressWarnings
    (
        {
            "PMD.TooManyMethods",
            "PMD.ExcessiveImports",
            "PMD.CouplingBetweenObjects"
        }
    )
final class MkRepo implements Repo {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param repo Repo name
     */
    MkRepo(
        final MkStorage stg,
        final String login,
        final Coordinates repo
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
    }

    @Override
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    public Coordinates coordinates() {
        return this.coords;
    }

    @Override
    public Issues issues() {
        try {
            return new MkIssues(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Milestones milestones() {
        try {
            return new MkMilestones(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Pulls pulls() {
        try {
            return new MkPulls(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Hooks hooks() {
        try {
            return new MkHooks(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public IssueEvents issueEvents() {
        try {
            return new MkIssueEvents(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Labels labels() {
        try {
            return new MkLabels(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Assignees assignees() {
        return new MkAssignees(this.storage, this.self, this.coords);
    }

    @Override
    public Releases releases() {
        try {
            return new MkReleases(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Forks forks() {
        try {
            return new MkForks(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Collaborators collaborators() {
        try {
            return new MkCollaborators(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public DeployKeys keys() {
        try {
            return new MkDeployKeys(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Contents contents() {
        try {
            return new MkContents(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public RepoCommits commits() {
        try {
            return new MkRepoCommits(
                this.storage, this.self, this.coordinates()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Branches branches() {
        try {
            return new MkBranches(
                this.storage, this.self, this.coordinates()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Git git() {
        try {
            return new MkGit(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Stars stars() {
        try {
            return new MkStars(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Notifications notifications() {
        return new MkNotifications(
            this.storage,
            this.xpath().concat("/notifications/notification")
        );
    }

    @Override
    public Iterable<Language> languages() {
        final List<Language> languages = new ArrayList<>(0);
        final int java = 999;
        languages.add(new RtLanguage("Java", java));
        final int php = 888;
        languages.add(new RtLanguage("PHP", php));
        final int ruby = 777;
        languages.add(new RtLanguage("Ruby", ruby));
        return languages;
    }

    @Override
    public Branch defaultBranch() {
        return new MkBranch(
            this.storage,
            this.self,
            this.coords,
            "master",
            ""
        );
    }

    @Override
    public Stargazers stargazers() {
        throw new UnsupportedOperationException(
            String.format(
                "%s.stargazers() not yet implemented",
                this.getClass().getSimpleName()
            )
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public int compareTo(final Repo repo) {
        return this.coords.compareTo(repo.coordinates());
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']",
            this.coords
        );
    }

}
