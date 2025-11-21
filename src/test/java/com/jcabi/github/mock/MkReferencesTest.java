/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.References;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for {@link MkReferences}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MkReferencesTest {

    @Test
    void createsMkReference() throws IOException {
        final References refs = new MkGitHub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            "Value is null",
            refs.create("refs/heads/branch1", "abcderf122"),
            Matchers.notNullValue()
        );
    }

    @Test
    void returnsRepo() throws IOException {
        final References refs = new MkGitHub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            "Value is null",
            refs.repo(),
            Matchers.notNullValue()
        );
    }

    @Test
    void iteratesReferences() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/br", "qweqwe");
        refs.create("refs/tags/t1", "111t222");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkReferences can iterate over references in sub-namespace.
     */
    @Test
    void iteratesReferencesInSubNamespace() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/br", "qweqwe");
        refs.create("refs/tags/t1", "111t222");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate("heads"),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate("tags"),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkReferences can iterate over references in Tagsub-namespace.
     */
    @Test
    void iteratesTags() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/tags/t2", "2322f34");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.tags(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkReferences can iterate over references in Tagsub-namespace.
     */
    @Test
    void iteratesHeads() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/branch2", "blahblah");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.heads(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void removesReference() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/testbr", "qweqwe22");
        refs.create("refs/tags/t2", "111teee");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate(),
            Matchers.iterableWithSize(2)
        );
        refs.remove("refs/tags/t2");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate(),
            Matchers.iterableWithSize(1)
        );
    }
}
