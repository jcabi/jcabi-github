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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github event.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "type")
final class MkEvent implements Event {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Type of event.
     */
    private final transient String type;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param tpe Type
     * @checkstyle ParameterNumber (5 lines)
     */
    MkEvent(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep,
        @NotNull(message = "tpe can't be NULL") final String tpe
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.type = tpe;
    }

    @Override
    @NotNull(message = "repo is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public int number() {
        return 0;
    }

    @Override
    public int compareTo(
        @NotNull(message = "event can't be NULL") final Event event
    ) {
        throw new UnsupportedOperationException("#compareTo()");
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() {
        return Json.createObjectBuilder().add("event", this.type).add(
            "actor", Json.createObjectBuilder().add("login", "test").build()
        ).add("created_at", new Github.Time().toString()).build();
    }
}
