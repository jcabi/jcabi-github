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
import com.jcabi.github.Repo;
import com.jcabi.github.Tree;
import com.jcabi.github.Trees;
import java.io.IOException;
import java.util.Map.Entry;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of Github Trees.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkTrees implements Trees {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo's name.
     */
    private final transient Coordinates coords;
    /**
     * Public constructor.
     * @param stg The storage.
     * @param login The login name.
     * @param rep Repo's coordinates.
     * @throws IOException If something goes wrong.
     */
    MkTrees(
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
                    "/github/repos/repo[@coords='%s']/git",
                    this.coords
                )
            ).addIf("trees")
        );
    }
    @Override
    @NotNull(message = "Repository can't be NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "created tree is never NULL")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Tree create(
        @NotNull(message = "params can't be NULL") final JsonObject params
    ) throws IOException {
        final JsonArray trees = params.getJsonArray("tree");
        for (final JsonValue val : trees) {
            final JsonObject tree = (JsonObject) val;
            final String sha = tree.getString("sha");
            final Directives dirs = new Directives().xpath(this.xpath())
                .add("tree");
            for (final Entry<String, JsonValue> entry : tree.entrySet()) {
                dirs.add(entry.getKey()).set(entry.getValue().toString()).up();
            }
            this.storage.apply(dirs);
            final String ref;
            if (tree.containsValue("name")) {
                ref = tree.getString("name");
            } else {
                ref = sha;
            }
            new MkReferences(this.storage, this.self, this.coords).create(
                new StringBuilder("refs/trees/").append(ref).toString(),
                sha
            );
        }
        return this.get(trees.getJsonObject(0).getString("sha"));
    }

    @Override
    @NotNull(message = "tree is never NULL")
    public Tree get(@NotNull(message = "sha can't be NULL") final String sha) {
        return new MkTree(this.storage, this.self, this.coords, sha);
    }

    /**
     * Gets a tree recursively.
     * @param sha The tree sha.
     * @return Trees
     * @see <a href="https://developer.github.com/v3/git/trees/#get-a-tree-recursively">Trees API</a>
     */
    @Override
    @NotNull(message = "tree is never NULL")
    public Tree getRec(@NotNull(message = "sha can't be NULL") final String sha
    ) {
        return new MkTree(this.storage, this.self, this.coords, sha);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/trees",
            this.coords
        );
    }

}
