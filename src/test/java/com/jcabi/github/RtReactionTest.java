/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.junit.Test;

/**
 * Tests for Runtime Reaction.
 *
 * @since 1.0
 */
public final class RtReactionTest {

    /**
     * Tests if RtReaction throws exception when reaction is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionOnInvalidReaction() {
        new RtReaction(new Reaction.Simple("invalid")).type();
    }
}
