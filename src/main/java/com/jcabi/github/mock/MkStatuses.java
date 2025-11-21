/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Commit;
import com.jcabi.github.Status;
import com.jcabi.github.Statuses;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Mock of GitHub commit statuses.
 * @since 0.24
 * @todo #1129:30min Finish implementing this class (MkStatuses), a mock of
 *  GitHub's commits statuses (the "Statuses" interface).
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "cmmt")
final class MkStatuses implements Statuses {
    /**
     * Commit whose statuses this represents.
     */
    private final transient Commit cmmt;

    /**
     * Ctor.
     * @param cmt Commit whose statuses this represents
     */
    MkStatuses(
        final Commit cmt
    ) {
        this.cmmt = cmt;
    }

    @Override
    public Commit commit() {
        return this.cmmt;
    }

    @Override
    public Status create(
        final Statuses.StatusCreate status
    ) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Iterable<Status> list(
        final String ref
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public JsonObject json() {
        throw new UnsupportedOperationException("Yet to be implemented");
    }
}
