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
import com.jcabi.http.Request;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtUser implements User {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Login of the user.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
    RtUser(
        @NotNull(message = "github can't be NULL") final Github github,
        @NotNull(message = "req can't be NULL") final Request req
    ) {
        this(github, req, "");
    }

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param login User identity/identity
     */
    RtUser(
        @NotNull(message = "github can't be NULL") final Github github,
        @NotNull(message = "req can't be NULL") final Request req,
        @NotNull(message = "login can't be NULL") final String login
    ) {
        this.ghub = github;
        if (login.isEmpty()) {
            this.request = req.uri().path("/user").back();
        } else {
            this.request = req.uri().path("/users").path(login).back();
        }
        this.self = login;
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    @NotNull(message = "github is never NULL")
    public Github github() {
        return this.ghub;
    }

    @Override
    @NotNull(message = "login is never NULL")
    public String login() throws IOException {
        final String login;
        if (this.self.isEmpty()) {
            login = this.json().getString("login");
        } else {
            login = this.self;
        }
        return login;
    }

    @Override
    @NotNull(message = "organizations is never NULL")
    public Organizations organizations() {
        return new RtOrganizations(this.ghub, this.ghub.entry(), this);
    }

    @Override
    @NotNull(message = "PublicKeys is never NULL")
    public PublicKeys keys() {
        return new RtPublicKeys(this.ghub.entry(), this);
    }

    @Override
    @NotNull(message = "user emails is never NULL")
    public UserEmails emails() {
        return new RtUserEmails(this.ghub.entry());
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(
        @NotNull(message = "JSON is never NULL") final JsonObject json)
        throws IOException {
        new RtJson(this.request).patch(json);
    }

}
