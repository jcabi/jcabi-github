/**
 * Copyright (c) 2013-2020, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "repo", "ticket" })
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
        final Set<String> added = new HashSet<String>();
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
        return new MkIterable<Label>(
            this.storage,
            String.format("%s/*", this.xpath()),
            new MkIterable.Mapping<Label>() {
                @Override
                public Label map(final XML xml) {
                    return new MkLabel(
                        MkIssueLabels.this.storage,
                        MkIssueLabels.this.self,
                        MkIssueLabels.this.repo,
                        xml.xpath("./text()").get(0)
                    );
                }
            }
        );
    }

    @Override
    public void remove(final String name
    ) throws IOException {
        if (this.labels().contains(name)) {
            this.storage.apply(
                new Directives().xpath(
                    String.format("%s/label[.='%s']", this.xpath(), name)
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
        final Set<String> labels = new HashSet<String>();
        for (final Label label : this.iterate()) {
            labels.add(label.name());
        }
        return labels;
    }
}
