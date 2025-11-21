/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Properties;

/**
 * User agent data read from the jcabigithub.properties file.
 * @since 0.37
 */
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
public final class FromProperties implements UserAgent {

    /**
     * Build timestamp.
     */
    private static final String JCABI_DATE = "JCabi-Date";

    /**
     * Project version.
     */
    private static final String JCABI_VERSION = "JCabi-Version";

    /**
     * Build number.
     */
    private static final String JCABI_BUILD = "JCabi-Build";

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
            this.props.getProperty(FromProperties.JCABI_VERSION),
            this.props.getProperty(FromProperties.JCABI_BUILD),
            this.props.getProperty(FromProperties.JCABI_DATE)
        );
    }
}
