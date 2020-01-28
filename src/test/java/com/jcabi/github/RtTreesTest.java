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
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtTrees}.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class RtTreesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtTrees can create a tree.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void createsTree() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"sha\":\"0abcd89jcabitest\",\"url\":\"http://localhost/1\"}"
            )
        ).start(this.resource.port());
        final Trees trees = new RtTrees(
            new ApacheRequest(container.home()),
            repo()
        );
        final JsonObject tree = Json.createObjectBuilder()
            .add("path", "/path").add("mode", "100644 ")
            .add("type", "blob").add("sha", "sha1")
            .add("content", "content1").build();
        final JsonObject input = Json.createObjectBuilder()
            .add("tree", tree).add("base_tree", "SHA1")
            .build();
        try {
            final Tree tri = trees.create(input);
            MatcherAssert.assertThat(
                tri,
                Matchers.instanceOf(Tree.class)
            );
            MatcherAssert.assertThat(
                trees.get(tri.sha()),
                Matchers.equalTo(tri)
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtTrees can get tree.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void getTree() throws Exception {
        final String sha = "0abcd89jcabitest";
        final Trees trees = new RtTrees(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("sha", sha)
                    .build()
                    .toString()
            ),
            repo()
        );
        MatcherAssert.assertThat(
            trees.get(sha).sha(), Matchers.equalTo(sha)
        );
    }

    /**
     * RtTrees can get tree recursively.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void getTreeRec() throws Exception {
        final String sha = "0abcd89jcabitest";
        final Trees trees = new RtTrees(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("sha", sha)
                    .build()
                    .toString()
            ),
            repo()
        );
        MatcherAssert.assertThat(
            trees.getRec(sha).sha(), Matchers.equalTo(sha)
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();

        final Github github = Mockito.mock(Github.class);
        Mockito.doReturn(github).when(repo).github();
        Mockito.doReturn(Constants.ROOT_REPO).when(github).rootRepoPath();

        return repo;
    }

}
