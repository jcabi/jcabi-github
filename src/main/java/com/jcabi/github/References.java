/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github Git Data References.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/git/references/">References API</a>
 */
@Immutable
public interface References {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Creates a reference.
     * @param ref The name of the fully qualified reference (ie: refs/heads/master).
     * @param sha The SHA1 value to set this reference to.
     * @return Reference - The newly created Reference
     * @throws IOException - If there are any errors.
     */
    Reference create(
        String ref,
        String sha
    ) throws IOException;

    /**
     * Get Reference by identifier.
     * @param identifier Reference's name.
     * @return Reference The reference with the given name
     */
    Reference get(
        String identifier
    );

    /**
     * Iterates all references.
     * @return Iterator of references.
     */
    Iterable<Reference> iterate();

    /**
     * Iterates references in sub-namespace.
     * @param subnamespace Sub-namespace
     * @return Iterator of references.
     */
    Iterable<Reference> iterate(
        String subnamespace
    );

    /**
     * Iterate references under "tags" sub-namespace.
     * @return Iterator of references.
     */
    Iterable<Reference> tags();

    /**
     * Iterate references under "heads" sub-namespace.
     * @return Iterator of references.
     */
    Iterable<Reference> heads();

    /**
     * Removes a reference by its identifier.
     * @param identifier Reference's identifier.
     * @throws IOException If there is any I/O problem.
     */
    void remove(
        String identifier
    ) throws IOException;
}
