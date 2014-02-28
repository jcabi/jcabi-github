/**
 * Copyright (c) 2013-2014, JCabi.com
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for nullability.
 * Checks that all public methods in clases in package {@code com.jcabi.github }
 * have {@code @NotNull} annotation for return value and for input arguments
 * (if they are not scalar).
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
public final class NullabilityTest {

    /**
     * ClasspathRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient ClasspathRule classpath = new ClasspathRule();

    /**
     * Test for nullability.
     * Checks that all public methods in clases in package
     * {@code com.jcabi.github }have {@code @NotNull} annotation for return
     * value and for input arguments(if they are not scalar).
     *
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void checkNullability() throws Exception {
        MatcherAssert.assertThat(
            this.classpath.allPublicMethods(),
            Matchers.everyItem(
                // @checkstyle LineLength (1 line)
                new CustomTypeSafeMatcher<Method>("parameter and return value is annotated with @NonNull") {
                    @Override
                    protected boolean matchesSafely(final Method item) {
                        return item.getReturnType().isPrimitive()
                            || item.isAnnotationPresent(NotNull.class)
                            && allParamsAnnotated(item, NotNull.class);
                    }
                }
            )
        );
    }

    /**
     * Checks if all params in method have given annotation.
     * @param method Method to be checked
     * @param annotation Annotation to be checked for
     * @return True if all parameters of method have given annotation
     */
    private boolean allParamsAnnotated(
        final Method method, final Class<?> annotation) {
        final Class<?> [] types = method.getParameterTypes();
        final Function<Annotation, Class<? extends Annotation>> filter =
            new Transformer();
        boolean allAnnotated = true;
        for (int index = 0; index < types.length; index += 1) {
            if (!types[index].isPrimitive()
                && !Collections2.transform(
                    Arrays.asList(method.getParameterAnnotations()[index]),
                    filter
                ).contains(annotation)) {
                allAnnotated =  false;
            }
        }
        return allAnnotated;
    }

    private class Transformer implements
        Function<Annotation, Class<? extends Annotation>> {
        @Override
        public Class<? extends Annotation> apply(final Annotation input) {
            return input.annotationType();
        }
    }
}
