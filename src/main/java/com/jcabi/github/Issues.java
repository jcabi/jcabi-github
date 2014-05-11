/**
 * Copyright (c) 2013-2014, jcabi.com
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
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Github issues.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/">Issues API</a>
 */
@Immutable
public interface Issues {

    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get specific issue by number.
     * @param number Issue number
     * @return Issue
     * @see <a href="http://developer.github.com/v3/issues/#get-a-single-issue">Get a Single Issue</a>
     */
    @NotNull(message = "issue is never NULL")
    Issue get(int number);

    /**
     * Create new issue.
     * @param title Title
     * @param body Body of it
     * @return Issue just created
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/#create-an-issue">Create an Issue</a>
     */
    @NotNull(message = "issue is never NULL")
    Issue create(
        @NotNull(message = "issue title is never NULL") String title,
        @NotNull(message = "issue body is never NULL") String body)
        throws IOException;

    /**
     * Iterate them all.
     * @param params Iterating parameters, as requested by API
     * @return Iterator of issues
     * @see <a href="http://developer.github.com/v3/issues/#list-issues">List Issues</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Issue> iterate(@NotNull(message = "map of params can't be NULL")
        Map<String, String> params);

}
