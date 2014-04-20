/**
 * Copyright (c) 2013-2014, JCabi.com
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
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github public keys.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/users/keys/">Public Keys API</a>
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
public final class RtPublicKeys implements PublicKeys {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     *
     * @param req Request
     * @param user User
     */
    public RtPublicKeys(final Request req, final User user) {
        this.entry = req;
        this.owner = user;
        this.request = this.entry.uri().path("/user").path("/keys").back();
    }

    @Override
    @NotNull(message = "user is never NULL")
    public User user() {
        return this.owner;
    }

    @Override
    @NotNull(message = "Iterable of public keys is never NULL")
    public Iterable<PublicKey> iterate() {
        return new RtPagination<PublicKey>(
            this.request,
            new RtPagination.Mapping<PublicKey, JsonObject>() {
                @Override
                public PublicKey map(final JsonObject object) {
                    return RtPublicKeys.this.get(object.getInt("id"));
                }
            }
        );
    }

    @Override
    @NotNull(message = "public key is never NULL")
    public PublicKey create(final String title, final String key)
        throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("title", title)
                        .add("key", key)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    @NotNull(message = "public key is never NULL")
    public PublicKey get(final int number) {
        return new RtPublicKey(this.entry, this.owner, number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(Integer.toString(number)).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public String toString() {
        return this.request.uri().get().toString();
    }

}
