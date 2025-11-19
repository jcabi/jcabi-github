/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.collect.Iterables;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Utility class which provides convenient methods for annotations check etc.
 * @since 0.13
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class ClasspathRule implements TestRule {

    /**
     * Provides all classes in package 'com.jcabi.github'.
     * @return Classes
     * @checkstyle NonStaticMethodCheck (5 lines)
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Iterable<Class<?>> allTypes() {
        return Iterables.filter(
            new Reflections(
                new ConfigurationBuilder()
                    .setScanners(
                        new SubTypesScanner(false),
                        new ResourcesScanner()
                )
                    .setUrls(
                        ClasspathHelper.forClassLoader(
                            ClasspathHelper.contextClassLoader(),
                            ClasspathHelper.staticClassLoader()
                        )
                    ).filterInputsBy(
                        new FilterBuilder().include(
                            FilterBuilder.prefix("com.jcabi.github")
                        )
                    )
            ).getSubTypesOf(Object.class),
            input -> {
                final String name = input.getName();
                // @checkstyle BooleanExpressionComplexityCheck (6 lines)
                return !name.endsWith("Test")
                    && !name.endsWith("ITCase")
                    && !name.endsWith("ClasspathRule")
                    && !name.endsWith("RepoRule")
                    && (input.getEnclosingClass() == null
                    || name.endsWith("Smart"));
            }
        );
    }

    @Override
    public Statement apply(final Statement statement,
        final Description description) {
        return new Statement() {
            @Override
            // @checkstyle IllegalThrowsCheck (1 line)
            public void evaluate() throws Throwable {
                statement.evaluate();
            }
        };
    }

    /**
     * Provides all public methods from classes in package 'com.jcabi.github'.
     * @return Methods
     */
    public Iterable<Method> allPublicMethods() {
        return Iterables.concat(
            Iterables.transform(
                ClasspathRule.allTypes(),
                input -> Iterables.filter(
                    Arrays.asList(input.getDeclaredMethods()),
                    method -> Modifier.isPublic(
                        method.getModifiers()
                    )
                )
            )
        );
    }
}
