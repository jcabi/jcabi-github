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

import com.jcabi.github.mock.MkGithub;
import com.rexsl.test.Request;
import com.rexsl.test.RequestBody;
import com.rexsl.test.RequestURI;
import com.rexsl.test.request.FakeRequest;
import javax.json.JsonStructure;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link GhGist}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class GhGistTest {

    /**
     * GhGist should be able to do reads.
     *
     * @throws Exception if there is a problem.
     */
    @Test
    public void executeRead() throws Exception {
        final Request req = Mockito.mock(Request.class);
        final RequestURI uri = Mockito.mock(RequestURI.class);
        Mockito.doReturn(uri).when(req).uri();
        Mockito.doReturn(uri).when(uri).path(Mockito.anyString());
        final Request fakereq = new FakeRequest().withBody(
            "{\"files\":{\"hello\":{\"raw_url\":\"world\"}}}"
        );
        Mockito.doReturn(fakereq).when(uri).back();
        final GhGist gist = new GhGist(new MkGithub(), req, "test");
        MatcherAssert.assertThat(
            gist.read("hello"),
            Matchers.containsString("world")
        );
    }

    /**
     * GhGist should be able to do writes.
     *
     * @throws Exception if there is a problem.
     */
    @Test
    public void executeWrite() throws Exception {
        final Request req = Mockito.mock(Request.class);
        final RequestURI uri = Mockito.mock(RequestURI.class);
        Mockito.doReturn(uri).when(req).uri();
        Mockito.doReturn(uri).when(uri).path(Mockito.anyString());
        Mockito.doReturn(req).when(uri).back();
        Mockito.doReturn(req).when(req).method(Request.PATCH);
        final RequestBody body = Mockito.mock(RequestBody.class);
        Mockito.doReturn(body).when(req).body();
        Mockito.doReturn(body).when(body).set(Mockito.any(JsonStructure.class));
        final Request fakereq = new FakeRequest();
        Mockito.doReturn(fakereq).when(body).back();
        final GhGist gist = new GhGist(new MkGithub(), req, "testWrite");
        gist.write("testFile", "testContent");
        Mockito.verify(req).method(Request.PATCH);
        MatcherAssert.assertThat(
            fakereq.body(),
            Matchers.notNullValue()
        );
    }

}
