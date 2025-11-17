/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Check;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock GitHub check.
 *
 * @since 1.6.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = {"storage", "coordinates", "pull", "identifier"})
public final class MkCheck implements Check {

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
     * Check identifier.
     */
    private final transient int identifier;

    /**
     * Main ctor.
     * @param stg Storage
     * @param coord Coordinates
     * @param pll Pull
     * @param number Check identifier
     * @checkstyle ParameterNumber (6 lines)
     */
    public MkCheck(
        final MkStorage stg,
        final Coordinates coord,
        final Pull pll,
        final int number
    ) {
        this.storage = stg;
        this.coordinates = coord;
        this.pull = pll;
        this.identifier = number;
    }

    @Override
    public boolean successful() throws IOException {
        final XML node = this.storage.xml().nodes(this.xpath()).get(0);
        final Check.Status status = Check.Status.fromString(
            node.xpath("@status").get(0)
        );
        final Check.Conclusion conclusion = Check.Conclusion.fromString(
            node.xpath("@conclusion").get(0)
        );
        return status == Check.Status.COMPLETED
            && conclusion == Check.Conclusion.SUCCESS;
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/pulls/pull[number='%d']/checks/check[@id='%d']",
            this.coordinates, this.pull.number(), this.identifier
        );
    }
}
