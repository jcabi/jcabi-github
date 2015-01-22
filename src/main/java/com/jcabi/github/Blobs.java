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
 * Github Git Data Blobs.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/git/blobs/">Blobs API</a>
 */
@Immutable
public interface Blobs {

    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get specific blob by sha.
     * @param sha SHA of a blob
     * @return Blob
     * @see <a href="http://developer.github.com/v3/git/blobs/#get-a-blob">Get single blob</a>
     */
    @NotNull(message = "Blob is never NULL")
    Blob get(
        @NotNull(message = "Sha is never null") String sha
    );

    /**
     * Create a blob.
     * @param content Content
     * @param encoding Encoding
     * @return A new blob
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/git/blobs/#create-a-blob">Create a Blob</a>
     */
    @NotNull(message = "Blob is never NULL")
    Blob create(
        @NotNull(message = "Content is never null") String content,
        @NotNull(message = "Encoding is never null") String encoding
    ) throws IOException;

}
