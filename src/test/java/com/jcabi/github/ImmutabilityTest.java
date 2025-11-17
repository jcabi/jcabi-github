/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import java.util.Set;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for immutability.
 * Checks that all classes in package {@code com.jcabi.github }
 * have {@code @Immutable} annotation.
 *
 */
public final class ImmutabilityTest {

    /**
     * ClasspathRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient ClasspathRule classpath = new ClasspathRule();

    /**
     * Test for immutability.
     * Checks that all classes in package {@code com.jcabi.github }
     * have {@code @Immutable} annotation.
     *
     */
    @Test
    public void checkImmutability() {
        MatcherAssert.assertThat(
            Iterables.filter(
                this.classpath.allTypes(),
                input -> !ImmutabilityTest.skip().contains(
                    input.getName()
                )
            ),
            Matchers.everyItem(
                new CustomTypeSafeMatcher<Class<?>>("annotated type") {
                    @Override
                    protected boolean matchesSafely(final Class<?> item) {
                        return item.isAnnotationPresent(Immutable.class);
                    }
                }
            )
        );
    }

    /**
     * Get set of class names to be skipped.
     * @return Set
     */
    private static Set<String> skip() {
        return ImmutableSet.<String>builder()
            .add("com.jcabi.github.mock.JsonNode")
            .add("com.jcabi.github.Bulk")
            .add("com.jcabi.github.Smarts")
            .build();
    }
}
