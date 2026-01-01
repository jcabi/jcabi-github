/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for Runtime Reaction.
 * @since 1.0
 */
final class RtReactionTest {

    @Test
    void throwsExceptionOnInvalidReaction() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RtReaction(new Reaction.Simple("invalid")).type(),
            "Should throw on invalid reaction"
        );
    }
}
