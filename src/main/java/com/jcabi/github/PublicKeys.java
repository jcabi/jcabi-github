/**
 * Copyright (c) 2013-2018, jcabi.com
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

/**
 * Github public keys.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/users/keys/">Public Keys API</a>
 */
@Immutable
public interface PublicKeys {

    /**
     * User we're in.
     *
     * @return User
     */
    User user();

    /**
     * Iterate all public keys.
     *
     * @return All public keys.
     * @see <a href="http://developer.github.com/v3/users/keys#list-your-public-keys/">List your public keys.</a>
     */
    Iterable<PublicKey> iterate();

    /**
     * Get a single public key.
     *
     * @param number ID of the public key to remove.
     * @return A single public key.
     * @see <a href="http://developer.github.com/v3/users/keys#get-a-single-public-key/">Get a single public key.</a>
     */
    PublicKey get(int number);

    /**
     * Create a public key.
     *
     * @param title The title of this key.
     * @param key The value of this key.
     * @return A new PublicKey
     * @throws IOException If an IO problem occurs.
     * @see <a href="http://developer.github.com/v3/users/keys/#create-a-public-key/">Create a public key.</a>
     */
    PublicKey create(
        String title,
        String key
    ) throws IOException;

    /**
     * Remove a public key.
     *
     * @param number ID of the public key to remove.
     * @throws IOException If an IO problem occurs.
     * @see <a href="http://developer.github.com/v3/users/keys/#delete-a-public-key/">Delete a public key.</a>
     */
    void remove(int number) throws IOException;

}
