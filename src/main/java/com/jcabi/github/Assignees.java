/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub Assignees.
 * @since 0.7
 */
@Immutable
public interface Assignees {

    /**
     * Iterate all available assignees.
     * @return Iterator of available assignees to which issues may be assigned
     * @see <a href="https://developer.github.com/v3/issues/assignees/#list-assignees">List assignees</a>
     */
    Iterable<User> iterate();

    /**
     * Check if a particular user is an assignee for a repository.
     * @param login Login of user to be checked
     * @return True if given assignee login belongs to an assignee for the repository
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/assignees/#check-assignee">Check assignee</a>
     */
    boolean check(String login)
        throws IOException;
}
