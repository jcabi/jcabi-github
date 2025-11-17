/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;

/**
 * GitHub repository language.
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "txt", "length" })
public final class RtLanguage implements Language {

    /**
     * Language name like C or Java.
     */
    private final transient String txt;

    /**
     * Number of bytes written in the language in project.
     */
    private final transient long length;

    /**
     * Public ctor.
     * @param lang Language name
     * @param size Language bytes
     */
    public RtLanguage(final String lang, final long size) {
        this.txt = lang;
        this.length = size;
    }

    @Override
    public String name() {
        return this.txt;
    }

    @Override
    public long bytes() {
        return this.length;
    }
}
