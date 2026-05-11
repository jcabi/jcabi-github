/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Commit;
import com.jcabi.github.Commits;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Statuses;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of GitHub Commits.
 * @since 0.3
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
public final class MkCommits implements Commits {

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
     * Public constructor.
     * @param stg The storage.
     * @param login The login name.
     * @param rep Repo's coordinates.
     * @throws IOException If something goes wrong.
     */
    MkCommits(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/git",
                    this.coords
                )
            ).addIf("commits")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Commit create(
        final JsonObject params
    ) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath()).add("commit");
        for (final Map.Entry<String, JsonValue> entry : params.entrySet()) {
            final JsonValue value = entry.getValue();
            if (value.getValueType() == JsonValue.ValueType.ARRAY) {
                dirs.add(entry.getKey()).attr("array", "true");
                for (final JsonValue item : value.asJsonArray()) {
                    dirs.add("item").set(MkCommits.asText(item)).up();
                }
                dirs.up();
            } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                dirs.add(entry.getKey());
                for (final Map.Entry<String, JsonValue> sub
                    : value.asJsonObject().entrySet()) {
                    dirs.add(sub.getKey()).set(MkCommits.asText(sub.getValue())).up();
                }
                dirs.up();
            } else {
                dirs.add(entry.getKey()).set(MkCommits.asText(value)).up();
            }
        }
        this.storage.apply(dirs);
        return this.get(params.getString("sha"));
    }

    @Override
    public Commit get(
        final String sha
    ) {
        return new MkCommit(this.storage, this.self, this.coords, sha);
    }

    @Override
    public Statuses statuses(
        final String sha
    ) {
        return new MkStatuses(this.get(sha));
    }

    /**
     * XPath of the commits element in the storage XML.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/commits",
            this.coords
        );
    }

    /**
     * Render JSON value as plain text suitable for XML storage.
     * @param value JSON value
     * @return Plain string
     */
    private static String asText(final JsonValue value) {
        final String result;
        if (value.getValueType() == JsonValue.ValueType.STRING) {
            result = ((JsonString) value).getString();
        } else {
            result = value.toString();
        }
        return result;
    }
}
