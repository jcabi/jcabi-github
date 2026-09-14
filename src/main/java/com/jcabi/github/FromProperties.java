/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Properties;

/**
 * User agent data read from the jcabigithub.properties file.
 *
 * @since 0.37
 */
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
public final class FromProperties implements UserAgent {

    /**
     * Properties.
     */
    private final transient Properties props = new Properties();

    /**
     * Name of the properties file to load.
     */
    private final transient String name;

    /**
     * Ctor.
     *
     * @param filename Name of the properties file to look for
     */
    public FromProperties(final String filename) {
        this.name = filename;
    }

    @Override
    public String format() {
        try {
            this.props.load(
                Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(this.name)
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("IOException when loading %s", this.name),
                ex
            );
        }
        return String.format(
            "jcabi-github %s %s %s",
            this.props.getProperty("JCabi-Version"),
            this.props.getProperty("JCabi-Build"),
            this.props.getProperty("JCabi-Date")
        );
    }
}
