/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for {@link MkReferences}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkReferencesTest {

    /**
     * MkReferences can create a MkReference.
     */
    @Test
    public void createsMkReference() throws IOException {
        final References refs = new MkGitHub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            "Value is null",
            refs.create("refs/heads/branch1", "abcderf122"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReference can return its owner.
     */
    @Test
    public void returnsRepo() throws IOException {
        final References refs = new MkGitHub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            "Value is null",
            refs.repo(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReferences can iterate over references.
     */
    @Test
    public void iteratesReferences() throws IOException {
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
    public void iteratesReferencesInSubNamespace() throws IOException {
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
    public void iteratesTags() throws IOException {
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
    public void iteratesHeads() throws IOException {
        final Repo owner = new MkGitHub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/branch2", "blahblah");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            refs.heads(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkReferences can remove a Reference.
     */
    @Test
    public void removesReference() throws IOException {
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
