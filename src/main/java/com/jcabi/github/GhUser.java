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
import com.rexsl.test.JsonResponse;
import com.rexsl.test.Request;
import com.rexsl.test.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = "identity")
@EqualsAndHashCode(of = { "entry", "identity" })
final class GhUser implements User {

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Login name of the user.
     */
    private final transient String identity;

    /**
     * Public ctor.
     * @param req Request
     */
    GhUser(final Request req) {
        this(req, "");
    }

    /**
     * Public ctor.
     * @param req Request
     * @param login User identity/identity
     */
    GhUser(final Request req, final String login) {
        this.entry = req;
        this.identity = login;
    }

    @Override
    public String login() throws IOException {
        return this.json().getString("login");
    }

    @Override
    public JsonObject json() throws IOException {
        final Request req;
        if (this.identity.isEmpty()) {
            req = this.entry.uri().path("/user").back();
        } else {
            req = this.entry.uri()
                .path("/users").path(this.identity)
                .back();
        }
        return req.fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject();
    }

}
