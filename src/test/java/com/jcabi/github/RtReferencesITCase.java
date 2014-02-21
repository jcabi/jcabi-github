/**
 * Copyright (c) 2013-2014, JCabi.com
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
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtReferences}.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtReferencesITCase {

    /**
     * RtReference can create a reference.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void createsReference() throws Exception {
        final References refs = repo().git().references();
        final Reference reference = refs.create(
            "refs/tags/foo",
            "8004bea5f2ed15c729579297ce3d3d969e0e1a90"
        );
        MatcherAssert.assertThat(
            reference,
            Matchers.notNullValue()
        );
        refs.remove("tags/foo");
    }

    /**
     * RtReference can iterate over references.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferences() throws Exception {
        final References refs = repo().git().references();
        refs.create(
            "refs/heads/foo",
            "8004bea5f2ed15c729579297ce3d3d969e0e1a90"
        );
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.<Reference>iterableWithSize(2)
        );
        refs.remove("heads/foo");
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.<Reference>iterableWithSize(1)
        );
    }

    /**
     * RtReferences can return its repo.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        final References refs = repo().git().references();
        MatcherAssert.assertThat(
            refs.repo().toString(),
            Matchers.is("amihaiemil/forTest")
        );
    }

    /**
     * Returns the repo for test.
     * @return Repo
     */
    public static Repo repo() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final String keyrepo = System.getProperty("failsafe.github.repo");
        Assume.assumeThat(keyrepo, Matchers.notNullValue());
        return new RtGithub(key).repos().get(new Coordinates.Simple(keyrepo));
    }

}
