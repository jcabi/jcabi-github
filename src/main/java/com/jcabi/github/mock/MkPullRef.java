/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Branch;
import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock GitHub pull request ref.
 *
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "branch" })
final class MkPullRef implements PullRef {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Branch.
     */
    private final transient Branch branch;

    /**
     * Public ctor.
     * @param stg Storage
     * @param brnch Branch
     */
    MkPullRef(
        final MkStorage stg,
        final Branch brnch
    ) {
        this.storage = stg;
        this.branch = brnch;
    }

    @Override
    public Repo repo() {
        return this.branch.repo();
    }

    @Override
    public String ref() {
        return this.branch.name();
    }

    @Override
    public String sha() {
        return this.branch.commit().sha();
    }

    @Override
    public JsonObject json() throws IOException {
        final String user = this.repo().coordinates().user();
        return Json.createObjectBuilder()
            .add("ref", this.ref())
            .add("sha", this.sha())
            .add("label", String.format("%s:%s", user, this.ref()))
            .add("user", new MkUser(this.storage, user).json())
            .add("repo", this.repo().json())
            .build();
    }
}
