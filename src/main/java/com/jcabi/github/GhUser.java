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
import com.rexsl.test.RestTester;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
@EqualsAndHashCode(of = { "header", "identity" })
final class GhUser implements User {

    /**
     * Authentication header.
     */
    private final transient String header;

    /**
     * Login name of the user.
     */
    private final transient String identity;

    /**
     * Public ctor.
     * @param hdr Authentication header
     */
    GhUser(final String hdr) {
        this(hdr, "");
    }

    /**
     * Public ctor.
     * @param hdr Authentication header
     * @param login User identity/identity
     */
    GhUser(final String hdr, final String login) {
        this.header = hdr;
        this.identity = login;
    }

    @Override
    public String login() {
        return this.identity;
    }

    @Override
    public String name() {
        final URI uri;
        if (this.identity.isEmpty()) {
            uri = Github.ENTRY.clone().path("/user").build();
        } else {
            uri = Github.ENTRY.clone()
                .path("/users/{user}")
                .build(this.identity);
        }
        return RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .get("getting Github user identity")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readObject().getString("name");
    }

}
