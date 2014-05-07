/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Issues;
import com.jcabi.github.Repo;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github issues.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkIssues implements Issues {

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
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep
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
    @NotNull(message = "repository can't be NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "Issue is never NULL")
    public Issue get(final int number) {
        return new MkIssue(this.storage, this.self, this.coords, number);
    }

    @Override
    @NotNull(message = "created issue is never NULL")
    public Issue create(@NotNull(message = "")
        final String title,
        @NotNull(message = "body is cannot be NULL") final String body
    )
        throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/issue/number/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("issue")
                    .add("number").set(Integer.toString(number)).up()
                    .add("state").set(Issue.OPEN_STATE).up()
                    .add("title").set(title).up()
                    .add("body").set(body).up()
                    .add("created_at").set(new Github.Time().toString()).up()
                    .add("updated_at").set(new Github.Time().toString()).up()
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
    @NotNull(message = "Iterable of issues is never NULL")
    public Iterable<Issue> iterate(@NotNull(message = "params can't be NULL")
        final Map<String, String> params
    ) {
        return new MkIterable<Issue>(
            this.storage,
            String.format("%s/issue", this.xpath()),
            new MkIterable.Mapping<Issue>() {
                @Override
                public Issue map(final XML xml) {
                    return MkIssues.this.get(
                        Integer.parseInt(xml.xpath("number/text()").get(0))
                    );
                }
            }
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues",
            this.coords
        );
    }

}
