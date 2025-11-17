/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * Json patch.
 *
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
