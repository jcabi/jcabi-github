/**
 * Copyright (c) 2013-2020, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.github.Event;
import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkHooks}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MkHooksTest {
    /**
     * Type of hook to create and use for tests.
     */
    private static final String HOOK_TYPE = "web";

    /**
     * MkHooks can fetch empty list of hooks.
     * @throws Exception if some problem inside
     */
    @Test
    void canFetchEmptyListOfHooks() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        MatcherAssert.assertThat(
            hooks.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkHooks can delete a single hook by ID.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void canDeleteSingleHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        final Hook hook = hooks.create(
            HOOK_TYPE,
            Collections.<String, String>emptyMap(),
            Collections.<Event>emptyList(),
            true
        );
        MatcherAssert.assertThat(
            hooks.iterate(),
            Matchers.<Hook>iterableWithSize(1)
        );
        hooks.remove(hook.number());
        MatcherAssert.assertThat(
            hooks.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkHooks can fetch single hook.
     * @throws Exception if some problem inside
     */
    @Test
    void canFetchSingleHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        final Hook hook = hooks.create(
            HOOK_TYPE,
            Collections.<String, String>emptyMap(),
            Collections.<Event>emptyList(),
            true
        );
        MatcherAssert.assertThat(
            hooks.get(hook.number()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkHooks can fetch non empty list of hooks.
     * @throws Exception If some problem inside
     */
    @Test
    void canFetchNonEmptyListOfHooks() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        hooks.create(
            HOOK_TYPE,
            Collections.<String, String>emptyMap(),
            Collections.<Event>emptyList(),
            true
        );
        hooks.create(
            HOOK_TYPE,
            Collections.<String, String>emptyMap(),
            Collections.<Event>emptyList(),
            true
        );
        MatcherAssert.assertThat(
            hooks.iterate(),
            Matchers.<Hook>iterableWithSize(2)
        );
    }

    /**
     * MkHooks can create a hook.
     * @throws Exception If some problem inside
     */
    @Test
    void canCreateHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        final Hook hook = hooks.create(
            HOOK_TYPE,
            Collections.<String, String>emptyMap(),
            Collections.<Event>emptyList(),
            true
        );
        MatcherAssert.assertThat(
            hooks.iterate().iterator().next().number(),
            Matchers.equalTo(hook.number())
        );
    }

    /**
     * Create hooks to work with.
     * @return Hooks
     * @throws Exception If some problem inside
     */
    private static Hooks newHooks() throws Exception {
        return new MkGithub().randomRepo().hooks();
    }
}
