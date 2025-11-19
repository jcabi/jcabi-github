/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.PublicKey;
import com.jcabi.github.User;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock GitHub public key.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "num" })
final class MkPublicKey implements PublicKey {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Issue number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param number Key number
     */
    MkPublicKey(
        final MkStorage stg,
        final String login,
        final int number
    ) {
        this.storage = stg;
        this.self = login;
        this.num = number;
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
    public User user() {
        return new MkUser(this.storage, this.self);
    }

    @Override
    public int number() {
        return this.num;
    }

    /**
     * XPath of this element in XML tree.
     *
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/users/user[login='%s']/keys/key[id='%d']",
            this.self,
            this.num
        );
    }
}
