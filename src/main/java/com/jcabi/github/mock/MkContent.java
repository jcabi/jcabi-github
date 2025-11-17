/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import jakarta.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Mock GitHub content.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords", "location", "branch" })
final class MkContent implements Content {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Path of this content.
     */
    private final transient String location;

    /**
     * Branch of this content.
     */
    private final transient String branch;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param path Path of this file
     * @param ref Branch of this file
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public MkContent(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final String path,
        final String ref
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.location = path;
        this.branch = ref;
    }

    @Override
    public int compareTo(
        final Content cont
    ) {
        return new CompareToBuilder()
            .append(this.path(), cont.path())
                .append(this.repo().coordinates(), cont.repo().coordinates())
            .build();
    }

    @Override
    public void patch(
        final JsonObject json)
        throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String path() {
        return this.location;
    }

    @Override
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            DatatypeConverter.parseBase64Binary(
                this.storage.xml().xpath(
                    String.format("%s/content/text()", this.xpath())
                ).get(0)
            )
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return The XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/contents/content[path='%s' and @ref='%s']",
            this.coords, this.location, this.branch
        );
    }
}
