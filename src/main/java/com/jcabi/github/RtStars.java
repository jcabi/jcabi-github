/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.validation.constraints.NotNull;
import org.hamcrest.Matchers;

/**
 * Github starring API.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/starring/">Starring API</a>
 */
@Immutable
final class RtStars implements Stars {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req Request
     */
    RtStars(final Request req) {
        this.request = req.uri()
            .path("/user/starred")
            .back();
    }

    @Override
    public boolean starred(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) throws IOException {
        return this.request
            .uri().path(user).path(repo)
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(
                Matchers.isOneOf(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    HttpURLConnection.HTTP_NOT_FOUND
            )
        ).status() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    @Override
    public void star(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) throws IOException {
        this.request
            .method(Request.PUT)
            .uri().path(user).path(repo)
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public void unstar(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) throws IOException {
        this.request
            .method(Request.DELETE)
            .uri().path(user).path(repo)
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
