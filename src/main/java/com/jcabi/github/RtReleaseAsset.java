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
import com.jcabi.http.RequestURI;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github release asset.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
public final class RtReleaseAsset implements ReleaseAsset {
    /**
     * Release asset url.
     */
    private static final String URL = "/assets";

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Release asset id.
     */
    private final transient int release;

    /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param coords Repository coordinates
     * @param nmbr Release asset id
     */
    RtReleaseAsset(final Request req, final Coordinates coords,
        final int nmbr) {
        this.release = nmbr;
        this.request = getRequest(req, coords)
            .path(String.valueOf(this.release))
            .path(RtReleaseAsset.URL)
            .back();
    }

    /**
     * Public ctor for getting single release asset.
     * @param req RESTful API entry point
     * @param nmbr Release asset id
     * @param coords Repository coordinates
     */
    RtReleaseAsset(final Request req, final int nmbr,
        final Coordinates coords) {
        this.release = nmbr;
        this.request = getRequest(req, coords)
            .path(RtReleaseAsset.URL)
            .path(String.valueOf(this.release))
            .back();
    }

    /**
     * Creates request object.
     * @param req RESTful API entry point
     * @param coords Repository coordinates
     * @return Request
     */
    private RequestURI getRequest(final Request req, final Coordinates coords) {
        return req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/releases");
    }

    @Override
    public int number() {
        return this.release;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

}
