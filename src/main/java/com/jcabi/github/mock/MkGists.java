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
import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import com.jcabi.github.Github;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github gists.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkGists implements Gists {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkGists(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath("/github").addIf("gists")
        );
    }

    @Override
    @NotNull(message = "Github is never NULL")
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    @NotNull(message = "created gist is never NULL")
    public Gist create(
        @NotNull(message = "map of files can't be NULL")
        final Map<String, String> files, final boolean visible
    ) throws IOException {
        this.storage.lock();
        final String number;
        try {
            number = Integer.toString(
                1 + this.storage.xml().xpath(
                    String.format("%s/gist/id/text()", this.xpath())
                ).size()
            );
            final Directives dirs = new Directives().xpath(this.xpath())
                .add("gist")
                .add("id").set(number).up()
                .add("public").set(String.valueOf(visible)).up()
                .add("files");
            for (final Map.Entry<String, String> file : files.entrySet()) {
                dirs.add("file")
                    .add("filename").set(file.getKey()).up()
                    .add("raw_content").set(file.getValue()).up().up();
            }
            this.storage.apply(dirs);
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    @NotNull(message = "gist is never NULL")
    public Gist get(@NotNull(message = "name can't be NULL")
        final String name
    ) {
        return new MkGist(this.storage, this.self, name);
    }

    @Override
    @NotNull(message = "Iterable of gists is never NULL")
    public Iterable<Gist> iterate() {
        return new MkIterable<Gist>(
            this.storage,
            String.format("%s/gist", this.xpath()),
            new MkIterable.Mapping<Gist>() {
                @Override
                public Gist map(final XML xml) {
                    return MkGists.this.get(xml.xpath("id/text()").get(0));
                }
            }
        );
    }

    @Override
    public void remove(@NotNull(message = "identifier should not be NULL")
        final String identifier
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/gist[id='%s']", this.xpath(), identifier)
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return "/github/gists";
    }

}
