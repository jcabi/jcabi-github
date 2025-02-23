/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.json.JsonObject;

/**
 * JSON readable.
 *
 * @since 0.4
 */
@Immutable
public interface JsonReadable {

    /**
     * Describe it in a JSON object.
     * @return JSON object
     * @throws IOException If there is any I/O problem
     */
    JsonObject json() throws IOException;

}
