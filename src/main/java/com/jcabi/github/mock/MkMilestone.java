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
import com.jcabi.github.Milestone;
import com.jcabi.github.Repo;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;

/**
 * Mock Github milestone.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
final class MkMilestone implements Milestone {

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
     * The number of the MkMilestone.
     */
    private final transient int code;

    /**
     * MkMilestone constructor.
     * @param strg The storage
     * @param login The user to login with
     * @param crds The repo
     * @param num The number of the MkMilestone
     * @checkstyle ParameterNumber (5 lines)
     */
    MkMilestone(
        @NotNull(message = "strg can't be NULL") final MkStorage strg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "crds can't be NULL") final Coordinates crds,
        final int num
    ) {
        this.self = login;
        this.coords = crds;
        this.storage = strg;
        this.code = num;
    }

    @Override
    public boolean equals(
        @NotNull(message = "obj should not be NULL") final Object obj
    ) {
        return obj instanceof Milestone
            && this.code == Milestone.class.cast(obj).number();
    }

    @Override
    public int hashCode() {
        return this.code;
    }

    @Override
    public int compareTo(
        @NotNull(message = "milestone can't be NULL") final Milestone milestone
    ) {
        assert this.self != null;
        assert this.coords != null;
        assert this.storage != null;
        assert this.code != -1;
        int result = this.coords.compareTo(
            milestone.repo().coordinates()
        );
        if (result == 0) {
            result = Integer.valueOf(this.code).compareTo(milestone.number());
        }
        return result;
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void patch(
        @NotNull(message = "json can't be NULL") final JsonObject json
    ) throws IOException {
        new JsonPatch(
            this.storage
        ).patch(
            this.xpath(),
            json
        );
    }

    @Override
    @NotNull(message = "repo is never NULL")
    public Repo repo() {
        return new MkRepo(
            this.storage,
            this.self,
            this.coords
        );
    }

    @Override
    public int number() {
        return this.code;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/milestones[number='%d']",
            this.coords,
            this.code
        );
    }

}
