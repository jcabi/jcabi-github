/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.RetryOnFailure;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * Test resource for skipping tests when random port is busy.
 * @since 0.8
 */
public class RandomPort implements InvocationInterceptor {
    @Override
    public final void interceptTestMethod(
        final Invocation<Void> invocation,
        final ReflectiveInvocationContext<java.lang.reflect.Method> context,
        final ExtensionContext extension
    ) throws Throwable {
        try {
            invocation.proceed();
        } catch (final BindException ignored) {
            Logger.warn(
                this,
                String.format(
                    "Test %s skipped due to no available ports",
                    extension.getDisplayName()
                )
            );
            Assumptions.assumeTrue(false);
        }
    }

    /**
     * Returns available port number.
     * @return Available port number
     * @throws IOException in case of IO error.
     * @checkstyle NonStaticMethodCheck (5 lines)
     */
    @RetryOnFailure
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static int port() throws IOException {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(true);
            socket.bind(
                new InetSocketAddress("localhost", 0)
            );
            return socket.getLocalPort();
        }
    }
}
