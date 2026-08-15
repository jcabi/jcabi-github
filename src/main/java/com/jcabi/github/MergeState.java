/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * State of a pull request merge attempt.
 * @since 0.24
 */
public enum MergeState {

    /**
     * If the Pull Request was successfully merged.
     */
    SUCCESS,
    /**
     * If the Pull Request wasn't mergeable.
     */
    NOT_MERGEABLE,
    /**
     * If the current Pull Request head doesn't match the specified SHA.
     */
    BAD_HEAD
}
