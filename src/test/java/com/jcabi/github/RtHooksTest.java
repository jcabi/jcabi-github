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

import com.jcabi.aspects.Immutable;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.JdkRequest;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHooks}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
public final class RtHooksTest {

    /**
     * RtHooks can fetch empty list of hooks.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfHooks() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")
        ).start();
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        try {
            MatcherAssert.assertThat(
                hooks.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtHooks can fetch non empty list of hooks.
     *
     * @todo #122 RtHooks should iterate multiple hooks. Let's implement
     *  a test here and a method of RtHooks. The method should iterate
     *  multiple hooks. See how it's done in other classes with GhPagination.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canFetchNonEmptyListOfHooks() {
        // to be implemented
    }

    /**
     * RtHooks can fetch single hook.
     *
     * @todo #122 RtHooks should be able to get a single Hook. Let's implement
     *  a test here and a method get() of RtHooks.
     *  The method should fetch a single hook.
     *  See how it's done in other classes, using Rexsl request/response.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canFetchSingleHook() {
        // to be implemented
    }

    /**
     * RtHooks can create a hook.
     *
     * @todo #122 RtHooks should be able to create a Hook. Let's implement
     *  a test here and a method create() of RtHooks. The method should create
     *  a hook on some event for some service.
     *  See how it's done in other classes, using Rexsl request/response.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
    @Test
    @Ignore
    public void canCreateHook() {
        // to be implemented
    }

    /**
     * RtHooks can delete a hook.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canDeleteHook() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start();
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        hooks.remove(1);
        try {
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.isEmptyString()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "hooks"))
            .when(repo).coordinates();
        return repo;
    }
}
