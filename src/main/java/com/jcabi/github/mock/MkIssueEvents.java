/**
 * Copyright (c) 2013-2022, jcabi.com
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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.IssueEvents;
import com.jcabi.github.Repo;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub issue events.
 *
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @since 0.23
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkIssueEvents implements IssueEvents {
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
     * Public constructor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    MkIssueEvents(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("issue-events")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Event get(final int number) {
        return new MkEvent(this.storage, this.self, this.coords, number);
    }

    @Override
    public Iterable<Event> iterate() {
        return new MkIterable<Event>(
            this.storage,
            String.format("%s/issue-event", this.xpath()),
            new MkIterable.Mapping<Event>() {
                @Override
                public Event map(final XML xml) {
                    return MkIssueEvents.this.get(
                        Integer.parseInt(xml.xpath("number/text()").get(0))
                    );
                }
            }
        );
    }

    /**
     * Creates a new issue event.
     * This has no equivalent in GitHub's public API, since GitHub generates
     * events automatically in response to some other API calls.
     * @param type Type of event
     * @param issue ID number of issue the event is regarding
     * @param login Username of actor who caused the event
     * @param label Label added or removed
     * @return The newly created issue event
     * @throws IOException If there is any I/O problem
     * @todo #1063:30min Make it possible to set the "assignee" field for
     *  "assigned"/"unassigned" events. Make it possible to set the
     *  "milestone" field for "milestoned"/"demilestoned" events. Make it
     *  possible to set the "rename" field for "renamed" events. Make it
     *  possible to set the "commit_id" field for events related to commits.
     *  See https://developer.github.com/v3/issues/events/ for details.
     * @checkstyle ParameterNumberCheck (4 lines)
     */
    public Event create(
        final String type,
        final int issue, final String login, final Optional<String> label
    ) throws IOException {
        final String created = new Github.Time().toString();
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/issue-event/number/text()", this.xpath())
            ).size();
            Directives directives = new Directives()
                .xpath(this.xpath())
                .add("issue-event")
                .add("issue").set(Integer.toString(issue)).up()
                .add("number").set(Integer.toString(number)).up()
                .add("event").set(type).up()
                .add("created_at").set(created).up()
                .add("login").set(login).up();
            if (label.isPresent()) {
                directives = directives.add("label").set(label.get()).up();
            }
            this.storage.apply(directives);
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            MkEvent.class,
            "issue event #%d of type %s created in %s for issue #%d by %s",
            number, type, this.self, issue, login
        );
        return this.get(number);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issue-events",
            this.coords
        );
    }
}
