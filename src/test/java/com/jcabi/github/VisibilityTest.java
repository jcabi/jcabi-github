/**
 * Copyright (c) 2013-2017, jcabi.com
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
 * @author Carlos Crespo (carlos.a.crespo@gmail.com)
 * @version $Id$
 */
public final class VisibilityTest {

    /**
     * Set of classes/interfaces that can be public.
     */
    private static final Set<String> SKIP = ImmutableSet.<String>builder()
        .add("com.jcabi.github.RtGithub")
        .add("com.jcabi.github.Bulk")
        .add("com.jcabi.github.Smarts")
        .add("com.jcabi.github.wire.CarefulWire")
        .add("com.jcabi.github.mock.MkGithub")
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
     * @throws Exception If some problem inside
     */
    @Test
    public void checkVisibility() throws Exception {
        MatcherAssert.assertThat(
            Iterables.filter(
                this.classpath.allTypes(),
                new Predicate<Class<?>>() {
                    @Override
                    public boolean apply(final Class<?> input) {
                        return !(
                            input.isInterface()
                                || SKIP.contains(input.getName())
                                || (input.getEnclosingClass() != null
                                    && input.getName().endsWith("Smart"))
                            );
                    }
                }
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
