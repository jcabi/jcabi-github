/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Label;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
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
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep,
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
    @NotNull(message = "Issue is never NULL")
    public Issue issue() {
        return new MkIssue(this.storage, this.self, this.repo, this.ticket);
    }

    @Override
    public void add(@NotNull(message = "labels can't be NULL")
        final Iterable<String> labels
    ) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath());
        for (final String label : labels) {
            dirs.add("label").set(label).up();
        }
        this.storage.apply(dirs);
    }

    @Override
    public void replace(@NotNull(message = "labels should not be NULL")
        final Iterable<String> labels
    ) throws IOException {
        this.clear();
        this.add(labels);
    }

    @Override
    @NotNull(message = "Iterable of labels is never NULL")
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
    public void remove(@NotNull(message = "name cannpt be NULL")
        final String name
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/label[.='%s']", this.xpath(), name)
            ).remove()
        );
    }

    @Override
    public void clear() throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/label", this.xpath())
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/labels",
            this.repo, this.ticket
        );
    }

}
