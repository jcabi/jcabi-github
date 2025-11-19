/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub public keys.
 * @see <a href="https://developer.github.com/v3/users/keys/">Public Keys API</a>
 */
@Immutable
public interface PublicKeys {

    /**
     * User we're in.
     * @return User
     */
    User user();

    /**
     * Iterate all public keys.
     * @return All public keys.
     * @see <a href="https://developer.github.com/v3/users/keys#list-your-public-keys/">List your public keys.</a>
     */
    Iterable<PublicKey> iterate();

    /**
     * Get a single public key.
     * @param number ID of the public key to remove.
     * @return A single public key.
     * @see <a href="https://developer.github.com/v3/users/keys#get-a-single-public-key/">Get a single public key.</a>
     */
    PublicKey get(int number);

    /**
     * Create a public key.
     * @param title The title of this key.
     * @param key The value of this key.
     * @return A new PublicKey
     * @throws IOException If an IO problem occurs.
     * @see <a href="https://developer.github.com/v3/users/keys/#create-a-public-key/">Create a public key.</a>
     */
    PublicKey create(
        String title,
        String key
    ) throws IOException;

    /**
     * Remove a public key.
     * @param number ID of the public key to remove.
     * @throws IOException If an IO problem occurs.
     * @see <a href="https://developer.github.com/v3/users/keys/#delete-a-public-key/">Delete a public key.</a>
     */
    void remove(int number) throws IOException;

}
