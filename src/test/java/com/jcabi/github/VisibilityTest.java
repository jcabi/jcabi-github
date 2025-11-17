/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.lang.reflect.Modifier;
import java.util.Set;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for visibility.
 * Checks that there are not public classes in package
 * {@code com.jcabi.github}. Certain types including all Smart types are
 * excluded.
 *
 */
public final class VisibilityTest {

    /**
     * Set of classes/interfaces that can be public.
     */
    private static final Set<String> SKIP = ImmutableSet.<String>builder()
        .add("com.jcabi.github.RtGitHub")
        .add("com.jcabi.github.Bulk")
        .add("com.jcabi.github.Smarts")
        .add("com.jcabi.github.wire.CarefulWire")
        .add("com.jcabi.github.mock.MkGitHub")
        .build();

    /**
     * ClasspathRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient ClasspathRule classpath = new ClasspathRule();

    /**
     * Test for visibility.
     * Checks that there are not public classes in package
     * {@code com.jcabi.github}.
     *
     */
    @Test
    public void checkVisibility() {
        MatcherAssert.assertThat(
            "String does not end with expected value",
            Iterables.filter(
                this.classpath.allTypes(),
                input -> !(
                    input.isInterface()
                        || VisibilityTest.SKIP.contains(input.getName())
                        || input.getEnclosingClass() != null
                            && input.getName().endsWith("Smart")
                    )
            ),
            Matchers.everyItem(
                new CustomTypeSafeMatcher<Class<?>>("not public type") {
                    @Override
                    protected boolean matchesSafely(final Class<?> item) {
                        return !Modifier.isPublic(item.getModifiers());
                    }
                }
            )
        );
    }

}
