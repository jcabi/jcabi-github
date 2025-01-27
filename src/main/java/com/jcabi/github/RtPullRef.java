/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.aspects.Loggable;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * GitHub pull request ref.
 *
 * @since 0.24
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "github", "jsn" })
final class RtPullRef implements PullRef {
    /**
     * API entry point.
     */
    private final transient Github github;
    /**
     * JSON of the pull request ref.
     */
    private final transient JsonObject jsn;

    /**
     * Public ctor.
     * @param gthb Github
     * @param json Pull request ref JSON object
     */
    RtPullRef(final Github gthb, final JsonObject json) {
        this.github = gthb;
        this.jsn = json;
    }

    @Override
    public JsonObject json() {
        return this.jsn;
    }

    @Override
    public String ref() {
        return this.jsn.getString("ref");
    }

    @Override
    public String sha() {
        return this.jsn.getString("sha");
    }

    @Override
    public Repo repo() {
        return this.github.repos().get(
            new Coordinates.Simple(
                this.jsn.getJsonObject("repo").getString("full_name")
            )
        );
    }
}
