/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub Fork.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
final class MkFork implements Fork {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Repository coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Release id.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param number Fork id
     * @param repo Repo name
     */
    MkFork(
        final MkStorage stg,
        final int number,
        final Coordinates repo
    ) {
        this.storage = stg;
        this.num = number;
        this.coords = repo;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
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
            final MkFork other = (MkFork) obj;
            result = this.num == other.num
                && this.storage.equals(other.storage)
                && this.coords.equals(other.coords);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.coords.hashCode();
        result = 31 * result + this.num;
        return result;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/forks/fork[id='%d']",
            this.coords, this.num
        );
    }
}
