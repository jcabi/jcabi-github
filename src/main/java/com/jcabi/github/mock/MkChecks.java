/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.collect.ImmutableList;
import com.jcabi.github.Check;
import com.jcabi.github.Checks;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import org.xembly.Directives;

/**
 * Mock GitHub Checks.
 *
 * @since 1.6.0
 */
public final class MkChecks implements Checks {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Coordinates of repo.
     */
    private final transient Coordinates coordinates;

    /**
     * Pull.
     */
    private final transient Pull pull;

    /**
     * Ctor.
     * @param strg Storage
     * @param coord Coordinates of repo
     * @param pll Pull
     */
    MkChecks(
        final MkStorage strg,
        final Coordinates coord,
        final Pull pll
    ) {
        this.storage = strg;
        this.coordinates = coord;
        this.pull = pll;
    }

    @Override
    public Collection<Check> all() throws IOException {
        return ImmutableList.copyOf(
            new MkIterable<>(
                this.storage,
                this.xpath().concat("/check"),
                item -> new MkCheck(
                    this.storage,
                    this.coordinates,
                    this.pull,
                    Integer.parseInt(item.xpath("@id").get(0))
                )
            )
        );
    }

    /**
     * Create check.
     * @param status Status.
     * @param conclusion Conclusion.
     * @return Check.
     * @throws IOException If fails.
     */
    public Check create(
        final Check.Status status,
        final Check.Conclusion conclusion
    ) throws IOException {
        final int identifier = new SecureRandom().nextInt();
        final Directives directives = new Directives()
            .xpath(this.xpath())
            .add("check")
            .attr("id", identifier)
            .attr("status", status.value())
            .attr("conclusion", conclusion.value())
            .up();
        this.storage.apply(directives);
        return new MkCheck(
            this.storage,
            this.coordinates,
            this.pull,
            identifier
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/checks",
            this.coordinates, this.pull.number()
        );
    }
}
