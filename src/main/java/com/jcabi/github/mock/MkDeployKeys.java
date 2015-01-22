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
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.DeployKey;
import com.jcabi.github.DeployKeys;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github deploy keys.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkDeployKeys implements DeployKeys {

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
    MkDeployKeys(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", this.coords)
            ).addIf("deploykeys")
        );
    }

    @Override
    @NotNull(message = "repo is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "Iterable of deploy keys is never NULL")
    public Iterable<DeployKey> iterate() {
        return Collections.emptyList();
    }

    @Override
    @NotNull(message = "deploy key is never NULL")
    public DeployKey get(final int number) {
        return new MkDeployKey(this.storage, number, this.repo());
    }

    @Override
    @NotNull(message = "created key is never NULL")
    public DeployKey create(
        @NotNull(message = "title can't be NULL") final String title,
        @NotNull(message = "key can't be NULL") final String key
    )
        throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/deploykey/id/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath())
                    .add("deploykey")
                    .add("id").set(String.valueOf(number)).up()
                    .add("title").set(title).up()
                    .add("key").set(key)
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/deploykeys",
            this.coords
        );
    }

}
