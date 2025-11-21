/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Markdown;
import jakarta.json.JsonObject;
import lombok.ToString;

/**
 * Mock markdown API.
 *
 * @since  0.10
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
public final class MkMarkdown implements Markdown {
    /**
     * Owner github.
     */
    private final transient GitHub owner;

    /**
     * Creates new instance.
     * @param github Owner github
     */
    public MkMarkdown(final GitHub github) {
        this.owner = github;
    }

    @Override
    public GitHub github() {
        return this.owner;
    }

    @Override
    public String render(
        final JsonObject json
    ) {
        return json.getString("text");
    }

    @Override
    public String raw(
        final String text
    ) {
        return text;
    }
}
