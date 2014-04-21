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
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github event.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtEvent implements Event {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Event number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param number Number of the get
     */
    RtEvent(final Request req, final Repo repo, final int number) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path("/events")
            .path(Integer.toString(number))
            .back();
        this.owner = repo;
        this.num = number;
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    @NotNull(message = "repository can't be NULL")
    public Repo repo() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    @NotNull(message = "JSON can't be NULL")
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public int compareTo(
        @NotNull(message = "Event can't be NULL") final Event event
    ) {
        return this.number() - event.number();
    }

}
