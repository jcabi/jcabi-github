/**
 * Copyright (c) 2013-2024, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
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
