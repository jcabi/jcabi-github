/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github user's emails.
 * @since 0.8
 */
@Immutable
public interface UserEmails extends JsonReadable {

    /**
     * Iterate all user's emails.
     * @return Emails
     * @throws IOException If there is any I/O problem
     */
    Iterable<String> iterate() throws IOException;

    /**
     * Add emails.
     * @param emails Emails
     * @return Emails
     * @throws IOException If there is any I/O problem
     */
    Iterable<String> add(
        Iterable<String> emails)
        throws IOException;

    /**
     * Remove emails.
     * @param emails Emails
     * @throws IOException If there is any I/O problem
     */
    void remove(
        Iterable<String> emails)
        throws IOException;

}
