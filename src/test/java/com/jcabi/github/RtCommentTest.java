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
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtComment}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtCommentTest {

    /**
     * JSon name for test.
     */
    private static final String JSON_TEST_NAME = "name";
    /**
     * JSon value for test.
     */
    private static final String JSON_TEST_VALUE = "test";
    /**
     * Issue title for test.
     */
    private static final String ISSUE_TEST_TITLE = "testing";
    /**
     * Issue body for test.
     */
    private static final String ISSUE_TEST_BODY = "issue";

    /**
     * RtComment should be able to compare different instances.
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final Issue issue = new MkGithub().repos().create(
            Json.createObjectBuilder()
                .add(JSON_TEST_NAME, JSON_TEST_VALUE).build()
        ).issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment less = new RtComment(new FakeRequest(), issue, 1);
        final RtComment greater = new RtComment(new FakeRequest(), issue, 2);
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * This tests that RtComment can properly return its issue (owner).
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void returnsIssue() throws Exception {
        final Issue issue = new MkGithub().repos().create(
            Json.createObjectBuilder()
                .add(JSON_TEST_NAME, JSON_TEST_VALUE).build()
        ).issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment comment = new RtComment(new FakeRequest(), issue, 1);
        MatcherAssert.assertThat(
            comment.issue(),
            Matchers.is(issue)
        );
    }

    /**
     * This tests that RtComment can properly return its number.
     * @throws Exception - in case something goes wrong.
     */
    @Test
    public void returnsNumber() throws Exception {
        final Issue issue = new MkGithub().repos()
            .create(Json.createObjectBuilder()
                .add(
                    JSON_TEST_NAME,
                    JSON_TEST_VALUE
                ).build()
            )
                .issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final int commentNumber = 10;
        final RtComment comment = new RtComment(
            new FakeRequest(),
            issue,
            commentNumber
        );
        MatcherAssert.assertThat(
            comment.number(),
            Matchers.is(commentNumber)
        );
    }

    /**
     * This tests that the remove() method is working fine.
     * @throws Exception - in case something goes wrong.
     */
    @Test
    public void removesComment() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_NO_CONTENT,
                ""
            )
        )
            .start();
        final Issue issue = new MkGithub().repos()
            .create(Json.createObjectBuilder()
                .add(
                    JSON_TEST_NAME,
                    JSON_TEST_VALUE
                ).build()
            )
            .issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment comment = new RtComment(new ApacheRequest(
            container.home()), issue, 10);
        try {
            comment.remove();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This tests that the json() method is working properly.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void returnsJSon() throws Exception {
        final StringBuilder body = new StringBuilder();
        body.append('{');
        body.append('"');
        body.append(JSON_TEST_NAME);
        body.append('"');
        body.append(':');
        body.append('"');
        body.append(JSON_TEST_VALUE);
        body.append('"');
        body.append('}');
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK, body.toString())).start();
        final Issue issue = new MkGithub().repos()
            .create(Json.createObjectBuilder()
                .add(
                    JSON_TEST_NAME,
                    JSON_TEST_VALUE
                ).build()
            )
            .issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment comment = new RtComment(
            new ApacheRequest(container.home()), issue, 10);
        try {
            final JsonObject json = comment.json();
            MatcherAssert.assertThat(
                json.getString(JSON_TEST_NAME),
                Matchers.is(JSON_TEST_VALUE)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This tests that the patch() method is functioning properly.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void patchesComment() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                ""
            )
        ).start();
        final Issue issue = new MkGithub().repos()
            .create(Json.createObjectBuilder()
                .add(JSON_TEST_NAME, JSON_TEST_VALUE).build()
            )
            .issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment comment = new RtComment(new ApacheRequest(
            container.home()), issue, 10);
        final JsonObject jsonPatch = Json.createObjectBuilder()
            .add("title", "test comment").build();
        try {
            comment.patch(jsonPatch);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This tests that the toString() method is working fine.
     * @throws Exception - if anything goes wrong.
     */
    @Test
    public void givesToString() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                ""
            )
        ).start();
        final Issue issue = new MkGithub().repos()
            .create(Json.createObjectBuilder()
                .add(
                    JSON_TEST_NAME,
                    JSON_TEST_VALUE
                ).build()
            )
            .issues().create(ISSUE_TEST_TITLE, ISSUE_TEST_BODY);
        final RtComment comment = new RtComment(new ApacheRequest(
            container.home()), issue, 10);
        try {
            final String stringComment = comment.toString();
            MatcherAssert.assertThat(
                stringComment,
                Matchers.not(Matchers.isEmptyOrNullString())
            );
            MatcherAssert.assertThat(stringComment, Matchers.endsWith("10"));
        } finally {
            container.stop();
        }
    }
}
