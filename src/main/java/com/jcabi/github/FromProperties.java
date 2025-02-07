/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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

import java.io.IOException;
import java.util.Properties;

/**
 * User agent data read from the jcabigithub.properties file.
 * @since 0.37
 */
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
