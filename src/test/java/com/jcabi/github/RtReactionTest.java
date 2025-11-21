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

    // TODO: Convert to Assertions.assertThrows(IllegalArgumentException.class, () -> { ... });
    @Test
    public void throwsExceptionOnInvalidReaction() {
        new RtReaction(new Reaction.Simple("invalid")).type();
    }
}
