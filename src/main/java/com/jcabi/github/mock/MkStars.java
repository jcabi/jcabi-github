/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Stars;
import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * GitHub starring API.
 * @since 0.15
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = {"storage", "self", "coords"})
final class MkStars implements Stars {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo's name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg The storage.
     * @param login The login name.
     * @param rep The Repository.
     * @throws IOException If something goes wrong.
     */
    MkStars(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath("/github/repos/repo")
                .addIf("stars")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public boolean starred() throws IOException {
        final List<String> xpath = this.storage.xml().xpath(
            String.format("%s/star/login/text()", this.xpath())
        );
        return !xpath.isEmpty()
            && StringUtils.equalsIgnoreCase(this.self, xpath.get(0));
    }

    @Override
    public void star() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("star").add("login")
                .set(this.self)
        );
    }

    @Override
    public void unstar() throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath())
                .xpath(String.format("star/login[.='%s']", this.self))
                .remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/stars",
            this.coords
        );
    }
}
