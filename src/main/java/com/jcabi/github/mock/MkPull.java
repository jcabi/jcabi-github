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
import com.jcabi.github.Checks;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.MergeState;
import com.jcabi.github.Pull;
import com.jcabi.github.PullComments;
import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github pull.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = {"storage", "self", "coords", "num"})
@SuppressWarnings("PMD.TooManyMethods")
final class MkPull implements Pull {
    /**
     * The separator between the username and
     * the branch name in the labels of pull request base/head objects.
     */
    private static final String USER_BRANCH_SEP = ":";
    /**
     * Property name for ref in pull request ref JSON object.
     */
    private static final String REF_PROP = "ref";
    /**
     * Property name for label in pull request ref JSON object.
     */
    private static final String LABEL_PROP = "label";
    /**
     * Property name for number in pull request JSON object.
     */
    private static final String NUMBER_PROP = "number";
    /**
     * Property name for user in pull request JSON object.
     */
    private static final String USER_PROP = "user";
    /**
     * Property name for head in pull request JSON object.
     */
    private static final String HEAD_PROP = "head";
    /**
     * Property name for head in pull request JSON object.
     */
    private static final String BASE_PROP = "base";

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
     * Pull number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param number Pull request number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkPull(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final int number
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.num = number;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public PullRef base() throws IOException {
        return new MkPullRef(
            this.storage,
            new MkBranches(
                this.storage,
                this.self,
                this.coords
            ).get(
                this.storage.xml().xpath(
                    String.format("%s/base/text()", this.xpath())
                ).get(0)
            )
        );
    }

    @Override
    public PullRef head() throws IOException {
        final String userbranch = this.storage.xml()
            .xpath(String.format("%s/head/text()", this.xpath()))
            .get(0);
        final String[] parts = userbranch.split(MkPull.USER_BRANCH_SEP, 2);
        if (parts.length != 2) {
            throw new IllegalStateException("Invalid MkPull head");
        }
        final String user = parts[0];
        final String branch = parts[1];
        return new MkPullRef(
            this.storage,
            new MkBranches(
                this.storage,
                this.self,
                new Coordinates.Simple(
                    user,
                    this.coords.repo()
                )
            ).get(branch)
        );
    }

    @Override
    public Iterable<Commit> commits() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<JsonObject> files() {
        return Collections.emptyList();
    }

    @Override
    public void merge(
        final String msg
    ) {
        // nothing to do here
    }

    @Override
    public MergeState merge(
        final String msg,
        final String sha
    ) {
        throw new UnsupportedOperationException("Merge not supported");
    }

    @Override
    public PullComments comments() throws IOException {
        return new MkPullComments(this.storage, this.self, this.coords, this);
    }

    /**
     * Retrieve PR check runs.
     * @return Checks
     * @since 1.6.0
     */
    @Override
    public Checks checks() {
        return new MkChecks(this.storage, this.coords, this);
    }

    @Override
    public int compareTo(
        final Pull pull
    ) {
        return this.number() - pull.number();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    public JsonObject json() throws IOException {
        final XML xml = this.storage.xml().nodes(this.xpath()).get(0);
        final String branch = xml.xpath("base/text()").get(0);
        final String head = xml.xpath("head/text()").get(0);
        final String[] parts = head.split(MkPull.USER_BRANCH_SEP, 2);
        final JsonObject obj = new JsonNode(xml).json();
        final JsonObjectBuilder json = Json.createObjectBuilder();
        for (final Map.Entry<String, JsonValue> val : obj.entrySet()) {
            if (MkPull.NUMBER_PROP.equals(val.getKey())) {
                json.add(
                    MkPull.NUMBER_PROP,
                    Integer.parseInt(xml.xpath("number/text()").get(0))
                );
            } else if (MkPull.USER_PROP.equals(val.getKey())) {
                json.add(
                    MkPull.USER_PROP,
                    Json.createObjectBuilder()
                        .add("login", xml.xpath("user/login/text()").get(0))
                        .build()
                );
            } else if (MkPull.HEAD_PROP.equals(val.getKey())) {
                json.add(
                    MkPull.HEAD_PROP,
                    Json.createObjectBuilder()
                        .add(MkPull.REF_PROP, parts[1])
                        .add(MkPull.LABEL_PROP, head)
                        .build()
                );
            } else if (MkPull.BASE_PROP.equals(val.getKey())) {
                json.add(
                    MkPull.BASE_PROP,
                    Json.createObjectBuilder()
                        .add(MkPull.REF_PROP, branch)
                        .add(
                            MkPull.LABEL_PROP,
                            String.format(
                                "%s:%s",
                                this.coords.user(),
                                branch
                            )
                        ).build()
                );
            } else {
                json.add(val.getKey(), val.getValue());
            }
        }
        json.add(
            "comments",
            this.storage.xml().nodes(this.comment()).size()
        );
        return json.build();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']",
            this.coords, this.num
        );
    }

    /**
     * XPath of issue element in XML tree.
     * @return XPath
     */
    private String comment() {
        return String.format(
            // @checkstyle LineLengthCheck (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/comments/comment",
            this.coords, this.num
        );
    }

}
