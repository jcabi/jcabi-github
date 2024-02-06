/**
 * Copyright (c) 2013-2024, jcabi.com
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
import com.jcabi.github.Comment;
import com.jcabi.github.Comments;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github comments.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "repo", "ticket" })
final class MkComments implements Comments {

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
     * @param login User to login
     * @param rep Repo
     * @param issue Issue number
     * @throws IOException If there is any I/O problem
     * @checkstyle ParameterNumber (5 lines)
     */
    MkComments(
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
                    this.repo, this.ticket
                )
            ).addIf("comments")
        );
    }

    @Override
    public Issue issue() {
        return new MkIssue(this.storage, this.self, this.repo, this.ticket);
    }

    @Override
    public Comment get(final int number) {
        return new MkComment(
            this.storage, this.self, this.repo, this.ticket, number
        );
    }

    @Override
    public Iterable<Comment> iterate(final Date since) {
        return new MkIterable<>(
            this.storage,
            String.format("%s/comment", this.xpath()),
            xml -> this.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
        );
    }

    @Override
    public Comment post(
        final String text
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            final String timestamp = new Github.Time().toString();
            number = 1 + this.storage.xml()
                .nodes("//comment/number").size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("comment")
                    .add("number").set(Integer.toString(number)).up()
                    .add("url")
                    .set(
                        String.format(
                            // @checkstyle LineLength (1 line)
                            "https://api.jcabi-github.invalid/repos/%s/%s/issues/comments/%d",
                            this.repo.user(),
                            this.repo.repo(),
                            number
                    )
                )
                    .up()
                    .add("body").set(text).up()
                    .add("user")
                        .add("login").set(this.self).up()
                    .up()
                    .add("created_at").set(timestamp).up()
                    .add("updated_at").set(timestamp)
            );
        } finally {
            this.storage.unlock();
        }
        Logger.info(
            this, "comment #%d posted to issue #%d by %s: %[text]s",
            number, this.issue().number(), this.self, text
        );
        return this.get(number);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/comments",
            this.repo, this.ticket
        );
    }

}
