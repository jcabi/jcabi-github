/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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

/**
 * Github hooks.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/hooks/">Hooks API</a>
 */
@Immutable
public interface Hooks {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of hooks
     * @see <a href="https://developer.github.com/v3/repos/hooks/#list">List</a>
     */
    Iterable<Hook> iterate();

    /**
     * Remove hook by ID.
     * @param number ID of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/hooks/#delete-a-hook">List</a>
     */
    void remove(int number) throws IOException;

    /**
     * Get specific hook by number.
     * @param number Hook number
     * @return Hook
     * @see <a href="https://developer.github.com/v3/repos/hooks/#get-single-hook">Get single hook</a>
     */
    Hook get(int number);

    /**
     * Create new hook.
     * @param name Hook name
     * @param config Configuration for the hook
     * @param events Events that trigger the hook
     * @param active Actually trigger the hook when the events occur?
     * @return Hook
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/hooks/#create-a-hook">Create a hook</a>
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    Hook create(
        String name, Map<String, String> config,
        Iterable<Event> events, boolean active
    ) throws IOException;
}
