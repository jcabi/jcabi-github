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
import javax.validation.constraints.NotNull;

/**
 * Github Releases.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@Immutable
public interface Releases {
    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of releases
     * @see <a href="http://developer.github.com/v3/repos/releases/#list">List</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Release> iterate();

    /**
     * Get a single release.
     * @param number Release id
     * @return Release
     * @see <a href="http://developer.github.com/v3/repos/releases/#get-a-single-release">Get a single release</a>
     */
    @NotNull(message = "release is never NULL")
    Release get(int number);

    /**
     * Create new release.
     * @param tag The name of the tag
     * @return Release just created
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/releases/#create-a-release">Create an Release</a>
     */
    @NotNull(message = "release is never NULL")
    Release create(
        @NotNull(message = "release tag is never NULL") String tag)
        throws IOException;

    /**
     * Remove a release.
     *
     * @param number ID of the release to remove.
     * @throws IOException If an IO problem occurs.
     * @see <a href="http://developer.github.com/v3/repos/releases/#delete-a-release">Delete a release.</a>
     */
    void remove(int number) throws IOException;
}
