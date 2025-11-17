/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.User;
import com.jcabi.github.Users;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub users.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "himself" })
final class MkUsers implements Users {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String himself;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkUsers(
        final MkStorage stg,
        final String login
    ) throws IOException {
        this.storage = stg;
        this.himself = login;
        this.storage.apply(
            new Directives().xpath("/github").addIf("users")
        );
        this.add(login);
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.himself);
    }

    @Override
    public User self() {
        return this.get(this.himself);
    }

    @Override
    public User get(
        final String login
    ) {
        return new MkUser(this.storage, login);
    }

    @Override
    public Iterable<User> iterate(
        final String identifier
    ) {
        return new MkIterable<>(
            this.storage,
            "/github/users/user",
            xml -> this.get(xml.xpath("login/text()").get(0))
        );
    }

    @Override
    public User add(final String login) {
        try {
            this.storage.apply(
                new Directives()
                    .xpath(
                        String.format(
                            "/github/users[not(user[login='%s'])]", login
                        )
                    )
                    .add("user")
                    .add("login").set(login).up()
                    .add("type").set("User").up()
                    .add("name").set(login).up()
                    .add("notifications").up()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return this.get(login);
    }

}
