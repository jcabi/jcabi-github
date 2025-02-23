/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
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
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void createsMkReference() throws Exception {
        final References refs = new MkGithub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            refs.create("refs/heads/branch1", "abcderf122"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReference can return its owner.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        final References refs = new MkGithub().randomRepo()
            .git().references();
        MatcherAssert.assertThat(
            refs.repo(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReferences can iterate over references.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferences() throws Exception {
        final Repo owner = new MkGithub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/br", "qweqwe");
        refs.create("refs/tags/t1", "111t222");
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.<Reference>iterableWithSize(2)
        );
    }

    /**
     * MkReferences can iterate over references in sub-namespace.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferencesInSubNamespace() throws Exception {
        final Repo owner = new MkGithub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/br", "qweqwe");
        refs.create("refs/tags/t1", "111t222");
        MatcherAssert.assertThat(
            refs.iterate("heads"),
            Matchers.<Reference>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            refs.iterate("tags"),
            Matchers.<Reference>iterableWithSize(1)
        );
    }

    /**
     * MkReferences can iterate over references in Tagsub-namespace.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesTags() throws Exception {
        final Repo owner = new MkGithub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/tags/t2", "2322f34");
        MatcherAssert.assertThat(
            refs.tags(),
            Matchers.<Reference>iterableWithSize(1)
        );
    }

    /**
     * MkReferences can iterate over references in Tagsub-namespace.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesHeads() throws Exception {
        final Repo owner = new MkGithub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/branch2", "blahblah");
        MatcherAssert.assertThat(
            refs.heads(),
            Matchers.<Reference>iterableWithSize(1)
        );
    }

    /**
     * MkReferences can remove a Reference.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void removesReference() throws Exception {
        final Repo owner = new MkGithub().randomRepo();
        final References refs = owner.git().references();
        refs.create("refs/heads/testbr", "qweqwe22");
        refs.create("refs/tags/t2", "111teee");
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.<Reference>iterableWithSize(2)
        );
        refs.remove("refs/tags/t2");
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.<Reference>iterableWithSize(1)
        );
    }
}
