/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.UserEmails;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub User Emails.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkUserEmails implements UserEmails {

    /**
     * Mapping.
     */
    private static final MkIterable.Mapping<String> MAPPING =
        xml -> xml.xpath("./text()").get(0);

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
    MkUserEmails(
        final MkStorage stg,
        final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath(this.userXpath()).addIf("emails")
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public Iterable<String> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/email"),
            MkUserEmails.MAPPING
        );
    }

    @Override
    public Iterable<String> add(
        final Iterable<String> emails
    ) throws IOException {
        this.storage.lock();
        try {
            final Directives directives = new Directives().xpath(this.xpath());
            for (final String email : emails) {
                directives.add("email").set(email).up();
            }
            this.storage.apply(directives);
        } finally {
            this.storage.unlock();
        }
        return emails;
    }

    @Override
    public void remove(
        final Iterable<String> emails
    ) throws IOException {
        final Directives directives = new Directives();
        for (final String email : emails) {
            directives.xpath(
                this.xpath().concat(String.format("/email[.='%s']", email))
            ).remove();
        }
        this.storage.apply(directives);
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    private String userXpath() {
        return String.format("/github/users/user[login='%s']", this.self);
    }

    /**
     * XPath of user emails element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format("%s/emails", this.userXpath());
    }
}
