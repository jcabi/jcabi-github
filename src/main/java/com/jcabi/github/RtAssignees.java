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
import com.jcabi.immutable.ArrayMap;
import com.rexsl.test.Request;
import java.io.IOException;
import java.util.Map;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github Assignees.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #94 Assignees API should be implemented. Let's implement
 *  method assignees() in RtRepo and MkRepo.
 *  See http://developer.github.com/v3/issues/assignees/
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
final class RtAssignees implements Assignees {

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
    RtAssignees(final Repo repo, final Request req) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/assignees")
            .back();
        this.owner = repo;
    }

    @Override
    public Iterable<User> iterate(
        @NotNull(message = "map of params can't be NULL")
        final Map<String, String> params) {
        return new RtPagination<User>(
            this.request,
            new RtPagination.Mapping<User>() {
                @Override
                public User map(final JsonObject object) {
                    return new RtUser(
                        RtAssignees.this.owner.github(),
                        RtAssignees.this.entry,
                        object.getString("login")
                    );
                }
            }
        );
    }

    @Override
    public boolean check(final String login) throws IOException {
        final Iterable<User> assignees = new Smarts<User>(
            new Bulk<User>(
                this.iterate(
                    new ArrayMap<String, String>().with("sort", "login")
                )
            )
        );
        boolean found = false;
        for (final User assignee : assignees) {
            if (login.equals(assignee.login())) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }
}
