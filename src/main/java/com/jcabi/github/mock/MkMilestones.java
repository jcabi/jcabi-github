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

import com.jcabi.github.Coordinates;
import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Map;
import org.xembly.Directives;

/**
 * Mock Github milestones.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 */
public final class MkMilestones implements Milestones {

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
     * MkMilestones ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException - if any I/O problem occurs
     */
    MkMilestones(
        final MkStorage stg, final String login, final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format("/github/repos/repo[@coords='%s']", this.coords)
            ).addIf("milestones")
        );
    }
    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Milestone create(final String title) throws IOException {
        final int number;
        number = 1 + this.storage.xml().xpath(
            String.format("%s/milestone/number/text()", this.xpath())
        ).size();
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("milestone")
                .add("number").set(Integer.toString(number)).up()
                .add("title").set(title).up()
                .add("state").set(Milestone.OPEN_STATE).up()
                .add("description").set("mock milestone").up()
        );
        return this.get(number);
    }

    @Override
    public Milestone get(final int number) {
        return new MkMilestone(this.storage, this.self, this.coords, number);
    }

    @Override
    public Iterable<Milestone> iterate(final Map<String, String> params) {
        throw new UnsupportedOperationException(
            "This method hasn't been implemented yet."
        );
    }

    @Override
    public void remove(final int number) throws IOException {
        throw new UnsupportedOperationException(
            "This operation is not available yet."
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/milestones",
            this.coords
        );
    }
}
