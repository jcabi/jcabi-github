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
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Github markdown.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6
 * @todo #1 Unit test for GhMarkdown is required. Let's create a simple one,
 *  to check that the class implements key functions correctly. I think
 *  we should mock Request using Mockito or com.rexsl.test.request.FakeRequest,
 *  and make sure that the class sends correct HTTP requests and parses
 *  results correctly.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class GhMarkdown implements Markdown {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
    GhMarkdown(final Github github, final Request req) {
        this.ghub = github;
        this.request = req.uri().path("markdown").back().method(Request.POST);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public String render(
        @NotNull(message = "JSON can't be NULL") final JsonObject json)
        throws IOException {
        final StringWriter output = new StringWriter();
        Json.createWriter(output).writeObject(json);
        return this.request
            .body()
            .set(output.toString())
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertHeader(
                HttpHeaders.CONTENT_TYPE,
                Matchers.everyItem(
                    Matchers.startsWith(MediaType.TEXT_HTML)
                )
            )
            .body();
    }

    @Override
    public String raw(
        @NotNull(message = "Markdown can't be NULL") final String text)
        throws IOException {
        return this.request
            .body()
            .set(text)
            .back()
            .uri().path("raw").back()
            .reset(HttpHeaders.CONTENT_TYPE)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertHeader(
                HttpHeaders.CONTENT_TYPE,
                Matchers.everyItem(
                    Matchers.startsWith(MediaType.TEXT_HTML)
                )
            )
            .body();
    }

}
