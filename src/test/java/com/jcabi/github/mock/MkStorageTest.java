/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xembly.Directives;

/**
 * Test case for {@link MkStorage}.
 *
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
    void readsPredefinedContent() throws IOException {
        MatcherAssert.assertThat(
            "Predefined content is not in the storage",
            new MkStorage.InFile(
                new XMLDocument("<github><test>predefined</test></github>")
            ).xml().xpath("/github/test/text()"),
            Matchers.contains("predefined")
        );
    }

    @Test
    void writesOverPredefinedContent() throws IOException {
        final MkStorage storage = new MkStorage.InFile(
            new XMLDocument("<github><test>first</test></github>")
        );
        storage.apply(new Directives().xpath("/github").add("test").set("second"));
        MatcherAssert.assertThat(
            "Change is not applied to predefined content",
            storage.xml().xpath("/github/test/text()"),
            Matchers.contains("first", "second")
        );
    }

    @Test
    void keepsPredefinedFileIntact(@TempDir final Path temp)
        throws IOException {
        final Path file = temp.resolve("github.xml");
        final String content = "<github><test>intact</test></github>";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        new MkStorage.InFile(new XMLDocument(file.toFile())).apply(
            new Directives().xpath("/github/test").set("changed")
        );
        MatcherAssert.assertThat(
            "Predefined file is modified by the storage",
            new String(Files.readAllBytes(file), StandardCharsets.UTF_8),
            Matchers.equalTo(content)
        );
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
