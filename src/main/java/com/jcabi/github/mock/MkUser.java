/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Notifications;
import com.jcabi.github.PublicKeys;
import com.jcabi.github.User;
import com.jcabi.github.UserEmails;
import com.jcabi.github.UserOrganizations;
import com.jcabi.xml.XML;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Date;
import lombok.ToString;

/**
 * GitHub user.
 *
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (8 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@SuppressWarnings("PMD.TooManyMethods")
final class MkUser implements User {

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
     */
    MkUser(final MkStorage stg, final String login) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public String login() {
        return this.self;
    }

    @Override
    public UserOrganizations organizations() {
        try {
            return new MkUserOrganizations(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public PublicKeys keys() {
        try {
            return new MkPublicKeys(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public UserEmails emails() {
        try {
            return new MkUserEmails(this.storage, this.self);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Notifications notifications() {
        return new MkNotifications(
            this.storage,
            this.xpath().concat("/notifications/notification")
        );
    }

    @Override
    public void markAsRead(final Date lastread) throws IOException {
        final Iterable<XML> ids = this.storage.xml().nodes(
            this.xpath().concat(
                String.format(
                    "/notifications/notification[date <= %s]/id",
                    lastread.getTime()
                )
            )
        );
        final JsonPatch json = new JsonPatch(this.storage);
        final JsonObject read = Json.createObjectBuilder()
            .add("read", true).build();
        for (final XML nid : ids) {
            json.patch(
                this.xpath().concat(
                    String.format(
                        "/notifications/notification[id = %s]",
                        nid.xpath("text()").get(0)
                    )
                ),
                read
            );
        }
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final MkUser other = (MkUser) obj;
            result = this.storage.equals(other.storage)
                && this.self.equals(other.self);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.self.hashCode();
        return result;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format("/github/users/user[login='%s']", this.self);
    }

}
