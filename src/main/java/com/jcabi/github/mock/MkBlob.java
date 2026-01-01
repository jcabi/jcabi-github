/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Blob;
import com.jcabi.github.Coordinates;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.ToString;

/**
 * Mock GitHub Blob.
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
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

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final MkBlob other = (MkBlob) obj;
            result = this.storage.equals(other.storage)
                && this.coords.equals(other.coords)
                && this.hash.equals(other.hash);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.coords.hashCode();
        result = 31 * result + this.hash.hashCode();
        return result;
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
