/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.json.JsonObject;

/**
 * Markdown API.
 *
 * @since 0.6
 * @see <a href="https://developer.github.com/v3/markdown/">Markdown API</a>
 */
@Immutable
public interface Markdown {

    /**
     * Get its owner.
     * @return Github
     */
    Github github();

    /**
     * Render.
     * @param json JSON parameters
     * @return HTML
     * @throws IOException If it fails due to I/O problem
     */
    String render(JsonObject json)
        throws IOException;

    /**
     * Raw rendering.
     * @param text Text in Markdown format
     * @return HTML
     * @throws IOException If it fails due to I/O problem
     */
    String raw(String text)
        throws IOException;

}
