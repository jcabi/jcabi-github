/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.GitHub;
import com.jcabi.github.IssueEvents;
import com.jcabi.github.Repo;
import com.jcabi.log.Logger;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub issue events.
 *
 * @since 0.23
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkIssueEvents implements IssueEvents {
    /**
     * XPath suffix for issue event number text.
     */
    private static final String EVENT_NUMBER_TEXT_PATH =
        "/issue-event/number/text()";

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
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/issue-event"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
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
        final String created = new GitHub.Time().toString();
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat(MkIssueEvents.EVENT_NUMBER_TEXT_PATH)
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
