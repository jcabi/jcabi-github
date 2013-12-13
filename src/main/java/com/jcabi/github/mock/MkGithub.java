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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Gists;
import com.jcabi.github.Github;
import com.jcabi.github.Limits;
import com.jcabi.github.Markdown;
import com.jcabi.github.Repos;
import com.jcabi.github.Users;
import com.rexsl.test.Request;
import com.rexsl.test.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

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
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self" })
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
    public MkGithub(final String login) throws IOException {
        this(new MkStorage.InFile(), login);
    }

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     */
    public MkGithub(final MkStorage stg, final String login) {
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
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Gists gists() {
        try {
            return new MkGists(this.storage, this.self);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Users users() {
        try {
            return new MkUsers(this.storage, this.self);
        } catch (IOException ex) {
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
    public JsonObject emojis() {
        return Json.createObjectBuilder()
            .add("+1", "http://locahost/up")
            .add("-1", "http://locahost/down")
            .build();
    }

    /**
     * {@inheritDoc}
     * @todo #6:30min Markdown rendering mechanism should be mocked
     *  and implemented in MkMarkdown class, in a primitive way, of course.
     *  We don't need to do a real rendering, but at least return the
     *  same text back. When done, just remote this entire JavaDoc block.
     */
    @Override
    public Markdown markdown() {
        throw new UnsupportedOperationException("#markdown()");
    }

}
