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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import com.jcabi.github.Forks;
import com.jcabi.github.Repo;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock Github forks.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkForks implements Forks {

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
    public MkForks(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep
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
            ).addIf("forks")
        );
    }
    @Override
    @NotNull(message = "Repo can't be NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }
    /**
     * Gets a mocked Fork.
     * @param forkid Fork id
     * @return Mocked Fork
     */
    @NotNull(message = "fork can't be NULL")
    public Fork get(final int forkid) {
        return new MkFork(this.storage, forkid, this.coords);
    }
    @Override
    @NotNull(message = "Iterable of forks can't be NULL")
    public Iterable<Fork> iterate(
        @NotNull(message = "sort can't be NULL") final String sort
    ) {
        return new MkIterable<Fork>(
            this.storage,
            String.format("%s/fork", this.xpath()),
            new MkIterable.Mapping<Fork>() {
                @Override
                public Fork map(final XML xml) {
                    return MkForks.this.get(
                        Integer.parseInt(xml.xpath("id/text()").get(0))
                    );
                }
            }
        );
    }
    @Override
    @NotNull(message = "created fork is never NULLs")
    public Fork create(
        @NotNull(message = "org can't be NULL") final String org
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/fork/id/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("fork")
                    .add("id").set(Integer.toString(number)).up()
                    .attr("organization", org)
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "fork %s created inside %s by %s",
            this.coords, org, this.self
        );
        return this.get(number);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/forks",
            this.coords
        );
    }
}
