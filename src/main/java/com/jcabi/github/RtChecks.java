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
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Github Checks.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @version $Id$
 * @since 1.5.0
 */
class RtChecks implements Checks {

    /**
     * Pull request.
     */
    private final transient Pull pull;

    /**
     * Http Request.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req Request
     * @param prequest Pull request
     */
    RtChecks(final Request req, final Pull prequest) {
        this.pull = prequest;
        this.request = req;
    }

    /**
     * Get all checks.
     * JSON schema:
     * <p>{@code
     * {
     *   "total_count": 1,
     *   "check_runs": [
     *     {
     *       "id": 4,
     *       "status": "completed",
     *       ...
     *     }
     *   ]
     * }
     * }</p>
     * @return Checks.
     * @throws IOException If there is any I/O problem.
     */
    @Override
    public Collection<Check> all() throws IOException {
        final Coordinates coords = this.pull.repo().coordinates();
        return this.request.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/commits")
            .path(this.pull.head().ref())
            .path("/check-runs")
            .back()
            .method(Request.GET).fetch().as(JsonResponse.class)
            .json()
            .readObject()
            .getJsonArray("check_runs")
            .stream()
            .map(RtChecks::check)
            .collect(Collectors.toList());
    }

    /**
     * Get check by id.
     * @param value Json value.
     * @return Check.
     */
    private static RtCheck check(final JsonValue value) {
        final JsonObject check = value.asJsonObject();
        return new RtCheck(
            check.getString("status"),
            check.getString("conclusion")
        );
    }
}
