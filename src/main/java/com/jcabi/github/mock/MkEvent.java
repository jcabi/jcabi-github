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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Repo;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
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
@EqualsAndHashCode(of = { "storage", "self", "coords", "num" })
final class MkEvent implements Event {
    /**
     * Created at.
     */
    private static final String CREATED_AT = "created_at";
    /**
     * Event.
     */
    private static final String EVENT = "event";
    /**
     * Login.
     */
    private static final String LOGIN = "login";

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
     * ID number of event.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param nmbr Number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkEvent(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int nmbr
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.num = nmbr;
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
    public int compareTo(
        final Event event
    ) {
        throw new UnsupportedOperationException("#compareTo()");
    }

    /**
     * Describes the event in a JSON object.
     * @return JSON object
     * @throws IOException If there is any I/O problem
     * @todo #1063:30min When the event has a label, retrieve and include the
     *  label's color too. MkIssueEvents.create() will also need to be
     *  updated accordingly.
     */
    @Override
    public JsonObject json() throws IOException {
        final JsonObject obj = new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("id", this.num)
            .add(
                "url",
                String.format(
                    // @checkstyle LineLength (1 line)
                    "https://api.jcabi-github.invalid/repos/%s/issues/events/%s",
                    this.coords,
                    this.num
                )
            )
            .add("commit_id", JsonValue.NULL)
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            .add(MkEvent.EVENT, obj.getString(MkEvent.EVENT))
            .add(
                "actor",
                Json.createObjectBuilder()
                    // @checkstyle MultipleStringLiteralsCheck (1 line)
                    .add(MkEvent.LOGIN, obj.getString(MkEvent.LOGIN))
                    .build()
            )
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            .add(MkEvent.CREATED_AT, obj.getString(MkEvent.CREATED_AT));
        final String label = "label";
        if (obj.containsKey(label)) {
            builder = builder.add(
                label,
                Json.createObjectBuilder()
                    .add("name", obj.getString(label))
                    .build()
            );
        }
        return builder.build();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/issue-events/issue-event[number='%d']",
            this.coords, this.num
        );
    }
}
