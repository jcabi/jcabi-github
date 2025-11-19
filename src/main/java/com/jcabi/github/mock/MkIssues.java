/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.GitHub;
import com.jcabi.github.Issue;
import com.jcabi.github.Issues;
import com.jcabi.github.Repo;
import com.jcabi.github.Search;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub issues.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkIssues implements Issues {

    /**
     * XPath suffix for issue number text.
     */
    private static final String ISSUE_NUMBER_TEXT_PATH = "/issue/number/text()";

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
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    MkIssues(
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
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("issues")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Issue get(final int number) {
        return new MkIssue(this.storage, this.self, this.coords, number);
    }

    @Override
    public Issue create(final String title,
        final String body
    )
        throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                this.xpath().concat(MkIssues.ISSUE_NUMBER_TEXT_PATH)
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("issue")
                    .add("number").set(Integer.toString(number)).up()
                    .add("state").set(Issue.OPEN_STATE).up()
                    .add("title").set(title).up()
                    .add("body").set(body).up()
                    .add("created_at").set(new GitHub.Time().toString()).up()
                    .add("updated_at").set(new GitHub.Time().toString()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("html_url").set("http://localhost/2").up()
                    .add("user").add("login").set(this.self).up().up()
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "issue #%d created in %s by %s: %[text]s",
            number, this.repo().coordinates(), this.self, title
        );
        return this.get(number);
    }

    @Override
    public Iterable<Issue> iterate(final Map<String, String> params
    ) {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/issue"),
            xml -> this.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
        );
    }

    @Override
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public Iterable<Issue> search(
        final Issues.Sort sort,
        final Search.Order direction,
        final EnumMap<Issues.Qualifier, String> qualifiers) {
        final Map<String, String> params = new HashMap<>();
        for (final EnumMap.Entry<Issues.Qualifier, String> entry : qualifiers
            .entrySet()) {
            params.put(entry.getKey().identifier(), entry.getValue());
        }
        params.put("sort", sort.identifier());
        params.put("direction", direction.identifier());
        return this.iterate(params);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues",
            this.coords
        );
    }

}
