/**
 * Copyright (c) 2013-2022, jcabi.com
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
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtValuePagination}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
public final class RtValuePaginationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPagination can jump to next page of results.
     * @throws Exception if there is any problem
     */
    @Test
    public void jumpNextPage() throws Exception {
        final String jeff = "Jeff";
        final String mark = "Mark";
        final String judy = "Judy";
        final String jessy = "Jessy";
        final MkContainer container = new MkGrizzlyContainer().next(
            RtValuePaginationTest.simple(jeff, mark)
                .withHeader("Link", "</s?page=3&per_page=100>; rel=\"next\"")
        ).next(RtValuePaginationTest.simple(judy, jessy))
            .start(this.resource.port());
        final Request request = new ApacheRequest(container.home());
        final RtValuePagination<JsonObject, JsonArray> page =
            new RtValuePagination<JsonObject, JsonArray>(
                request,
                new RtValuePagination.Mapping<JsonObject, JsonArray>() {
                    @Override
                    public JsonObject map(final JsonArray object) {
                        return Json.createObjectBuilder()
                            .add("id1", object.getString(0))
                            .add("id2", object.getString(1))
                            .build();
                    }
                }
            );
        final Iterator<JsonObject> iterator = page.iterator();
        MatcherAssert.assertThat(
            iterator.next().toString(),
            Matchers.allOf(
                Matchers.containsString(jeff),
                Matchers.containsString(mark)
            )
        );
        MatcherAssert.assertThat(
            iterator.next().toString(),
            Matchers.allOf(
                Matchers.containsString(judy),
                Matchers.containsString(jessy)
            )
        );
        container.stop();
    }

    /**
     * RtValuePagination can throw if there is no more elements in pagination.
     * @throws Exception if there is any problem
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsIfNoMoreElement() throws Exception {
        final String jeff = "other Jeff";
        final String mark = "other Mark";
        final MkContainer container = new MkGrizzlyContainer().next(
            RtValuePaginationTest.simple(jeff, mark)
        ).start(this.resource.port());
        try {
            final Request request = new ApacheRequest(container.home());
            final RtValuePagination<JsonObject, JsonArray> page =
                new RtValuePagination<JsonObject, JsonArray>(
                    request,
                    new RtValuePagination.Mapping<JsonObject, JsonArray>() {
                        @Override
                        public JsonObject map(final JsonArray object) {
                            return Json.createObjectBuilder()
                                .add("id3", object.getString(0))
                                .add("id4", object.getString(1))
                                .build();
                        }
                    }
                );
            final Iterator<JsonObject> iterator = page.iterator();
            iterator.next();
            MatcherAssert.assertThat(
                iterator.next(),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return MkAnswer.Simple to test.
     * @param one First array element
     * @param another Second array element
     * @return MkAnswer.Simple
     * @throws Exception If some problem inside
     */
    private static MkAnswer.Simple simple(final String one,
        final String another
    ) throws Exception {
        final String message = Json.createArrayBuilder()
            .add(Json.createArrayBuilder().add(one).add(another))
            .build().toString();
        return new MkAnswer.Simple(HttpURLConnection.HTTP_OK, message);
    }
}
