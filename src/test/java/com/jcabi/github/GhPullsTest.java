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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test case for GhPulls.
 * @author Pavel Danilchenko (mits0908@gmail.com)
 * @version $Id$
 */
public final class GhPullsTest {

    /**
     * GhPulls can return string when invoke toString().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnStringWhenInvokeToString() throws Exception {
        final Pulls mock = Mockito.mock(Pulls.class);
        final String dump = "bla";
        Mockito.when(mock.toString()).thenReturn(dump);
        MatcherAssert.assertThat(
            mock.toString(), Matchers.containsString(dump)
        );
    }

    /**
     * GhPulls can return Repo object when invoke repo().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnRepoObjectWhenInvokeRepoMethod() throws Exception {
        final Pulls mock = Mockito.mock(Pulls.class);
        final Repo repoMock = Mockito.mock(Repo.class);
        Mockito.when(mock.repo()).thenAnswer(
            new Answer<Object>() {
            @Override
            public Object answer(
                final InvocationOnMock invocationOnMock) throws Exception {
                return repoMock;
            }
        });
        MatcherAssert.assertThat(mock.repo(), Matchers.equalTo(repoMock));
        Mockito.verify(mock).repo();
    }

    /**
     * GhPulls can return Pull when invoke get(int number).
     * @todo #31:0.2hr add test for get(int number) method
     * @throws Exception If some problem inside
     */
    @Test
    public void returnPullWhenInvokeGet() throws Exception {
    }

    /**
     * GhPulls can return Pull when invoke create().
     * @todo #31:0.2hr add test for create() method
     * @throws Exception If some problem inside
     */
    @Test
    public void returnPullWhenInvokeCreate() throws Exception {
    }

    /**
     * GhPulls can return Iterable<Pull> when invoke iterate().
     * @todo #31:0.2hr add test for iterate() method
     * @throws Exception If some problem inside
     */
    @Test
    public void returnIterableWhenInvokeIterate() throws Exception {
    }

}
