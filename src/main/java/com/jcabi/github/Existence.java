/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Check if a GitHub JSON object actually exists on the server.
 *
 * @since 0.38
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "readable")
final class Existence {

    /**
     * Checked object.
     */
    private final transient JsonReadable readable;

    /**
     * Ctor.
     * @param rdbl Checked object.
     */
    Existence(final JsonReadable rdbl) {
        this.readable = rdbl;
    }

    /**
     * Check existence.
     * @return True if exists, false otherwise.
     * @throws IOException If there is a networking problem.
     */
    public boolean check() throws IOException {
        boolean exists = true;
        try {
            this.readable.json();
        } catch (final AssertionError ex) {
            exists = false;
        } catch (final IndexOutOfBoundsException ex) {
            exists = false;
        }
        return exists;
    }

}
