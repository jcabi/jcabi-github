/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkStorage}.
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public final class MkStorageTest {

    /**
     * MkStorage can text and write.
     * @throws Exception If some problem inside
     */
    @Test
    public void readsAndWrites() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        storage.lock();
        try {
            storage.apply(
                new Directives().xpath("/github").add("test")
                    .set("hello, world")
            );
            MatcherAssert.assertThat(
                storage.xml().xpath("/github/test/text()").get(0),
                Matchers.endsWith(", world")
            );
        } finally {
            storage.unlock();
        }
    }

    /**
     * MkStorage can lock and unlock files.
     * @throws Exception If some problem inside
     */
    @Test
    public void locksAndUnlocks() throws Exception {
        final MkStorage storage = new MkStorage.Synced(new MkStorage.InFile());
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Runnable second = () -> storage.lock();
        storage.lock();
        Future<?> future = executor.submit(second);
        try {
            future.get(1L, TimeUnit.SECONDS);
            MatcherAssert.assertThat("timeout SHOULD happen", false);
        } catch (final TimeoutException ex) {
            future.cancel(true);
        } finally {
            storage.unlock();
        }
        future = executor.submit(second);
        try {
            future.get(1L, TimeUnit.SECONDS);
        } catch (final TimeoutException ex) {
            MatcherAssert.assertThat("timeout SHOULD NOT happen", false);
            future.cancel(true);
        }
        executor.shutdown();
    }

}
