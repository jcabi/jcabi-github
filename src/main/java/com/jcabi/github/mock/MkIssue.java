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

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comments;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;

/**
 * Mock Github issue.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords", "num" })
@SuppressWarnings("PMD.TooManyMethods")
final class MkIssue implements Issue {

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
     * @param number Issue number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkIssue(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int number
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.num = number;
    }

    @Override
    public String toString() {
        return Integer.toString(this.num);
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
    public Comments comments() {
        try {
            return new MkComments(
                this.storage, this.self, this.coords, this.num
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public IssueLabels labels() {
        try {
            return new MkIssueLabels(
                this.storage, this.self, this.coords, this.num
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Iterable<Event> events() throws IOException {
        return new MkIterable<Event>(
            this.storage,
            String.format(
                // @checkstyle LineLength (1 line)
                "/github/repos/repo[@coords='%s']/issue-events/issue-event[issue='%s']",
                this.coords,
                this.num
            ),
            new MkIssueEventMapping(this)
        );
    }

    @Override
    public boolean exists() throws IOException {
        return this.storage.xml().xpath(
            String.format("%s/number/text()", this.xpath())
        ).size() == 1;
    }

    @Override
    public int compareTo(
        final Issue issue
    ) {
        return this.number() - issue.number();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        final Issue.Smart smart = new Issue.Smart(this);
        final boolean was = smart.isOpen();
        new JsonPatch(this.storage).patch(this.xpath(), json);
        final boolean now = smart.isOpen();
        if (now != was) {
            final String type;
            if (now) {
                type = Event.REOPENED;
            } else {
                type = Event.CLOSED;
            }
            new MkIssueEvents(this.storage, this.self, this.coords)
                .create(type, this.num, this.self, Optional.<String>absent());
        }
    }

    @Override
    public JsonObject json() throws IOException {
        final JsonObject obj = new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
        final JsonObjectBuilder json = Json.createObjectBuilder();
        for (final Map.Entry<String, JsonValue> val: obj.entrySet()) {
            json.add(val.getKey(), val.getValue());
        }
        final JsonArrayBuilder array = Json.createArrayBuilder();
        for (final Label label : this.labels().iterate()) {
            array.add(
                Json.createObjectBuilder().add("name", label.name()).build()
            );
        }
        return json
            .add("labels", array)
            .add(
                // @checkstyle MultipleStringLiteralsCheck (1 line)
                "assignee",
                Json.createObjectBuilder().add(
                    "login", obj.getString("assignee", "")
                ).build()
            )
            .add(
                "pull_request",
                Json.createObjectBuilder().addNull("html_url").build()
            )
            .build();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']",
            this.coords, this.num
        );
    }

    private static class MkIssueEventMapping
        implements MkIterable.Mapping<Event> {
        /**
         * Issue events.
         */
        private final transient MkIssueEvents evts;

        /**
         * Constructor.
         * @param issue Mock issue to get events from
         * @throws IOException If there is any I/O problem
         */
        public MkIssueEventMapping(
            final MkIssue issue
        ) throws IOException {
            this.evts = new MkIssueEvents(
                issue.storage,
                issue.self,
                issue.coords
            );
        }

        @Override
        public Event map(
            final XML xml
        ) {
            return this.evts.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            );
        }
    }
}
