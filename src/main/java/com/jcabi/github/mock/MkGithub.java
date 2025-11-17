/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.github.Gists;
import com.jcabi.github.Github;
import com.jcabi.github.Gitignores;
import com.jcabi.github.Limits;
import com.jcabi.github.Markdown;
import com.jcabi.github.Organizations;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Search;
import com.jcabi.github.Users;
import com.jcabi.http.Request;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Mock Github client.
 *
 * <p>This is how you use it:
 *
 * <pre> GitHub gitHub = new MkGithub("username");
 * Repos.RepoCreate create = new Repos.RepoCreate("dummy", false);
 * Repo repo = gitHub.repos().create(create);
 * Issue issue = repo.issues().create("title", "body");</pre>
 *
 * <p>By default, it works with a temporary file, which will be deleted
 * on JVM exit:
 *
 * <pre> Github github = new MkGithub("jeff");</pre>
 * @since 0.5
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self" })
@SuppressWarnings("PMD.TooManyMethods")
public final class MkGithub implements Github {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @throws IOException If there is any I/O problem
     */
    public MkGithub() throws IOException {
        this("jeff");
    }

    /**
     * Public ctor.
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    public MkGithub(
        final String login
    ) throws IOException {
        this(new MkStorage.Synced(new MkStorage.InFile()), login);
    }

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     */
    public MkGithub(
        final MkStorage stg,
        final String login
    ) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public String toString() {
        return this.storage.toString();
    }

    @Override
    public Request entry() {
        return new FakeRequest()
            .withBody("{}")
            .withStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public Repos repos() {
        try {
            return new MkRepos(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Gists gists() {
        try {
            return new MkGists(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Users users() {
        try {
            return new MkUsers(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Organizations organizations() {
        try {
            return new MkOrganizations(this.storage);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Limits limits() {
        return new MkLimits(this.storage, this.self);
    }

    @Override
    public JsonObject meta() {
        return Json.createObjectBuilder()
            .add("hooks", Json.createArrayBuilder().build())
            .add("git", Json.createArrayBuilder().build())
            .build();
    }

    @Override
    public Search search() {
        return new MkSearch(this.storage, this.self);
    }

    @Override
    public Gitignores gitignores() {
        return new MkGitignores(this);
    }

    @Override
    public JsonObject emojis() {
        return Json.createObjectBuilder()
            .add("+1", "http://locahost/up")
            .add("-1", "http://locahost/down")
            .build();
    }

    @Override
    public Markdown markdown() {
        return new MkMarkdown(this);
    }

    /**
     * Relogin.
     * @param login User to login
     * @return Github
     */
    public Github relogin(final String login
    ) {
        return new MkGithub(this.storage, login);
    }

    /**
     * Create repo with random name.
     * @return Repo
     * @throws IOException If fails
     */
    public Repo randomRepo() throws IOException {
        return this.repos().create(
            new Repos.RepoCreate(
                RandomStringUtils.randomAlphanumeric(Tv.TWENTY),
                true
            )
        );
    }
}
