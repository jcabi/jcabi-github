/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub issue events.
 * @see <a href="https://developer.github.com/v3/issues/events/">Issue Events API</a>
 * @since 0.23
 */
@Immutable
public interface IssueEvents {
    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get specific issue event by number.
     * @param number Issue event number
     * @return Event
     * @see <a href="https://developer.github.com/v3/issues/events/#get-a-single-event">Get a single event</a>
     */
    Event get(int number);

    /**
     * Iterate over all issue events.
     * @return Iterator of issue events
     * @see <a href="https://developer.github.com/v3/issues/events/#list-events-for-a-repository">List events for a repository</a>
     */
    Iterable<Event> iterate();
}
