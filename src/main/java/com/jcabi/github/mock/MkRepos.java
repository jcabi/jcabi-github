/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.log.Logger;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * GitHub repos.
 *
 * @since 0.5
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class MkRepos implements Repos {

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
    MkRepos(
        final MkStorage stg,
        final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(new Directives().xpath("/github").addIf("repos"));
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public Repo create(
        final Repos.RepoCreate settings
    ) throws IOException {
        String owner = this.self;
        final String org = settings.organization();
        if (org != null && !org.isEmpty()) {
            owner = "/orgs/".concat(org).concat("/repos");
        }
        final Coordinates coords = new Coordinates.Simple(
            owner,
            settings.name()
        );
        this.storage.apply(
            new Directives().xpath(MkRepos.xpath()).add("repo")
                .attr("coords", coords.toString())
                .add("name").set(settings.name()).up()
                .add("description").set("test repository").up()
                .add("private").set(settings.isPrivate()).up()
        );
        final Repo repo = this.get(coords);
        repo.patch(settings.json());
        Logger.info(
            this, "repository %s created by %s",
            coords, owner
        );
        return repo;
    }

    @Override
    public Repo get(
        final Coordinates coords
    ) {
        try {
            final String xpath = String.format(
                "%s/repo[@coords='%s']", MkRepos.xpath(), coords
            );
            if (this.storage.xml().nodes(xpath).isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("repository %s doesn't exist", coords)
                );
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return new MkRepo(this.storage, this.self, coords);
    }

    @Override
    public void remove(
        final Coordinates coords) {
        try {
            this.storage.apply(
                new Directives().xpath(
                    String.format("%s/repo[@coords='%s']", MkRepos.xpath(), coords)
                ).remove()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Iterable<Repo> iterate(
        final String identifier) {
        return new MkIterable<>(
            this.storage,
            "/github/repos/repo",
            xml -> new MkRepo(
                this.storage, this.self,
                new Coordinates.Simple(xml.xpath("@coords").get(0))
            )
        );
    }

    @Override
    public boolean exists(final Coordinates coords) throws IOException {
        final String xpath = String.format(
            "%s/repo[@coords='%s']", MkRepos.xpath(), coords
        );
        return !this.storage.xml().nodes(xpath).isEmpty();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private static String xpath() {
        return "/github/repos";
    }

}
