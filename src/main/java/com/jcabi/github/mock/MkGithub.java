/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.aspects.Tv;
import com.jcabi.github.Gists;
import com.jcabi.github.Github;
import com.jcabi.github.Gitignores;
import com.jcabi.github.Limits;
import com.jcabi.github.Markdown;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Search;
import com.jcabi.github.Users;
import com.jcabi.http.Request;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Mock Github client.
 *
 * <p>This is how you use it:
 *
 * <pre> Github github = new MkGithub(new MkStorage.InFile(file), "jeff");
 * github.repos().create("jcabi/jcabi-github");
 * Repo repo = github.repos().get("jcabi/jcabi-github");
 * Issues issues = repo.issues();
 * Issue issue = issues.post("issue title", "issue body");</pre>
 *
 * <p>By default, it works with a temporary file, which will be deleted
 * on JVM exit:
 *
 * <pre> Github github = new MkGithub("jeff");</pre>
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
        @NotNull(message = "login can't be NULL") final String login
    ) throws IOException {
        this(new MkStorage.InFile(), login);
    }

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     */
    public MkGithub(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login should not be NULL") final String login
    ) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public String toString() {
        return this.storage.toString();
    }

    @Override
    @NotNull(message = "entry request is never NULL")
    public Request entry() {
        return new FakeRequest()
            .withBody("{}")
            .withStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    @NotNull(message = "repos is never NULL")
    public Repos repos() {
        try {
            return new MkRepos(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    @NotNull(message = "gists is never NULL")
    public Gists gists() {
        try {
            return new MkGists(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    @NotNull(message = "users is never NULL")
    public Users users() {
        try {
            return new MkUsers(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    @NotNull(message = "limits is never NULL")
    public Limits limits() {
        return new MkLimits(this.storage, this.self);
    }

    @Override
    @NotNull(message = "JSON meta is never NULL")
    public JsonObject meta() {
        return Json.createObjectBuilder()
            .add("hooks", Json.createArrayBuilder().build())
            .add("git", Json.createArrayBuilder().build())
            .build();
    }

    @Override
    @NotNull(message = "Search is never NULL")
    public Search search() {
        return new MkSearch(this.storage, this.self);
    }

    @Override
    @NotNull(message = "Gitignores is never NULL")
    public Gitignores gitignores() throws IOException {
        return new MkGitignores(this);
    }

    @Override
    @NotNull(message = "emojis JSON is never NULL")
    public JsonObject emojis() {
        return Json.createObjectBuilder()
            .add("+1", "http://locahost/up")
            .add("-1", "http://locahost/down")
            .build();
    }

    @Override
    @NotNull(message = "markdown is never NULL")
    public Markdown markdown() {
        return new MkMarkdown();
    }

    /**
     * Relogin.
     * @param login User to login
     * @return Github
     * @throws IOException If there is any I/O problem
     */
    @NotNull(message = "github is never NULL")
    public Github relogin(@NotNull(message = "login is never NULL")
        final String login
    ) throws IOException {
        return new MkGithub(this.storage, login);
    }

    /**
     * Create repo with random name.
     * @return Repo
     * @throws IOException If fails
     */
    @NotNull(message = "Repo is never NULL")
    public Repo randomRepo() throws IOException {
        return this.repos().create(
            Json.createObjectBuilder().add(
                "name",
                RandomStringUtils.randomAlphanumeric(Tv.TEN)
            ).build()
        );
    }
}
