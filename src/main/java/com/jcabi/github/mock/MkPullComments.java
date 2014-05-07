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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
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
final class MkPullComments implements PullComments {
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
    MkPullComments(
        @NotNull(message = "stg is never NULL") final MkStorage stg,
        @NotNull(message = "login is never NULL") final String login,
        @NotNull(message = "rep is never NULL") final Coordinates rep,
        @NotNull(message = "pull is never NULL") final Pull pull
    ) throws IOException {
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
    @NotNull(message = "pull is never NULL")
    public Pull pull() {
        return this.owner;
    }

    @Override
    @NotNull(message = "pull comment is never NULL")
    public PullComment get(final int number) {
        return new MkPullComment(this.storage, this.repo, this.owner, number);
    }

    @Override
    @NotNull(message = "Iterable of pull comments is never NULL")
    public Iterable<PullComment> iterate(
        @NotNull(message = "params is never NULL")
        final Map<String, String> params
    ) {
        return new MkIterable<PullComment>(
            this.storage,
            String.format(
                "/github/repos/repo[@coords='%s']/pulls/pull/comments",
                this.repo
            ),
            new MkIterable.Mapping<PullComment>() {
                @Override
                public PullComment map(final XML xml) {
                    return MkPullComments.this.get(
                        Integer.parseInt(xml.xpath("comment/id/text()").get(0))
                    );
                }
            }
        );
    }

    @Override
    @NotNull(message = "Iterable is never NULL")
    public Iterable<PullComment> iterate(
        final int number,
        @NotNull(message = "params cannot be NULL")
        final Map<String, String> params
    ) {
        return new MkIterable<PullComment>(
            this.storage, String.format("%s/comment", this.xpath()),
            new MkIterable.Mapping<PullComment>() {
                @Override
                public PullComment map(final XML xml) {
                    return MkPullComments.this.get(
                        Integer.parseInt(xml.xpath("id/text()").get(0))
                    );
                }
            }
        );
    }

    // @checkstyle ParameterNumberCheck (7 lines)
    @Override
    @NotNull(message = "pull comment isn't ever NULL")
    public PullComment post(
        @NotNull(message = "body can't be NULL") final String body,
        @NotNull(message = "commit can't be NULL") final String commit,
        @NotNull(message = "path can't be NULL") final String path,
        final int position
    ) throws IOException {
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
                    // @checkstyle MultipleStringLiteralsCheck (4 lines)
                    .add("path").set(path).up()
                    .add("position").set(Integer.toString(position)).up()
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
    @NotNull(message = "reply is never NULL")
    public PullComment reply(
        @NotNull(message = "body can't be NULL") final String body,
        final int comment
    )
        throws IOException {
        this.storage.lock();
        try {
            final JsonObject orig = this.get(comment).json();
            final PullComment reply = this.post(
                body,
                orig.getString("commit_id"),
                orig.getString("path"),
                comment
            );
            reply.patch(
                Json.createObjectBuilder()
                    .add("original_position", String.valueOf(comment)).build()
            );
            return reply;
        } finally {
            this.storage.unlock();
        }
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/comment[id='%d']", this.xpath(), number)
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
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/comments",
            this.repo, this.owner.number()
        );
    }
}
