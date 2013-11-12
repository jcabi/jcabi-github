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

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Mocker of {@link Comment}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class CommentMocker implements Comment {

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Issue.
     */
    private final transient Issue owner;

    /**
     * Author of it.
     */
    private final transient User who;

    /**
     * JSON with properties.
     */
    private transient JsonObject object;

    /**
     * Public ctor.
     * @param number Comment number
     * @param issue Owner of it
     * @param author Author of it
     */
    public CommentMocker(final int number, final Issue issue,
        final User author) {
        this.num = number;
        this.owner = issue;
        this.who = author;
        this.object = Json.createObjectBuilder().build();
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public User author() {
        return this.who;
    }

    @Override
    public void remove() {
        // nothing to do
    }

    @Override
    public JsonObject json() {
        return this.object;
    }

    @Override
    public void patch(final JsonObject json) {
        this.object = new JsonMocker(this.object).patch(json);
    }

}
