/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkStorage}.
 * @since 0.5
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.DoNotUseThreads")
final class MkStorageTest {

    @Test
    void readsAndWrites() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        storage.lock();
        try {
            storage.apply(
                new Directives().xpath("/github").add("test")
                    .set("hello, world")
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                storage.xml().xpath("/github/test/text()").get(0),
                Matchers.endsWith(", world")
            );
        } finally {
            storage.unlock();
        }
    }

    @Test
    void locksAndUnlocks() throws IOException, InterruptedException, ExecutionException {
        final MkStorage storage = new MkStorage.Synced(new MkStorage.InFile());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Runnable second = () -> storage.lock();
        storage.lock();
        Future<?> future = executor.submit(second);
        try {
            future.get(1L, TimeUnit.SECONDS);
            MatcherAssert.assertThat(
                "Timeout did not occur",
                false,
                Matchers.is(true)
            );
        } catch (final TimeoutException ex) {
            future.cancel(true);
        } finally {
            storage.unlock();
        }
        future = executor.submit(second);
        try {
            future.get(1L, TimeUnit.SECONDS);
        } catch (final TimeoutException ex) {
            MatcherAssert.assertThat(
                "Timeout occurred unexpectedly",
                false,
                Matchers.is(true)
            );
            future.cancel(true);
        }
        executor.shutdown();
    }

}
