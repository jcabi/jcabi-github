/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * Github tree.
 *
 * @see <a href="https://developer.github.com/v3/git/trees/">Trees API</a>
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
