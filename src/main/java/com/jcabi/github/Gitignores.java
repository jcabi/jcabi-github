/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub Gitignore.
 * <p>Defines storage of .gitignore templates
 *
 * @see <a href="https://developer.github.com/v3/gitignore/#gitignore">Gitignore</a>
 * @since 0.8
 */
@Immutable
public interface Gitignores {
    /**
     * Get its owner.
     * @return GitHub
     */
    GitHub github();

    /**
     * Iterate them all.
     * @return Iterator of Gitignote template names
     * @throws IOException If it fails due to I/O problem
     * @see <a href="https://developer.github.com/v3/gitignore/#listing-available-templates">Listing available templates</a>
     */
    Iterable<String> iterate() throws IOException;

    /**
     * Gets raw gitignore template.
     * @param name Name of the template
     * @return Raw template
     * @throws IOException If it fails due to I/O problem
     */
    String template(String name) throws IOException;
}
