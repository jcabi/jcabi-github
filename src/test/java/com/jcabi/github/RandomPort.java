/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.RetryOnFailure;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import org.junit.Assume;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Test resource for skipping tests when random port is busy.
 * @since 0.8
 */
public class RandomPort extends ExternalResource {
    @Override
    public final Statement apply(
        final Statement base, final Description description
    ) {
        return new Statement() {
            @Override
            // @checkstyle IllegalThrowsCheck (1 line)
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (final BindException ignored) {
                    Logger.warn(
                        base,
                        String.format(
                            "Test %s skipped due to no available ports",
                            description
                        )
                    );
                    Assume.assumeTrue(false);
                }
            }
        };
    }

    /**
     * Returns available port number.
     * @return Available port number
     * @throws IOException in case of IO error.
     */
    @RetryOnFailure
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public int port() throws IOException {
        final ServerSocket socket = new ServerSocket();
        try {
            socket.setReuseAddress(true);
            socket.bind(
                new InetSocketAddress("localhost", 0)
            );
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }
}
