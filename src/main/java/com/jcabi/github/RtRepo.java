/**
 * Copyright (c) 2013-2025, jcabi.com
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
import com.jcabi.http.Request;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;

/**
 * Github repository.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexity (10 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"ghub", "entry", "coords"})
@SuppressWarnings
    (
        {
            "PMD.TooManyMethods",
            "PMD.CouplingBetweenObjects"
        }
    )
final class RtRepo implements Repo {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param crd Coordinate of the repo
     */
    RtRepo(final Github github, final Request req, final Coordinates crd) {
        this.ghub = github;
        this.entry = req;
        this.coords = crd;
        this.request = this.entry.uri()
            .path("/repos")
            .path(this.coords.user())
            .path(this.coords.repo())
            .back();
    }

    @Override
    public String toString() {
        return this.coords.toString();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public Coordinates coordinates() {
        return this.coords;
    }

    @Override
    public Issues issues() {
        return new RtIssues(this.entry, this);
    }

    @Override
    public Milestones milestones() {
        return new RtMilestones(this.entry, this);
    }

    @Override
    public Pulls pulls() {
        return new RtPulls(this.entry, this);
    }

    @Override
    public Hooks hooks() {
        return new RtHooks(this.entry, this);
    }

    @Override
    public IssueEvents issueEvents() {
        return new RtIssueEvents(this.entry, this);
    }

    @Override
    public Labels labels() {
        return new RtLabels(this.entry, this);
    }

    @Override
    public Assignees assignees() {
        return new RtAssignees(this.entry, this);
    }

    @Override
    public Releases releases() {
        return new RtReleases(this.entry, this);
    }

    @Override
    public DeployKeys keys() {
        return new RtDeployKeys(this.entry, this);
    }

    @Override
    public Forks forks() {
        return new RtForks(this.entry, this);
    }

    @Override
    public Contents contents() {
        return new RtContents(this.entry, this);
    }

    @Override
    public Collaborators collaborators() {
        return new RtCollaborators(this.entry, this);
    }

    @Override
    public Git git() {
        return new RtGit(this.entry, this);
    }

    @Override
    public Stars stars() {
        return new RtStars(this.entry, this);
    }

    @Override
    public Notifications notifications() {
        return new RtNotifications(
            this.request.uri().path("notifications").back()
        );
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Iterable<Language> languages() throws IOException {
        final RtJson json = new RtJson(
            this.request.uri()
                .path("/languages")
                .back()
        );
        final JsonObject object = json.fetch();
        final List<Language> languages =
            new ArrayList<>(object.size());
        for (final Map.Entry<String, JsonValue> value : object.entrySet()) {
            final String name = value.getKey();
            languages.add(
                new RtLanguage(
                    name,
                    object.getJsonNumber(name).longValue()
                )
            );
        }
        return languages;
    }

    @Override
    public Branch defaultBranch() throws IOException {
        return new RtBranch(
            this.request,
            this,
            this.json().getString("default_branch"),
            ""
        );
    }

    @Override
    public Stargazers stargazers() {
        return new RtStargazers(this.request);
    }

    @Override
    public void patch(
        final JsonObject json
    )
        throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public RepoCommits commits() {
        return new RtRepoCommits(this.entry, this);
    }

    @Override
    public Branches branches() {
        return new RtBranches(this.entry, this);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public int compareTo(final Repo repo) {
        return this.coords.compareTo(repo.coordinates());
    }
}
