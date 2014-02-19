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
import com.jcabi.http.Request;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Implementation of Collaborators.
 * @todo #371 Implement isCollaborator, add and remove methods.
 *  They should be implemented as described at
 *  http://developer.github.com/v3/repos/collaborators/
 *  Tests as com.jcabi.github.RtCollaboratorsTest should be also implemented.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
public final class RtCollaborators implements Collaborators {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param repo Repo
     * @param req Request
     */
    public RtCollaborators(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/collaborators")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public boolean isCollaborator(
        @NotNull(message = "User is never null") final String user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(
        @NotNull(message = "User is never null") final String user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final String user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<User> iterate() {
        return new RtPagination<User>(
            this.request,
            new RtPagination.Mapping<User, JsonObject>() {
                @Override
                public User map(final JsonObject object) {
                    return new RtUser(
                        RtCollaborators.this.owner.github(),
                        RtCollaborators.this.entry,
                        object.getString("login")
                    );
                }
            }
        );
    }
}
