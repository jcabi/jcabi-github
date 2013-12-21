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

import com.rexsl.test.Request;
import com.rexsl.test.RequestURI;

/**
 * Test case for {@link GhIssue}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #42? I assumed that the JSON methods json() and fetch() are not
 * 	covered by this test suite, since they are covered by GhJson tests.
 *  GhIssue just creates a GhJson using its own request object. I'm
 *  not entirely sure whether we should test it again here or not.
 */
public final class GhIssueTest {

	/**
	 * GhIssue should be able to fetch its comments.
	 *
	 * @throws Exception if a problem occurs.
	 */
	@Test
	public void fetchesComments() throws Exception {
        final Repo repo = repo();

        final Request req = request();

		final GhIssue ghIssue = new GhIssue(req, repo, 1);

		MatcherAssert.assertThat(
			ghIssue.comments(),
			Matchers.notNullValue()
		);
	}

	/**
	 * GhIssue should be able to fetch its labels.
	 *
	 * @throws Exception if a problem occurs.
	 */
	@Test
	public void fetchesLabels() throws Exception {
        final Repo repo = repo();

        final Request req = request();

		final GhIssue ghIssue = new GhIssue(req, repo, 1);

		MatcherAssert.assertThat(
			ghIssue.labels(),
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
        final Repo repo = repo();

        final Request req = request();

		final GhIssue ghIssue = new GhIssue(req, repo, 1);

		MatcherAssert.assertThat(
			ghIssue.events(),
			Matchers.notNullValue()
		);
	}

	/**
	 * Mock request for GhIssue creation.
	 * @return The mock request.
	 */
	private Request request() {
		final Request req = Mockito.mock(Request.class);
        final RequestURI uri = Mockito.mock(RequestURI.class);
        Mockito.doReturn(uri).when(req).uri();
        Mockito.doReturn(uri).when(uri).path(Mockito.anyString());
        Mockito.doReturn(req).when(uri).back();

		return req;
	}

	/**
	 * Mock repo for GhIssue creation.
	 * @return The mock repo.
	 */
	private Repo repo() {
		final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
		return repo;
	}

}
