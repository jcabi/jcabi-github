/**
 * Copyright (c) 2013-2014, JCabi.com
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
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Release;
import com.jcabi.github.ReleaseAsset;
import com.jcabi.github.ReleaseAssets;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github Release Assets.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "coords", "rel" })
public final class MkReleaseAssets implements ReleaseAssets {
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
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep,
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
    @NotNull(message = "release is never NULL")
    public Release release() {
        return new MkRelease(
            this.storage,
            this.self,
            this.coords,
            this.rel
        );
    }

    @Override
    @NotNull(message = "Iterable is never NULL")
    public Iterable<ReleaseAsset> iterate() {
        return new MkIterable<ReleaseAsset>(
            this.storage,
            String.format("%s/asset", this.xpath()),
            new MkIterable.Mapping<ReleaseAsset>() {
                @Override
                public ReleaseAsset map(final XML xml) {
                    return MkReleaseAssets.this.get(
                        Integer.parseInt(xml.xpath("id/text()").get(0))
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "Asset is never NULL")
    public ReleaseAsset upload(final byte[] content,
        @NotNull(message = "type can't be NULL") final String type,
        @NotNull(message = "name can't be NULL") final String name
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/asset/id/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("asset")
                    .add("id").set(Integer.toString(number)).up()
                    .add("name").set(name).up()
                    .add("content").set(new String(content, "UTF-8")).up()
                    .add("content_type").set(type).up()
                    .add("size").set(Integer.toString(content.length)).up()
                    .add("download_count").set("42").up()
                    .add("created_at").set(new Github.Time().toString()).up()
                    .add("updated_at").set(new Github.Time().toString()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("html_url").set("http://localhost/2").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    @NotNull(message = "Asset can't be NULL")
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
    @NotNull(message = "Xpath si never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/releases/release[id='%d']/assets",
            this.coords, this.rel
        );
    }
}
