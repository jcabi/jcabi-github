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
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import lombok.EqualsAndHashCode;

/**
 * Github release assets.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
final class RtReleaseAssets implements ReleaseAssets {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Owner of assets.
     */
    private final transient Release owner;

    /**
     * Public ctor.
     * @param req Request
     * @param release Issue
     */
    RtReleaseAssets(final Request req, final Release release) {
        this.entry = req;
        final Coordinates coords = release.repo().coordinates();
        // @checkstyle MultipleStringLiteralsCheck (7 lines)
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/releases")
            .path(Integer.toString(release.number()))
            .path("/assets")
            .back();
        this.owner = release;
    }

    @Override
    @NotNull(message = "release is never NULL")
    public Release release() {
        return this.owner;
    }

    @Override
    @NotNull(message = "Iterable of ReleaseAsset is never NULL")
    public Iterable<ReleaseAsset> iterate() {
        return new RtPagination<ReleaseAsset>(
            this.request.uri().back()
                .method(Request.GET),
            new RtPagination.Mapping<ReleaseAsset, JsonObject>() {
                @Override
                public ReleaseAsset map(final JsonObject value) {
                    return RtReleaseAssets.this.get(
                    //@checkstyle MultipleStringLiteralsCheck (1 line)
                        value.getInt("id")
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "ReleaseAsset is never NULL")
    public ReleaseAsset upload(
        final byte[] content,
        @NotNull(message = "type can't be NULL") final String type,
        @NotNull(message = "name can't be NULL") final String name
    ) throws IOException {
        return this.get(
            this.request.uri()
                .set(URI.create("https://uploads.github.com"))
                .path("/repos")
                .path(this.owner.repo().coordinates().user())
                .path(this.owner.repo().coordinates().repo())
                .path("/releases")
                .path(String.valueOf(this.owner.number()))
                .path("/assets")
                .queryParam("name", name)
                .back()
                .method(Request.POST)
                .reset(HttpHeaders.CONTENT_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, type)
                .body().set(content).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    @NotNull(message = "ReleaseAsset is never NULL")
    public ReleaseAsset get(final int number) {
        return new RtReleaseAsset(this.entry, this.owner, number);
    }

}
