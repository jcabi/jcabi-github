/**
 * Copyright (c) 2013-2018, jcabi.com
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

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRelease}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public class RtReleaseTest {
    /**
     * An empty JSON string.
     */
    private static final String EMPTY_JSON = "{}";

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * A mock container used in test to mimic the Github server.
     */
    private transient MkContainer container;

    /**
     * Setting up the test fixture.
     */
    @Before
    public final void setUp() {
        this.container = new MkGrizzlyContainer();
    }

    /**
     * Tear down the test fixture to return to the original state.
     */
    @After
    public final void tearDown() {
        this.container.stop();
    }

    /**
     * RtRelease can edit a release.
     * @throws Exception If any problem during test execution occurs.
     */
    @Test
    public final void editRelease() throws Exception {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, EMPTY_JSON)
        ).start(this.resource.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        final JsonObject json = Json.createObjectBuilder()
            .add("tag_name", "v1.0.0")
            .build();
        release.patch(json);
        final MkQuery query = this.container.take();
        MatcherAssert.assertThat(
            query.method(),
            Matchers.equalTo(Request.PATCH)
        );
        MatcherAssert.assertThat(
            query.body(),
            Matchers.equalTo(json.toString())
        );
    }

    /**
     * RtRelease can delete a release.
     * @throws Exception If any problems in the test occur.
     */
    @Test
    public final void deleteRelease() throws Exception {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, EMPTY_JSON)
        ).start(this.resource.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        release.delete();
        MatcherAssert.assertThat(
            this.container.take().method(),
            Matchers.equalTo(Request.DELETE)
        );
    }

    /**
     * RtRelese can execute PATCH request.
     * @throws Exception if there is any problem
     */
    @Test
    public final void executePatchRequest() throws Exception {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, EMPTY_JSON)
        ).start(this.resource.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        release.patch(Json.createObjectBuilder().add("name", "v1")
            .build()
        );
        MatcherAssert.assertThat(
            this.container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
    }

    /**
     * Create a test release.
     * @param uri REST API entry point.
     * @return A test release.
     */
    private static RtRelease release(final URI uri) {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        final Github github = Mockito.mock(Github.class);
        Mockito.doReturn(github).when(repo).github();
        Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("tstuser").when(coords).user();
        Mockito.doReturn("tstbranch").when(coords).repo();
        return new RtRelease(
            new ApacheRequest(uri),
            repo,
            2
        );
    }

}
