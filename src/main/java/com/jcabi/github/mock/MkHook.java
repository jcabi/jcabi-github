/**
 * Copyright (c) 2013-2018, jcabi.com
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
import com.jcabi.github.Hook;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github hook.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @todo #1425:30min Finish adding tests for MkHook#json(). Finish making
 *  MkHooks#create pass the missing attributes to MkHook.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords", "num" })
final class MkHook implements Hook {
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
     * Issue number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param number Hook number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkHook(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int number) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.num = number;
    }
    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }
    @Override
    public int number() {
        return this.num;
    }
    @Override
    public JsonObject json() throws IOException {
        final XML hook = this.storage.xml().nodes(
            String.format(
                "//repo[@coords='%s']/hooks/hook[id='%s']",
                this.coords, this.num
            )
        ).get(0);
        final JsonArrayBuilder events = Json.createArrayBuilder();
        for (final XML event : hook.nodes("events/child::*")) {
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            events.add(event.xpath("name()").get(0));
        }
        final JsonObjectBuilder configs = Json.createObjectBuilder();
        for (final XML config : hook.nodes("configs/child::*")) {
            configs.add(
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                config.xpath("name()").get(0), config.xpath("text()").get(0)
            );
        }
        return Json.createObjectBuilder()
            .add("id", this.number())
            .add("url", hook.xpath("url/text()").get(0))
            .add("test_url", hook.xpath("test_url/text()").get(0))
            .add("ping_url", hook.xpath("ping_url/text()").get(0))
            .add("name", hook.xpath("name/text()").get(0))
            .add("events", events)
            .add(
                "active",
                Boolean.parseBoolean(hook.xpath("active/text()").get(0))
            ).add("config", configs)
            .add("updated_at", hook.xpath("updated_at/text()").get(0))
            .add("created_at", hook.xpath("created_at/text()").get(0))
            .build();
    }
}
