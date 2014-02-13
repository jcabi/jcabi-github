/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github pull comments.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/pulls/comments/">Review Comments API</a>
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "storage", "self", "repo", "owner" })
public final class MkPullComments implements PullComments {
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
     * Owner of comments.
     */
    private final transient Pull owner;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param pull Pull
     * @throws IOException If there is any I/O problem
     * @checkstyle ParameterNumber (5 lines)
     */
    MkPullComments(final MkStorage stg, final String login,
        final Coordinates rep, final Pull pull) throws IOException {
        this.storage = stg;
        this.self = login;
        this.repo = rep;
        this.owner = pull;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']",
                    this.repo, this.owner.number()
                )
            ).addIf("comments")
        );
    }
    @Override
    public Pull pull() {
        return this.owner;
    }

    @Override
    public PullComment get(final int number) {
        return new MkPullComment(this.storage, this.repo, this.owner, number);
    }

    @Override
    public Iterable<PullComment> iterate(final Map<String, String> params) {
        //@checkstyle MultipleStringLiteralsCheck (1 line)
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    @Override
    public Iterable<PullComment> iterate(final int number,
        final Map<String, String> params) {
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    // @checkstyle ParameterNumberCheck (3 lines)
    @Override
    public PullComment post(final String body, final String commit,
        final String path, final int position) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml()
                .nodes(String.format("%s/comment/id/text()", this.xpath()))
                .size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("comment")
                    .add("id").set(Integer.toString(number)).up()
                    .add("url").set("http://localhost/1").up()
                    .add("diff_hunk").set("@@ -16,33 +16,40 @@ public...").up()
                    .add("path").set("file1.txt").up()
                    .add("position").set(Integer.toString(number)).up()
                    .add("original_position").set(Integer.toString(number)).up()
                    .add("commit_id").set(commit).up()
                    .add("original_commit_id").set(commit).up()
                    .add("body").set(body).up()
                    .add("created_at").set(new Github.Time().toString()).up()
                    .add("published_at").set(new Github.Time().toString()).up()
                    .add("user").add("login").set(this.self).up()
                    .add("pull_request_url").set("http://localhost/2").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public PullComment reply(final String body,
        final int comment) throws IOException {
        throw new UnsupportedOperationException("Reply not yet implemented.");
    }

    @Override
    public void remove(final int number) throws IOException {
        throw new UnsupportedOperationException("Remove not yet implemented.");
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/comments",
            this.repo, this.owner.number()
        );
    }
}
