/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.aspects.Tv;
import org.apache.commons.lang3.RandomStringUtils;
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
        final String name = RandomStringUtils.randomAlphabetic(Tv.FIVE);
        final StringBuilder builder = new StringBuilder();
        builder.append("refs/tags/").append(name);
        final Reference reference = refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
            .getString("sha")
        );
        MatcherAssert.assertThat(
            reference,
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("tags/").append(name);
        refs.remove(builder.toString());
    }

    /**
     * RtReference can iterate over references.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferences() throws Exception {
        final References refs = repo().git().references();
        final String name = RandomStringUtils.randomAlphabetic(Tv.SIX);
        final StringBuilder builder = new StringBuilder();
        builder.append("refs/heads/").append(name);
        refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
            .getString("sha")
        );
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("heads/").append(name);
        refs.remove(builder.toString());
    }

    /**
     * Returns the repo for test.
     * @return Repo
     */
    private static Repo repo() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final String keyrepo = System.getProperty("failsafe.github.repo");
        Assume.assumeThat(keyrepo, Matchers.notNullValue());
        return new RtGithub(key).repos().get(new Coordinates.Simple(keyrepo));
    }

}
