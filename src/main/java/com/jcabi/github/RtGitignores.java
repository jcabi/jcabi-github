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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonString;
import javax.ws.rs.core.HttpHeaders;
import lombok.EqualsAndHashCode;

/**
 * Github Gitignore.
 * <p>Defines storage of .gitignore templates
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@EqualsAndHashCode(of = { "ghub" , "request" })
final class RtGitignores implements Gitignores {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public CTOR.
     * @param github Github
     */
    public RtGitignores(
        final Github github) {
        this.ghub = github;
        this.request = github().entry().uri()
            .path("/gitignore/templates").back();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public Iterable<String> iterate() throws IOException {
        final RestResponse response = this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        final List<JsonString> list = response.as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonString.class);
        final List<String> templates = new ArrayList<String>(list.size());
        for (final JsonString value : list) {
            templates.add(value.getString());
        }
        return templates;
    }

    @Override
    public String template(
        final String name)
        throws IOException {
        return this.request.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.raw")
            .uri().path(name).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }
}
