/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
 */
final class MkReferencesTest {

    @Test
    void createsMkReference() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo()
                .git().references().create("refs/heads/branch1", "abcderf122"),
            Matchers.notNullValue()
        );
    }

    @Test
    void returnsRepo() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo()
                .git().references().repo(),
            Matchers.notNullValue()
        );
    }

    @Test
    void iteratesReferences() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkReferencesTest.filled().iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkReferences can iterate over references in sub-namespace.
     */
    @Test
    void iteratesReferencesInHeadsNamespace() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkReferencesTest.filled().iterate("heads"),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkReferences can iterate over references in tags sub-namespace.
     * @throws IOException If some problem inside
     */
    @Test
    void iteratesReferencesInTagsNamespace() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkReferencesTest.filled().iterate("tags"),
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
        final References refs = MkReferencesTest.filled();
        refs.remove("refs/tags/t1");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    private static References filled() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/br", "qweqwe");
        refs.create("refs/tags/t1", "111t222");
        return refs;
    }
}
