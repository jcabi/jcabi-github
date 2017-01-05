/**
 * Copyright (c) 2013-2017, jcabi.com
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

/**
 * Github Milestones.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/issues/milestones/">Milestones API</a>
 * @since 0.7
 */
@Immutable
public interface Milestones {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Create Milestone.
     * @param title Milestone creation JSON
     * @return Milestone
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/milestones/#create-a-milestone">Create Milestone</a>
     * @since 0.5
     */
    Milestone create(String title)
        throws IOException;

    /**
     * Get specific milestone by number.
     * @param number Milestone number
     * @return Milestone
     * @see <a href="http://developer.github.com/v3/issues/milestones/#get-a-single-milestone">Get a single milestone</a>
     */
    Milestone get(int number);

    /**
     * Iterate them all.
     * @param params Iterating parameters, as requested by API
     * @return Iterator of milestones
     * @see <a href="http://developer.github.com/v3/issues/milestones/#list-milestones-for-a-repository">List milestones for a repository</a>
     */
    Iterable<Milestone> iterate(
        Map<String, String> params);

    /**
     * Remove milestone by number.
     * @param number Milestone number
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/milestones/#delete-a-milestone">Delete a milestone</a>
     */
    void remove(int number)
        throws IOException;
}
