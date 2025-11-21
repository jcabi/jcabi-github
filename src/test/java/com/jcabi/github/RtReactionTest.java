/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for Runtime Reaction.
 * @since 1.0
 */
public final class RtReactionTest {

    @Test
    public void throwsExceptionOnInvalidReaction() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RtReaction(new Reaction.Simple("invalid")).type(),
            "Should throw on invalid reaction"
        );
    }
}
