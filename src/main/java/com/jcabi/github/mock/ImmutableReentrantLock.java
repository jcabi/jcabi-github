/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Immutable ReentrantLock.
 *
 * @since 0.18
 */
@Immutable
final class ImmutableReentrantLock extends ReentrantLock {
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -2683908226666647513L;
}
