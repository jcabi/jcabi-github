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
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github Blob.
 *
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "hash" })
final class RtBlob implements Blob {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Blob SHA hash.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param sha Number of the get
     */
    RtBlob(
        @NotNull(message = "Request can't be NULL") final Request req,
        @NotNull(message = "Repo can't be NULL") final Repo repo,
        @NotNull(message = "Sha can't be NULL") final String sha) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/git")
            .path("/blobs")
            .path(sha)
            .back();
        this.hash = sha;
    }

    @Override
    @NotNull(message = "sha is never NULL")
    public String sha() {
        return this.hash;
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
