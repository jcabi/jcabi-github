/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Blob;
import com.jcabi.github.Coordinates;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github Blob.
 *
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "coords", "hash" })
final class MkBlob implements Blob {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Repository coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Blob SHA hash.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param stg Storage
     * @param sha Blob sha
     * @param repo Repo name
     */
    MkBlob(
        final MkStorage stg,
        final String sha,
        final Coordinates repo) {
        this.storage = stg;
        this.hash = sha;
        this.coords = repo;
    }

    @Override
    public String sha() {
        return this.hash;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/blobs/blob[sha='%s']",
            this.coords, this.sha()
        );
    }

}
