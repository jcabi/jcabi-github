/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Map;
import org.xembly.Directives;

/**
 * Mock GitHub milestones.
 */
@Immutable
final class MkMilestones implements Milestones {

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
        final MkStorage stg,
        final String login,
        final Coordinates rep
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
    public Milestone create(
        final String title
    ) throws IOException {
        final int number = 1 + this.storage.xml().xpath(
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
    public Iterable<Milestone> iterate(
        final Map<String, String> params
    ) {
        return new MkIterable<>(
            this.storage,
            String.format("%s/milestone", this.xpath()),
            xml -> this.get(
                Integer.parseInt(xml.xpath("number/text()").get(0))
            )
        );
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/milestone[number='%d']", this.xpath(), number)
            ).remove()
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
