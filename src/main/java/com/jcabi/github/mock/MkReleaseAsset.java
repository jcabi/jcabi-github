/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Charsets;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import jakarta.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub release asset.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "coords", "rel", "num" })
final class MkReleaseAsset implements ReleaseAsset {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repository coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Release id.
     */
    private final transient int rel;

    /**
     * The asset id.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param release Release ID
     * @param asset Asset ID
     * @checkstyle ParameterNumber (7 lines)
     */
    MkReleaseAsset(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int release,
        final int asset
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.rel = release;
        this.num = asset;
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
    public Release release() {
        return new MkRelease(
            this.storage,
            this.self,
            this.coords,
            this.rel
        );
    }

    @Override
    public int number() {
        return this.num;
    }

    /**
     * Remove asset.
     *
     * @throws IOException If there is any I/O problem
     */
    @Override
    public void remove() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).strict(1).remove()
        );
    }

    /**
     * Get raw release asset content.
     *
     * @see <a href="https://developer.github.com/v3/repos/releases/">Releases API</a>
     * @return Stream with content
     * @throws IOException If some problem inside.
     */
    @Override
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            this.storage.xml().xpath(
                String.format("%s/content/text()", this.xpath())
            ).get(0).getBytes(Charsets.UTF_8)
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/releases/release[id='%d']/assets/asset[id='%d']",
            this.coords, this.rel, this.num
        );
    }
}
