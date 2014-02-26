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

import java.util.Random;
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
        final String name = randomName(5);
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
        final String name = randomName(6);
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
     * RtReferences can return its repo.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        final References refs = repo().git().references();
        MatcherAssert.assertThat(
            refs.repo(),
            Matchers.notNullValue()
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

    /**
     * Generate a random name for every Reference.
     * @param length The length of the name.
     * @return String
     */
    public static String randomName(final int length) {
        final Random rnd = new Random();
        final String characters = "abcdefghijklmnopqrstuvwxyz";
        final char[] name = new char[length];
        for (int index = 0; index < length; index = index + 1) {
            name[index] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(name);
    }

}
