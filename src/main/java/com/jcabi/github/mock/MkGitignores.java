/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Gitignores;
import java.util.Collections;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Gitignore.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "ghub")
@SuppressWarnings("PMD.UseConcurrentHashMap")
final class MkGitignores implements Gitignores {

    /**
     * The gitignore templates.
     */
    private static final Map<String, String> GITIGNORES =
        Collections.singletonMap(
            "Java",
            "*.class\n\n# Package Files #\n*.jar\n*.war\n*.ear\n"
        );

    /**
     * GitHub.
     */
    private final transient MkGitHub ghub;

    /**
     * Public ctor.
     * @param github The github
     */
    MkGitignores(final MkGitHub github) {
        this.ghub = github;
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public Iterable<String> iterate() {
        return MkGitignores.GITIGNORES.keySet();
    }

    @Override
    public String template(
        final String name) {
        final String template = MkGitignores.GITIGNORES.get(name);
        if (template == null) {
            throw new IllegalArgumentException("Template not found.");
        }
        return template;
    }
}
