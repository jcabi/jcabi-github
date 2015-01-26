/**
 * Copyright (c) 2013-2015, jcabi.com
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
 * Github deploy keys.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 */
@Immutable
public interface DeployKeys {

    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of deploy keys
     * @see <a href="http://developer.github.com/v3/repos/keys/#list">List</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<DeployKey> iterate();

    /**
     * Get a single deploy key.
     * @param number Id of a deploy key
     * @return Deploy key
     * @see <a href="http://developer.github.com/v3/repos/keys/#get">Get a deploy key</a>
     */
    @NotNull(message = "deploy key is never NULL")
    DeployKey get(int number);

    /**
     * Create a deploy key.
     * @param title Title
     * @param key Key
     * @return A new deploy key
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/keys/#create">Add a new deploy key</a>
     */
    @NotNull(message = "deploy key is never NULL")
    DeployKey create(
        @NotNull(message = "title can't be NULL") String title,
        @NotNull(message = "key can't be NULL") String key
    ) throws IOException;

}
