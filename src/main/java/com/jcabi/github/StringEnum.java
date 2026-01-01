/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * An enum whose values correspond to unique strings.
 *
 * @since 0.22.0
 */
@Immutable
public interface StringEnum {
    /**
     * The string that this enum value represents.
     * @return String
     */
    String identifier();
}
