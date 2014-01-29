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
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Test case for {@link RtRelease}.
 * @author Haris Osmanagic (haris.osmanagic@gmail.com)
 * @version $Id$
 * @since 0.8
 */
public final class RtReleaseITCase {

	/**
	 * Test release.
	 */
	private transient Release release;

	/**
	 * Test repository.
	 */
	private transient Repo repo;

	/**
	 * Set up test fixtures.
	 * @throws IOException If creating the test release didn't succeed.
	 */
	@Before
	public final void setUp() throws IOException {
		final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        this.repo = github.repos().get(
            new Coordinates.Simple(System.getProperty("failsafe.github.repo"))
        );
        this.release = this.repo.releases().create("jcabi_test_tag");
	}

	/**
	 * Tear down test fixtures.
	 * @throws IOException If deleting the test release didn't succeed.
	 */
	@After
	public final void tearDown() throws IOException {
		this.release.delete();
	}

    /**
     * RtReleases can iterate releases.
     * @throws Exception If any problems during test execution occur.
     */
    @Test
    public void canEditRelease() throws Exception {
        JsonObject jsonPatch = Json.createObjectBuilder()
            .add("tag_name", "v23")
            .add("name", "JCabi Github test release")
            .add("body", "JCabi Github was here!")
            .build();
        this.release.patch(jsonPatch);
        final JsonObject json = this.repo.releases().get(this.release.number()).json();
        MatcherAssert.assertThat(
            json.getString("tag_name"),
            Matchers.equalTo("v23")
        );
        MatcherAssert.assertThat(
            json.getString("name"),
            Matchers.equalTo("JCabi Github test release")
        );
        MatcherAssert.assertThat(
            json.getString("body"),
            Matchers.equalTo("JCabi Github was here!")
        );
    }
}
