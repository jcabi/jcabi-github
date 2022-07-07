/**
 * Copyright (c) 2013-2022, jcabi.com
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
 * Github gists.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/gists/">Gists API</a>
 */
@Immutable
public interface Gists {

    /**
     * Github we're in.
     * @return Github
     */
    Github github();

    /**
     * Create a new gist.
     *
     * @param files Names and content of files
     * @param visible Indicates whether the gist is public
     * @return Gist
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/#create-a-gist">Create a Gist</a>
     */
    Gist create(
        Map<String, String> files, boolean visible
    ) throws IOException;

    /**
     * Get gist by name.
     * @param name Name of it
     * @return Gist
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    Gist get(String name);

    /**
     * Iterate all gists.
     * @return Iterator of gists
     * @see <a href="http://developer.github.com/v3/gists/#list-gists">List Gists</a>
     */
    Iterable<Gist> iterate();

    /**
     * Removes a gist by id.
     *
     *
     * @param identifier Identifier of the gist to be removed.
     * @throws IOException If there is any I/O problem
     */
    void remove(String identifier) throws IOException;
}
