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
 * Github gists.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/gists/">Gists API</a>
 * @todo #1:1hr New method remove() to delete a gist. Let's add a new
 *  method to remove a gist by name, as explained in
 *  http://developer.github.com/v3/gists/#delete-a-gist. The method
 *  should be tested by unit and integration tests.
 */
@Immutable
public interface Gists {

    /**
     * Github we're in.
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Create a new gist.
     *
     * @param files Names and content of files
     * @return Gist
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/#create-a-gist">Create a Gist</a>
     */
    @NotNull(message = "gist is never NULL")
    Gist create(@NotNull(message = "list of files can't be NULL")
        Map<String, String> files) throws IOException;

    /**
     * Get gist by name.
     * @param name Name of it
     * @return Gist
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    @NotNull(message = "gist is never NULL")
    Gist get(@NotNull(message = "name can't be NULL") String name);

    /**
     * Iterate all gists.
     * @return Iterator of gists
     * @see <a href="http://developer.github.com/v3/gists/#list-gists">List Gists</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Gist> iterate();

}
