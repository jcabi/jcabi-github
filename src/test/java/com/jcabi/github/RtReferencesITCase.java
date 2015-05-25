/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.github.OAuthScope.Scope;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtReferences}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtReferencesITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        repos = github.repos();
        repo = rule.repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtReference can create a reference.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void createsReference() throws Exception {
        final References refs = repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder(Tv.HUNDRED)
            .append("refs/tags/").append(name);
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
        final References refs = repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder(Tv.HUNDRED)
            .append("refs/heads/").append(name);
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
     * RtReference can iterate over references in sub-namespace.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void iteratesReferencesInSubNamespace() throws Exception {
        final References refs = repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder(Tv.HUNDRED)
            .append("refs/heads/").append(name);
        refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
                .getString("sha")
        );
        MatcherAssert.assertThat(
            refs.iterate("heads"),
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("heads/").append(name);
        refs.remove(builder.toString());
    }

}
