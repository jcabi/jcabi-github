/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Event;
import com.jcabi.xml.XML;

/**
 * Mapping for MkIssueEvents.
 * @since 0.5
 */
class MkIssueEventMapping
    implements MkIterable.Mapping<Event> {

    /**
     * Issue events.
     */
    private final transient MkIssueEvents evts;

    /**
     * Constructor.
     * @param events Mock events of the issue
     */
    MkIssueEventMapping(final MkIssueEvents events) {
        this.evts = events;
    }

    @Override
    public Event map(final XML xml) {
        return this.evts.get(
            Integer.parseInt(xml.xpath("number/text()").get(0))
        );
    }
}
