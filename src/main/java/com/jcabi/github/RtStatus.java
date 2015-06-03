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

import com.google.common.base.Optional;
import com.jcabi.aspects.Loggable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github commit status.
 * @author Marcin Cylke (maracin.cylke+github@gmail.com)
 * @version $Id$
 * @since 0.23
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "cstate", "url", "message", "name" })
public final class RtStatus implements Status {

    /**
     * Commit status.
     */
    private final transient State cstate;

    /**
     * Details url.
     */
    private final transient Optional<String> url;

    /**
     * Complete message.
     */
    private final transient Optional<String> message;

    /**
     * Short message.
     */
    private final transient Optional<String> name;

    /**
     * Create new status object.
     * @param status State.
     * @param address Target url.
     * @param descr Description.
     * @param ctx Context.
     * @checkstyle ParameterNumberCheck (500 lines)
     */
    public RtStatus(
        @NotNull(message = "status can't be NULL")
        final State status,
        @NotNull(message = "address optional itself can't be NULL")
        final Optional<String> address,
        @NotNull(message = "descr optional itself can't be NULL")
        final Optional<String> descr,
        @NotNull(message = "ctx optional itself can't be NULL")
        final Optional<String> ctx
    ) {
        this.url = address;
        this.message = descr;
        this.name = ctx;
        this.cstate = status;
    }

    /**
     * Get status of this status.
     * @return Present status
     */
    @Override
    @NotNull(message = "state is never NULL")
    public State state() {
        return this.cstate;
    }

    /**
     * Get url of the status.
     * @return Url as string
     */
    @Override
    public Optional<String> targetUrl() {
        return this.url;
    }

    /**
     * Get message from container.
     * @return Description string
     */
    @Override
    public Optional<String> description() {
        return this.message;
    }

    /**
     * Get name info from container.
     * @return Context
     */
    @Override
    public Optional<String> context() {
        return this.name;
    }

    @Override
    @NotNull(message = "json is never NULL")
    public JsonObject json() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("state", this.cstate.identifier());
        if (this.url.isPresent()) {
            builder = builder.add("target_url", this.url.get());
        }
        if (this.message.isPresent()) {
            builder = builder.add("description", this.message.get());
        }
        if (this.name.isPresent()) {
            builder = builder.add("context", this.name.get());
        }
        return builder.build();
    }
}
