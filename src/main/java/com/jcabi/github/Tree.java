/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub tree.
 * @see <a href="https://developer.github.com/v3/git/trees/">Trees API</a>
 * @since 0.6
 */
@Immutable
public interface Tree extends JsonReadable {

    /**
     * The repo we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * SHA of it.
     * @return SHA
     */
    String sha();

}
