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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;

/**
 * Github Repo API.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 * @see <a href="http://developer.github.com/v3/repos/">Repos API</a>
 */
@Immutable
public interface Repos {

    /**
     * Get its owner.
     * @return Github
     */
    @NotNull(message = "github is never NULL")
    Github github();
    //byte[]

    /**
     * Create repository.
     * @param json Repository creation JSON
     * @return Repository
     * @throws IOException If there is any I/O problem
     * @since 0.5
     * @see <a href="http://developer.github.com/v3/repos/#create">Create Repository</a>
     */
    @NotNull(message = "repository is never NULL")
    Repo create(@NotNull(message = "JSON can't be NULL") JsonObject json)
        throws IOException;

    /**
     * Get repository by name.
     * @param coords Repository name in "user/repo" format
     * @return Repository
     * @see <a href="http://developer.github.com/v3/repos/#get">Get Repository</a>
     */
    @NotNull(message = "repository is never NULL")
    Repo get(@NotNull(message = "coordinates can't be NULL")
        Coordinates coords);

    /**
     * Remove repository by name.
     *
     * <p>Note: Deleting a repository requires admin access.
     * If OAuth is used, the delete_repo scope is required.
     *
     * @param coords Repository name in "user/repo" format
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/#delete-a-repository">Delete a Repository</a>
     */
    void remove(@NotNull(message = "coordinates can't be NULL")
        Coordinates coords) throws IOException;
}
