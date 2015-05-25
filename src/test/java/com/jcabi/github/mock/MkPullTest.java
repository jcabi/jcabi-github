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
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.github.PullRef;
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
     * Login of test user.
     */
    private static final String USERNAME = "patrick";
    /**
     * Base branch name.
     */
    private static final String BASE = "my-base-branch";
    /**
     * Head branch name.
     */
    private static final String HEAD = "my-head-branch";

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
        final Pull pull = MkPullTest.pullRequest();
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
        final Pull pull = MkPullTest.pullRequest();
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
        final Pull pull = MkPullTest.pullRequest();
        MatcherAssert.assertThat(
            pull.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkPull can get its base ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canGetBase() throws Exception {
        final PullRef base = pullRequest().base();
        MatcherAssert.assertThat(base, Matchers.notNullValue());
        MatcherAssert.assertThat(base.ref(), Matchers.equalTo(BASE));
    }

    /**
     * MkPull can get its head ref.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void canGetHead() throws Exception {
        final PullRef head = pullRequest().head();
        MatcherAssert.assertThat(head, Matchers.notNullValue());
        MatcherAssert.assertThat(head.ref(), Matchers.equalTo(HEAD));
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
            json.getInt("number"),
            Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            json.getJsonObject("head").getString("label"),
            Matchers.equalTo(String.format("%s:%s", USERNAME, head))
        );
        MatcherAssert.assertThat(
            json.getJsonObject("base").getString("label"),
            Matchers.equalTo(String.format("%s:%s", USERNAME, base))
        );
        MatcherAssert.assertThat(
            json.getJsonObject("user").getString("login"),
            Matchers.equalTo(USERNAME)
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
        return new MkGithub(USERNAME).randomRepo();
    }

    /**
     * Create a pull request to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Pull pullRequest() throws Exception {
        final Repo rpo = MkPullTest.repo();
        final MkBranches branches = (MkBranches) (rpo.branches());
        branches.create(BASE, "e11f7ffa797f8422f016576cb7c2f5bb6f66aa51");
        branches.create(HEAD, "5a8d0143b3fa9de883a5672d4a1f44d472657a8a");
        return rpo.pulls().create("Test PR", HEAD, BASE);
    }
}
