/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.DeployKey;
import com.jcabi.github.Repo;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub deploy key.
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
final class MkDeployKey implements DeployKey {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Id.
     */
    private final transient int key;

    /**
     * Public ctor.
     * @param stg Storage
     * @param number Id
     * @param repo Repository
     */
    MkDeployKey(
        final MkStorage stg,
        final int number,
        final Repo repo
    ) {
        this.storage = stg;
        this.key = number;
        this.owner = repo;
    }

    @Override
    public int number() {
        return this.key;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void remove() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).strict(1).remove()
        );
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final MkDeployKey other = (MkDeployKey) obj;
            result = this.key == other.key
                && this.storage.equals(other.storage)
                && this.owner.equals(other.owner);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.owner.hashCode();
        result = 31 * result + this.key;
        return result;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/deploykeys/deploykey[id='%d']",
            this.owner.coordinates(),
            this.key
        );
    }

}
