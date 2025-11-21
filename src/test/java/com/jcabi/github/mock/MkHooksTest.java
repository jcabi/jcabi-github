/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkHooks}.
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
            "Collection is not empty",
            hooks.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkHooks can delete a single hook by ID.
     * @throws Exception if something goes wrong.
     */
    @Test
    void canDeleteSingleHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        final Hook hook = hooks.create(
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            hooks.iterate(),
            Matchers.iterableWithSize(1)
        );
        hooks.remove(hook.number());
        MatcherAssert.assertThat(
            "Collection is not empty",
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
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
        MatcherAssert.assertThat(
            "Value is null",
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
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
        hooks.create(
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            hooks.iterate(),
            Matchers.iterableWithSize(2)
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
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            hooks.iterate().iterator().next().number(),
            Matchers.equalTo(hook.number())
        );
    }

    /**
     * Create hooks to work with.
     * @return Hooks
     */
    private static Hooks newHooks() throws IOException {
        return new MkGitHub().randomRepo().hooks();
    }
}
