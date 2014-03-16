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
import com.jcabi.github.Label;
import com.jcabi.github.Labels;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkLabels implements Labels {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    MkLabels(@NotNull(message = "stg cannot be NULL")
        final MkStorage stg,
        @NotNull(message = "login cannot be NULL") final String login,
        @NotNull(message = "rep cannot be NULL") final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("labels")
        );
    }

    @Override
    @NotNull(message = "Repository is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "Label is never NULL")
    public Label get(@NotNull(message = "name cannot be NULL")
        final String name
    ) {
        return new MkLabel(this.storage, this.self, this.coords, name);
    }

    @Override
    @NotNull(message = "Created label is never NULL")
    public Label create(@NotNull(message = "name can't be NULL")
        final String name,
        @NotNull(message = "color can't be NULL") final String color
    ) throws IOException {
        if (!color.matches("[0-9a-f]{6}")) {
            throw new IllegalArgumentException(
                String.format(
                    "color '%s' is in wrong format, six hex letters expected",
                    color
                )
            );
        }
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("label")
                .add("name").set(name).up()
                .add("color").set(color).up()
        );
        return this.get(name);
    }

    @Override
    @NotNull(message = "iterable of labels is never NULL")
    public Iterable<Label> iterate() {
        return new MkIterable<Label>(
            this.storage,
            String.format("%s/label", this.xpath()),
            new MkIterable.Mapping<Label>() {
                @Override
                public Label map(final XML xml) {
                    return MkLabels.this.get(
                        xml.xpath("name/text()").get(0)
                    );
                }
            }
        );
    }

    @Override
    public void delete(@NotNull(message = "name should not be NULL")
        final String name
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath())
                .xpath(String.format("label[name='%s']", name))
                .remove()
                .xpath("/github/repos")
                .xpath(String.format("repo[@coords='%s']", this.coords))
                .xpath(String.format("issues/issue/labels/label[.='%s']", name))
                .remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/labels",
            this.coords
        );
    }
}
