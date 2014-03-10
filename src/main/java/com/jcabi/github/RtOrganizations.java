/**
 * Copyright (c) 2013-2014, JCabi.com
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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "ghub", "request", "owner" })
final class RtOrganizations implements Organizations {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param user User
     */
    RtOrganizations(final Github github, final Request req, final User user) {
        this.entry = req;
        this.ghub = github;
        this.owner = user;
        this.request = this.entry.uri().path("/user").path("/orgs").back();
    }

    @Override
    @NotNull(message = "Github is never NULL")
    public Github github() {
        return this.ghub;
    }

    @Override
    @NotNull(message = "user is never NULL")
    public User user() {
        return this.owner;
    }

    @Override
    @NotNull(message = "organization is never NULL")
    public Organization get(final String login) {
        return new RtOrganization(this.ghub, this.entry, login);
    }

    @Override
    @NotNull(message = "Iterable of orgs is never NULL")
    public Iterable<Organization> iterate() {
        return new RtPagination<Organization>(
            this.request,
            new RtPagination.Mapping<Organization, JsonObject>() {
                @Override
                public Organization map(final JsonObject object) {
                    return RtOrganizations.this.get(object.getString("login"));
                }
            }
        );
    }

    @Override
    @NotNull(message = "Iterable of orgs is never NULL")
    public Iterable<Organization> iterate(
        @NotNull(message = "username can't be NULL") final String username
    ) {
        return new RtPagination<Organization>(
            this.entry.uri().path("/users").path(username).path("/orgs").back(),
            new RtPagination.Mapping<Organization, JsonObject>() {
                @Override
                public Organization map(final JsonObject object) {
                    return RtOrganizations.this.get(object.getString("login"));
                }
            }
        );
    }
}
