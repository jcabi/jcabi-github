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
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github forks.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/forks/">Forks API</a>
 * @todo #440 Create RtFork class which will implement Fork interface, RtForkTest
 *  will be necessary as well, implement number() and patch() method,
 *  lastly change RtForks to return an RtFork instance instead  of an anonymous
 *  Fork object. Don't forget to remove this to do tag after finishing.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "owner" })
public final class RtForks implements Forks {
    /**
     * Restful Request.
     */
    private final transient Request request;
    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    public RtForks(final Request req, final Repo repo) {
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("forks")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<Fork> iterate(
        @NotNull(message = "sort can't be NULL") final String sort) {
        return new RtPagination<Fork>(
            this.request.uri()
                .queryParam("sort", sort).back(),
            new RtPagination.Mapping<Fork, JsonObject>() {
                @Override
                public Fork map(final JsonObject value) {
                    return new Fork() {
                        @Override
                        public JsonObject json() throws IOException {
                            return value;
                        }
                        @Override
                        public int number() {
                            throw new UnsupportedOperationException("");
                        }
                        @Override
                        public void patch(
                            final JsonObject json) throws IOException {
                            throw new UnsupportedOperationException("");
                        }
                    };
                }
            }
        );
    }

    @Override
    public Fork create(
        @NotNull(message = "organization can't be NULL")
        final String organization) {
        return new Fork() {
            @Override
            public JsonObject json() throws IOException {
                return RtForks.this.request.method(Request.POST)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_ACCEPTED)
                    .as(JsonResponse.class).json().readObject();
            }
            @Override
            public int number() {
                throw new UnsupportedOperationException("");
            }
            @Override
            public void patch(final JsonObject json) throws IOException {
                throw new UnsupportedOperationException("");
            }
        };
    }
}
