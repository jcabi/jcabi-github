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
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLabels}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class RtLabelsTest {

    /**
     * The rule for skipping test if there's BindException.
     *  and make MkGrizzlyContainers use port() given by this resource to avoid
     *  tests fail with BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtLabels can create a label.
     * @throws Exception if some problem inside
     */
    @Test
    public void createLabel() throws Exception {
        final String name = "API";
        final String color = "FFFFFF";
        final String body = label(name, color).toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
            .start(this.resource.port());
        final RtLabels labels = new RtLabels(
            new JdkRequest(container.home()),
            repo()
        );
        final Label label = labels.create(name, color);
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.POST)
        );
        MatcherAssert.assertThat(
            new Label.Smart(label).name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            new Label.Smart(label).color(),
            Matchers.equalTo(color)
        );
        container.stop();
    }

    /**
     * RtLabels can get a single label.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void getSingleLabel() throws Exception {
        final String name = "bug";
        final String color = "f29513";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                label(name, color).toString()
            )
        ).start(this.resource.port());
        final RtLabels issues = new RtLabels(
            new JdkRequest(container.home()),
            repo()
        );
        final Label label = issues.get(name);
        MatcherAssert.assertThat(
            new Label.Smart(label).color(),
            Matchers.equalTo(color)
        );
        container.stop();
    }

    /**
     * RtLabels can delete a label.
     * @throws Exception if some problem inside
     */
    @Test
    public void deleteLabel() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start(this.resource.port());
        final RtLabels issues = new RtLabels(
            new JdkRequest(container.home()),
            repo()
        );
        issues.delete("issue");
        final MkQuery query = container.take();
        MatcherAssert.assertThat(
            query.method(),
            Matchers.equalTo(Request.DELETE)
        );
        MatcherAssert.assertThat(
            query.body(),
            Matchers.isEmptyOrNullString()
        );
        container.stop();
    }

    /**
     * RtLabels can iterate labels.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateLabels() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(label("new issue", "f29512"))
                    .add(label("new bug", "f29522"))
                    .build().toString()
            )
        ).start(this.resource.port());
        final RtLabels labels = new RtLabels(
            new JdkRequest(container.home()),
            repo()
        );
        MatcherAssert.assertThat(
            labels.iterate(),
            Matchers.<Label>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test.
     * @param name The name of the label
     * @param color A 6 character hex code, identifying the color
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject label(
        final String name, final String color) throws Exception {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("color", color)
            .build();
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
        return repo;
    }
}
