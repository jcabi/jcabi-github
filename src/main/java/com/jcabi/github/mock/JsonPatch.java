/**
 * Copyright (c) 2013-2015, jcabi.com
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
import java.io.IOException;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * Json patch.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
final class JsonPatch {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Public ctor.
     * @param stg Storage to use
     */
    JsonPatch(final MkStorage stg) {
        this.storage = stg;
    }

    /**
     * Patch an XML object/element.
     * @param xpath XPath to locate the node to patch
     * @param obj Object to apply
     * @throws IOException If there is any I/O problem
     */
    public void patch(
        final String xpath,
        final JsonObject obj
    ) throws IOException {
        final Directives dirs = new Directives().xpath(xpath);
        for (final Map.Entry<String, JsonValue> entry : obj.entrySet()) {
            dirs.addIf(entry.getKey())
                .set(StringUtils.strip(entry.getValue().toString(), "\""))
                .up();
        }
        this.storage.apply(dirs);
    }
}
