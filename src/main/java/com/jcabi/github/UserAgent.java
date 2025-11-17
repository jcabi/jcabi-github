/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * User agent data.
 * @since 0.37
 */
interface UserAgent {

    /**
     * Format user-agent http header value.
     * @return String
     */
    String format();
}
