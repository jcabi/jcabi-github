/**
 * Copyright (c) 2013-2023, jcabi.com
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

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;

/**
 * Github statuses for a given commit.
 * @author Marcin Cylke (maracin.cylke+github@gmail.com)
 * @version $Id$
 * @since 0.23
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public class RtStatuses implements Statuses {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Commit cmmt.
     */
    private final transient Commit cmmt;

    /**
     * Create a new status-aware object based on given commit.
     * @param req Http request
     * @param commit Specific commit
     */
    RtStatuses(final Request req, final Commit commit) {
        final Coordinates coords = commit.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/statuses")
            .path(commit.sha())
            .back();
        this.cmmt = commit;
    }

    /**
     * Generate string representation.
     * @return String representation
     */
    @Override
    public final String toString() {
        return this.request.uri().get().toString();
    }

    /**
     * Get commit object.
     * @return Commit object
     */
    @Override
    public final Commit commit() {
        return this.cmmt;
    }

    /**
     * Create new status for a commit.
     * @param status Add this status
     * @return Returned status
     * @throws IOException In case of any I/O problems
     */
    @Override
    public final Status create(
        final StatusCreate status
    ) throws IOException {
        final JsonObject response = this.request.method(Request.POST)
            .body().set(status.json()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json().readObject();
        return new RtStatus(this.cmmt, response);
    }

    /**
     * Get all status messages for a given commit.
     * @param ref It can be a SHA, a branch name, or a tag name.
     * @return Full list of statuses for this commit.
     * @todo #1126:30min Implement this method which gets all status messages for a given commit.
     */
    @Override
    public final Iterable<Status> list(
        final String ref
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * JSON object for this request.
     * @return Json object
     * @throws IOException In case of I/O problems
     */
    @Override
    public final JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
