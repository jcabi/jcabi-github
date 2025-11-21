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
import lombok.ToString;

/**
 * Mock GitHub public key.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
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

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final MkPublicKey other = (MkPublicKey) obj;
            result = this.num == other.num
                && this.storage.equals(other.storage)
                && this.self.equals(other.self);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.self.hashCode();
        result = 31 * result + this.num;
        return result;
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
