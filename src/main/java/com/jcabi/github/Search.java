/**
 * Copyright (c) 2013-2014, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Github search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/search/">Search API</a>
 * @todo #124 The {@link Search} interface currently defines methods for
 *  searching repos, issues, and users. We need to add a new method codes()
 *  for the purposes for code searching. At the moment, we don't have a way to
 *  represent codes in the API.
 */
@Immutable
public interface Search {

    /**
     * Github we're in.
     *
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Search repositories.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Repos
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-repositories">Search repositories</a>
     */
    @NotNull(message = "Iterable of repos is never NULL")
    Iterable<Repo> repos(
        @NotNull(message = "Search keywords can't be NULL") String keywords,
        @NotNull(message = "Sort field can't be NULL") String sort,
        @NotNull(message = "Sort order can't be NULL") String order
    ) throws IOException;

    /**
     * Search issues.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Issues
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-issues">Search issues</a>
     */
    @NotNull(message = "Iterable of issues is never NULL")
    Iterable<Issue> issues(
        @NotNull(message = "Search keywords can't be NULL") String keywords,
        @NotNull(message = "Sort field can't be NULL") String sort,
        @NotNull(message = "Sort order can't be NULL") String order)
        throws IOException;

    /**
     * Search users.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Users
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-users">Search users</a>
     */
    @NotNull(message = "Iterable of users is never NULL")
    Iterable<User> users(
        @NotNull(message = "Search keywords can't be NULL") String keywords,
        @NotNull(message = "Sort field can't be NULL") String sort,
        @NotNull(message = "Sort order can't be NULL") String order)
        throws IOException;
}
