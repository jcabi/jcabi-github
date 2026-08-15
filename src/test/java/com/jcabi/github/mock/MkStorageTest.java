/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
 */
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
    @SuppressWarnings("PMD.CloseResource")
    void locks() throws IOException, InterruptedException, ExecutionException {
        final MkStorage storage = new MkStorage.Synced(new MkStorage.InFile());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        storage.lock();
        final Future<?> future = executor.submit(() -> storage.lock());
        boolean blocked = false;
        try {
            future.get(1L, TimeUnit.SECONDS);
        } catch (final TimeoutException ex) {
            blocked = true;
        } finally {
            future.cancel(true);
            storage.unlock();
            executor.shutdown();
        }
        MatcherAssert.assertThat(
            "Second lock is not blocked",
            blocked,
            Matchers.is(true)
        );
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void unlocks()
        throws IOException, InterruptedException, ExecutionException {
        final MkStorage storage = new MkStorage.Synced(new MkStorage.InFile());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        storage.lock();
        storage.unlock();
        final Future<?> future = executor.submit(() -> storage.lock());
        boolean acquired = true;
        try {
            future.get(1L, TimeUnit.SECONDS);
        } catch (final TimeoutException ex) {
            acquired = false;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
        MatcherAssert.assertThat(
            "Lock is not released",
            acquired,
            Matchers.is(true)
        );
    }
}
