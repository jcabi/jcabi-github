/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Event;
import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkHooks}.
 * @since 0.8
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkHooksTest {
    /**
     * Type of hook to create and use for tests.
     */
    private static final String HOOK_TYPE = "web";

    /**
     * MkHooks can fetch empty list of hooks.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfHooks() throws Exception {
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
    public void canDeleteSingleHook() throws Exception {
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
    public void canFetchSingleHook() throws Exception {
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
    public void canFetchNonEmptyListOfHooks() throws Exception {
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
    public void canCreateHook() throws Exception {
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
