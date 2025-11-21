/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub Release Assets.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "coords", "rel" })
@SuppressWarnings({"PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
final class MkReleaseAssets implements ReleaseAssets {
    /**
     * XPath suffix for asset ID text.
     */
    private static final String ASSET_ID_XPATH = "/asset/id/text()";

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
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param number Release ID
     * @throws IOException If an IO Exception occurs
     * @checkstyle ParameterNumber (7 lines)
     */
    MkReleaseAssets(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int number
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.rel = number;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    // @checkstyle LineLengthCheck (1 line)
                    "/github/repos/repo[@coords='%s']/releases/release[id='%d']",
                    this.coords, this.rel
                )
            ).addIf("assets")
        );
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
    public Iterable<ReleaseAsset> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/asset"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public ReleaseAsset upload(
        final byte[] content,
        final String type,
        final String name
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat(MkReleaseAssets.ASSET_ID_XPATH)
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("asset")
                    .add("id").set(Integer.toString(number)).up()
                    .add("name").set(name).up()
                    .add("content").set(
                        DatatypeConverter.printBase64Binary(content)
                    ).up()
                    .add("content_type").set(type).up()
                    .add("size").set(Integer.toString(content.length)).up()
                    .add("download_count").set("42").up()
                    .add("created_at").set(new GitHub.Time().toString()).up()
                    .add("updated_at").set(new GitHub.Time().toString()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("html_url").set("http://localhost/2").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public ReleaseAsset get(final int number) {
        return new MkReleaseAsset(
            this.storage,
            this.self,
            this.coords,
            this.rel,
            number
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/releases/release[id='%d']/assets",
            this.coords, this.rel
        );
    }
}
