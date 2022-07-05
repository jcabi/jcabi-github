/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.google.common.base.Predicate;
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
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
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
     * @throws Exception If some problem inside
     */
    @Test
    public void checkImmutability() throws Exception {
        MatcherAssert.assertThat(
            Iterables.filter(
                this.classpath.allTypes(),
                new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(final Class<?> input) {
                        return !ImmutabilityTest.skip().contains(
                            input.getName()
                        );
                    }
                }
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
