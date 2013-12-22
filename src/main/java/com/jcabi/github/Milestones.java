/**
 * Copyright (c) 2012-2013, JCabi.com
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
 * Github Milestones.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/issues/milestones/">Milestones API</a>
 * @since 0.5
 * @todo #1:1hr New method remove() to delete a Milestone. Let's add a new
 *  method to remove a milestone by id, as explained in
 *  http://developer.github.com/v3/issues/milestones/#delete-a-milestone.
 *  The method should be tested by unit and integration tests.
 */
@Immutable
public interface Milestones {

    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Create Milestone.
     * @param title Milestone creation JSON
     * @return Milestone
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/milestones/#create-a-milestone">Create Milestone</a>
     * @since 0.5
     */
    @NotNull(message = "repository is never NULL")
    Milestone create(@NotNull(message = "Title can't be NULL") String title)
        throws IOException;

    /**
     * Get specific milestone by number.
     * @param number Milestone number
     * @return Milestone
     * @see <a href="http://developer.github.com/v3/issues/milestones/#get-a-single-milestone">Get a single milestone</a>
     */
    @NotNull(message = "milestone is never NULL")
    Milestone get(int number);

    /**
     * Iterate them all.
     * @param params Iterating parameters, as requested by API
     * @return Iterator of milestones
     * @see <a href="http://developer.github.com/v3/issues/milestones/#list-milestones-for-a-repository">List milestones for a repository</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Milestone> iterate(
        @NotNull(message = "map of params can't be NULL")
        Map<String, String> params);
}
