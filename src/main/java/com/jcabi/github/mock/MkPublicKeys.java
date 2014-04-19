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
import com.jcabi.github.PublicKey;
import com.jcabi.github.PublicKeys;
import com.jcabi.github.User;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock github public keys.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkPublicKeys implements PublicKeys {

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
    public MkPublicKeys(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath(this.userXpath()).addIf("keys")
        );
    }

    @Override
    @NotNull(message = "User is never NULL")
    public User user() {
        try {
            return new MkUser(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    @NotNull(message = "Iterable of keys is never NULL")
    public Iterable<PublicKey> iterate() {
        return new MkIterable<PublicKey>(
            this.storage,
            String.format("%s/key", this.xpath()),
            new MkIterable.Mapping<PublicKey>() {
                @Override
                public PublicKey map(final XML xml) {
                    return MkPublicKeys.this.get(
                        Integer.parseInt(xml.xpath("id/text()").get(0))
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "key is never NULL")
    public PublicKey get(final int number) {
        return new MkPublicKey(this.storage, this.self, number);
    }

    @Override
    @NotNull(message = "created public key is never NULL")
    public PublicKey create(
        @NotNull(message = "title cannot be NULL") final String title,
        @NotNull(message = "key cannot be NULL") final String key
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/key/id/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath())
                    .add("key")
                    .add("id").set(String.valueOf(number)).up()
                    .add("title").set(title).up()
                    .add("key").set(key)
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/key[id='%d']", this.xpath(), number)
            ).remove()
        );
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath of the user is never NULL")
    private String userXpath() {
        return String.format("/github/users/user[login='%s']", this.self);
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format("%s/keys", this.userXpath());
    }
}
