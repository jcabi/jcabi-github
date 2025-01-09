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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import java.util.Iterator;
import javax.json.JsonObject;

/**
 * Github pagination.
 *
 * <p>This class is a convenient iterator over multiple JSON objects
 * returned by Github API. For example, to iterate through notifications
 * (see Notifications API) you can use this code:</p>
 *
 * <pre> Iterable&lt;JsonObject&gt; notifications = new RtPagination&lt;&gt;(
 *   new RtGithub(oauth).entry()
 *     .uri().path("/notifications").back(),
 *   RtPagination.COPYING
 * );</pre>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 * @param <T> Type of iterable objects
 * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
 * @since 0.11
 */
@Immutable
public final class RtPagination<T> implements Iterable<T> {

    /**
     * Mapping that just copies JsonObject.
     * @checkstyle LineLength (3 lines)
     */
    public static final RtValuePagination.Mapping<JsonObject, JsonObject> COPYING =
        value -> value;

    /**
     * Encapsulated paging.
     */
    private final transient RtValuePagination<T, JsonObject> pages;

    /**
     * Public ctor.
     * @param req Request
     * @param mpp Mapping
     */
    public RtPagination(
        final Request req,
        final RtValuePagination.Mapping<T, JsonObject> mpp
    ) {
        this.pages = new RtValuePagination<>(req, mpp);
    }

    @Override
    public Iterator<T> iterator() {
        return this.pages.iterator();
    }

    /**
     * Entry.
     * @return Entry point
     */
    public Request request() {
        return this.pages.request();
    }

    /**
     * Mapping.
     * @return Mapping
     */
    public RtValuePagination.Mapping<T, JsonObject> mapping() {
        return this.pages.mapping();
    }
}
