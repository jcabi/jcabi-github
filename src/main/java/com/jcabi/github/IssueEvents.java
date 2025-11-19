/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub issue events.
 * @since 0.23
 * @see <a href="https://developer.github.com/v3/issues/events/">Issue Events API</a>
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
