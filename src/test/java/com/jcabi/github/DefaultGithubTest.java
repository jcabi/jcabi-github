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

import com.rexsl.test.Request;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test case for DefaultGithub.
 * @author Pavel Danilchenko (mits0908@gmail.com)
 * @version $Id$
 */
public final class DefaultGithubTest {

    /**
     * DefaultGithub can return Request when invoke entity().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnRequestWhenInvokeEntity() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Request requestMock = Mockito.mock(Request.class);
        Mockito.when(mock.entry()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return requestMock;
                }
            });
        MatcherAssert.assertThat(mock.entry(), Matchers.equalTo(requestMock));
        Mockito.verify(mock).entry();
    }

    /**
     * DefaultGithub can return Repos when invoke repos().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnReposWhenInvokeReposMethod() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Repos reposMock = Mockito.mock(Repos.class);
        Mockito.when(mock.repos()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return reposMock;
                }
            });
        MatcherAssert.assertThat(mock.repos(), Matchers.equalTo(reposMock));
        Mockito.verify(mock).repos();
    }

    /**
     * Github can return Gists when invoke gists().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnGistsWhenInvokeGistsMethod() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Gists gistsMock = Mockito.mock(Gists.class);
        Mockito.when(mock.gists()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return gistsMock;
                }
            });
        MatcherAssert.assertThat(mock.gists(), Matchers.equalTo(gistsMock));
        Mockito.verify(mock).gists();
    }

    /**
     * Github can return Users when invoke users().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnUsersWhenInvokeUsersMethod() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Users usersMock = Mockito.mock(Users.class);
        Mockito.when(mock.users()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return usersMock;
                }
            });
        MatcherAssert.assertThat(mock.users(), Matchers.equalTo(usersMock));
        Mockito.verify(mock).users();
    }

    /**
     * Github can return Limits when invoke limits().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnLimitsWhenInvokeLimitsMethod() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Limits limitsMock = Mockito.mock(Limits.class);
        Mockito.when(mock.limits()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return limitsMock;
                }
            });
        MatcherAssert.assertThat(mock.limits(), Matchers.equalTo(limitsMock));
        Mockito.verify(mock).limits();
    }

    /**
     * Github can return JsonObject when invoke meta().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnJsonWhenInvokeMeta() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final JsonObject jsonObjectMock = Mockito.mock(JsonObject.class);
        Mockito.when(mock.meta()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return jsonObjectMock;
                }
            });
        MatcherAssert.assertThat(mock.meta(), Matchers.equalTo(jsonObjectMock));
        Mockito.verify(mock).meta();
    }

    /**
     * Github can return JsonObject when invoke emojis().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnJsonWhenInvokeEmojis() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final JsonObject jsonObjectMock = Mockito.mock(JsonObject.class);
        Mockito.when(mock.emojis()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return jsonObjectMock;
                }
            });
        MatcherAssert.assertThat(
            mock.emojis(), Matchers.equalTo(jsonObjectMock)
        );
        Mockito.verify(mock).emojis();
    }

    /**
     * Github can return Markdown when invoke markdown().
     * @throws Exception If some problem inside
     */
    @Test
    public void returnMarkdownWhenInvokeMarkdownMethod() throws Exception {
        final Github mock = Mockito.mock(Github.class);
        final Markdown markdownMock = Mockito.mock(Markdown.class);
        Mockito.when(mock.markdown()).thenAnswer(
            new Answer<Object>() {
                @Override
                public Object answer(
                    final InvocationOnMock invocationOnMock) throws Exception {
                    return markdownMock;
                }
            });
        MatcherAssert.assertThat(
            mock.markdown(), Matchers.equalTo(markdownMock)
        );
        Mockito.verify(mock).markdown();
    }
}

