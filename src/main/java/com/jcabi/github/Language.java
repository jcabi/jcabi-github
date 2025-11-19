/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub repository language.
 * @since 0.19
 * @see <a href="https://developer.github.com/v3/repos/#list-languages">List languages</a>
 */
@Immutable
public interface Language {
    /**
     * Language name.
     * @return Name
     */
    String name();
    /**
     * Number of bytes of code written in that language.
     * @return Number of bytes
     */
    long bytes();
}
