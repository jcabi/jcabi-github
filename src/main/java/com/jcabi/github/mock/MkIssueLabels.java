/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Label;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub labels.
 *
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "repo", "ticket" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class MkIssueLabels implements IssueLabels {

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
    private final transient Coordinates repo;

    /**
     * Issue number.
     */
    private final transient int ticket;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login Login
     * @param rep Repo
     * @param issue Issue number
     * @throws IOException If fails
     * @checkstyle ParameterNumber (5 lines)
     */
    MkIssueLabels(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int issue
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.repo = rep;
        this.ticket = issue;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "/github/repos/repo[@coords='%s']/issues/issue[number='%d']",
                    rep, this.ticket
                )
            ).addIf("labels")
        );
    }

    @Override
    public Issue issue() {
        return new MkIssue(this.storage, this.self, this.repo, this.ticket);
    }

    @Override
    public void add(final Iterable<String> labels
    ) throws IOException {
        final Collection<String> existing = this.labels();
        final Set<String> added = new HashSet<>();
        final Directives dirs = new Directives().xpath(this.xpath());
        for (final String label : labels) {
            dirs.add("label").set(label).up();
            if (!existing.contains(label)) {
                added.add(label);
            }
        }
        this.storage.apply(dirs);
        if (!added.isEmpty()) {
            final MkIssueEvents events = new MkIssueEvents(
                this.storage,
                this.self,
                this.repo
            );
            for (final String label : added) {
                events.create(
                    Event.LABELED,
                    this.ticket,
                    this.self,
                    Optional.of(label)
                );
            }
        }
    }

    @Override
    public void replace(final Iterable<String> labels
    ) throws IOException {
        this.clear();
        this.add(labels);
    }

    @Override
    public Iterable<Label> iterate() {
        return new MkIterable<>(
            this.storage,
            this.xpath().concat("/*"),
            xml -> new MkLabel(
                this.storage,
                this.self,
                this.repo,
                xml.xpath("./text()").get(0)
            )
        );
    }

    @Override
    public void remove(final String name
    ) throws IOException {
        if (this.labels().contains(name)) {
            this.storage.apply(
                new Directives().xpath(
                    this.xpath().concat(String.format("/label[.='%s']", name))
                ).remove()
            );
            new MkIssueEvents(
                this.storage,
                this.self,
                this.repo
            ).create(
                Event.UNLABELED,
                this.ticket,
                this.self,
                Optional.of(name)
            );
        }
    }

    @Override
    public void clear() throws IOException {
        for (final String label : this.labels()) {
            this.remove(label);
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/labels",
            this.repo, this.ticket
        );
    }

    /**
     * Returns a set of all of the issue's labels.
     * @return Set of label names
     */
    private Collection<String> labels() {
        final Set<String> labels = new HashSet<>();
        for (final Label label : this.iterate()) {
            labels.add(label.name());
        }
        return labels;
    }
}
