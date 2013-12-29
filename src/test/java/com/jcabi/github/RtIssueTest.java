/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.rexsl.test.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssue}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #42 I assumed that the JSON methods json() and fetch() are not
 *  covered by this test suite, since they are covered by GhJson tests.
 *  GhIssue just creates a GhJson using its own request object. I'm
 *  not entirely sure whether we should test it again here or not.
 */
public final class RtIssueTest {

    /**
     * RtIssue should be able to fetch its comments.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesComments() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.comments(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to fetch its labels.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesLabels() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.labels(),
            Matchers.notNullValue()
        );
    }

    /**
     * GhIssue should be able to fetch its events.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchesEvents() throws Exception {
        final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
        MatcherAssert.assertThat(
            issue.events(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtIssue should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final RtIssue less = new RtIssue(new FakeRequest(), this.repo(), 1);
        final RtIssue greater = new RtIssue(new FakeRequest(), this.repo(), 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }

}
