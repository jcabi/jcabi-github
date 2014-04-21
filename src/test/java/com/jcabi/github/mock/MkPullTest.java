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
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkPull}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkPullTest {

    /**
     * MkPull should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final MkPull less = new MkPull(
            new MkStorage.InFile(),
            "login-less",
            Mockito.mock(Coordinates.class),
            1
        );
        final MkPull greater = new MkPull(
            new MkStorage.InFile(),
            "login-greater",
            Mockito.mock(Coordinates.class),
            2
        );
        MatcherAssert.assertThat(
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkPull can get comments number if no comments.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetCommentsNumberIfZero() throws Exception {
        final Pull pull = MkPullTest.repo().pulls().create("", "", "");
        MatcherAssert.assertThat(
            pull.json().getInt("comments"),
            Matchers.is(0)
        );
    }

    /**
     * MkPull can get comments number if some comments exist.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetCommentsNumberIfNonZero() throws Exception {
        final Repo repo =  MkPullTest.repo();
        final Pull pull = repo.pulls().create("", "", "");
        pull.comments().post("comment1", "path1", "how are you?", 1);
        pull.comments().post("comment2", "path2", "how are you2?", 2);
        MatcherAssert.assertThat(
            pull.json().getInt("comments"),
            Matchers.is(2)
        );
    }

    /**
     * MkPull can get comments.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canGetComments() throws Exception {
        final Repo repo =  MkPullTest.repo();
        final Pull pull = repo.pulls().create("", "", "");
        MatcherAssert.assertThat(
            pull.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can be represented as JSON.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canRetrieveAsJson() throws Exception {
        final String head = "blah";
        final String base = "aaa";
        final Pull pull = repo().pulls().create("Test Pull Json", head, base);
        final JsonObject json = pull.json();
        MatcherAssert.assertThat(
            json.getString("number"),
            Matchers.equalTo("1")
        );
        MatcherAssert.assertThat(
            json.getString("head"),
            Matchers.equalTo(head)
        );
        MatcherAssert.assertThat(
            json.getString("base"),
            Matchers.equalTo(base)
        );
    }

    /**
     * MkPull can perform JSON patch operation.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canPatchJson() throws Exception {
        final Pull pull = repo().pulls().create("Test Patch", "def", "abc");
        final String value = "someValue";
        pull.patch(
            Json.createObjectBuilder().add("patch", value).build()
        );
        MatcherAssert.assertThat(
            pull.json().getString("patch"),
            Matchers.equalTo(value)
        );
    }

    /**
     * Create an repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
