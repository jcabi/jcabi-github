/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock github public keys.
 *
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
        final MkStorage stg,
        final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath(this.userXpath()).addIf("keys")
        );
    }

    @Override
    public User user() {
        return new MkUser(this.storage, this.self);
    }

    @Override
    public Iterable<PublicKey> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/key", this.xpath()),
            xml -> this.get(
                Integer.parseInt(xml.xpath("id/text()").get(0))
            )
        );
    }

    @Override
    public PublicKey get(final int number) {
        return new MkPublicKey(this.storage, this.self, number);
    }

    @Override
    public PublicKey create(
        final String title,
        final String key
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
    private String userXpath() {
        return String.format("/github/users/user[login='%s']", this.self);
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format("%s/keys", this.userXpath());
    }
}
