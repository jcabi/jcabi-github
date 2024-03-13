/**
 * Copyright (c) 2013-2024, jcabi.com
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
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github commit status.
 * @author Marcin Cylke (maracin.cylke+github@gmail.com)
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @since 0.23
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "cmmt", "jsn" })
public final class RtStatus implements Status {
    /**
     * Associated commit.
     */
    private final transient Commit cmmt;
    /**
     * Encapsulated status JSON.
     */
    private final transient String jsn;

    /**
     * Public ctor.
     * @param cmt Associated commit
     * @param obj Status JSON object
     */
    public RtStatus(
        final Commit cmt,
        final JsonObject obj
    ) {
        this.cmmt = cmt;
        this.jsn = obj.toString();
    }

    @Override
    public JsonObject json() {
        return Json.createReader(new StringReader(this.jsn)).readObject();
    }

    @Override
    public int identifier() {
        return this.json().getInt("id");
    }

    @Override
    public String url() {
        return this.json().getString("url");
    }

    @Override
    public Commit commit() {
        return this.cmmt;
    }
}
