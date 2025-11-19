/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.util.Arrays;

/**
 * Runtime Reaction.
 * @since 1.0
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public final class RtReaction implements Reaction {

    /**
     * Origin Reaction.
     */
    private final Reaction origin;

    /**
     * Constructor.
     * @param reaction Decorated reaction.
     */
    public RtReaction(final Reaction reaction) {
        this.origin = reaction;
    }

    @Override
    public String type() {
        if (Arrays.asList(
            Reaction.CONFUSED, Reaction.HEART, Reaction.HOORAY,
            Reaction.LAUGH, Reaction.THUMBSDOWN, Reaction.THUMBSUP
        ).contains(this.origin.type())
        ) {
            return this.origin.type();
        }
        throw new IllegalArgumentException(
            String.format(
                "Invalid reaction: \"%s\"",
                this.origin.type()
            )
        );
    }
}
