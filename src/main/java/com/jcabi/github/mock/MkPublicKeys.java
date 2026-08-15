/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkPublicKeys implements PublicKeys {

    /**
     * XPath suffix for key ID text.
     */
    private static final String KEY_ID_TEXT_PATH = "/key/id/text()";

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
    MkPublicKeys(final MkStorage stg, final String login) throws IOException {
        this(login, MkPublicKeys.bootstrap(stg, login));
    }

    private MkPublicKeys(final String login, final MkStorage stg) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public User user() {
        return new MkUser(this.storage, this.self);
    }

    @Override
    public Iterable<PublicKey> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/key"),
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
                this.xpath().concat(MkPublicKeys.KEY_ID_TEXT_PATH)
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
                this.xpath().concat(String.format("/key[id='%d']", number))
            ).remove()
        );
    }

    /**
     * XPath of user element in XML tree.
     * @param login User login
     * @return XPath
     */
    private static String userXpath(final String login) {
        return String.format("/github/users/user[login='%s']", login);
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format("%s/keys", MkPublicKeys.userXpath(this.self));
    }

    /**
     * Prepare the storage.
     * @param stg Storage
     * @param login String
     * @return The same storage
     * @throws IOException If fails
     */
    private static MkStorage bootstrap(final MkStorage stg, final String login) throws IOException {
        stg.apply(
            new Directives().xpath(MkPublicKeys.userXpath(login)).addIf("keys")
        );
        return stg;
    }
}
