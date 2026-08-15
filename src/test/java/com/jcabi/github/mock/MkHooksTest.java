/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
        MatcherAssert.assertThat(
            "Collection is not empty",
            MkHooksTest.newHooks().iterate(),
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
        hooks.remove(MkHooksTest.hook(hooks).number());
        MatcherAssert.assertThat(
            "Collection is not empty",
            hooks.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkHooks can fetch a list with a single hook.
     * @throws Exception if something goes wrong.
     */
    @Test
    void canFetchListWithSingleHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        MkHooksTest.hook(hooks);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            hooks.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkHooks can fetch single hook.
     * @throws Exception if some problem inside
     */
    @Test
    void canFetchSingleHook() throws Exception {
        final Hooks hooks = MkHooksTest.newHooks();
        MatcherAssert.assertThat(
            "Value is null",
            hooks.get(MkHooksTest.hook(hooks).number()),
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
        MkHooksTest.hook(hooks);
        MkHooksTest.hook(hooks);
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
        MatcherAssert.assertThat(
            "Values are not equal",
            MkHooksTest.hook(hooks).number(),
            Matchers.equalTo(hooks.iterate().iterator().next().number())
        );
    }

    /**
     * Create hooks to work with.
     * @return Hooks
     * @throws IOException If some problem inside
     */
    private static Hooks newHooks() throws IOException {
        return new MkGitHub().randomRepo().hooks();
    }

    /**
     * Create a hook in the given collection.
     * @param hooks Collection to create the hook in
     * @return Created hook
     * @throws IOException If some problem inside
     */
    private static Hook hook(final Hooks hooks) throws IOException {
        return hooks.create(
            MkHooksTest.HOOK_TYPE,
            Collections.emptyMap(),
            Collections.emptyList(),
            true
        );
    }
}
