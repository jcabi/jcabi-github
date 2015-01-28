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
 * Github release assets.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
 */
@Immutable
public interface ReleaseAssets {

    /**
     * The release we're in.
     * @return Issue
     */
    @NotNull(message = "release is never NULL")
    Release release();

    /**
     * Iterate them all.
     * @return All comments
     * @see <a href="http://developer.github.com/v3/repos/releases/#list-assets-for-a-release">List Assets for a Release</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<ReleaseAsset> iterate();

    /**
     * Upload a release asset.
     * @param content The raw content bytes.
     * @param type Content-Type of the release asset.
     * @param name Name of the release asset.
     * @return The new release asset.
     * @throws IOException If an IO Exception occurs
     * @see <a href="http://developer.github.com/v3/repos/releases/#upload-a-release-asset">Upload a Release Asset</a>
     */
    @NotNull(message = "ReleaseAsset is never NULL")
    ReleaseAsset upload(
        @NotNull(message = "content is never NULL") byte[] content,
        @NotNull(message = "type is never NULL") String type,
        @NotNull(message = "name is never NULL") String name
    ) throws IOException;

    /**
     * Get a single release asset.
     * @param number The release asset ID.
     * @return The release asset.
     * @see <a href="http://developer.github.com/v3/repos/releases/#get-a-single-release-asset">Get a Single Release Asset</a>
     */
    @NotNull(message = "ReleaseAsset is never NULL")
    ReleaseAsset get(int number);

}
