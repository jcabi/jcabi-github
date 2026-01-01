/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
import lombok.ToString;

/**
 * Mock GitHub check.
 *
 * @since 1.6.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
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

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final MkCheck other = (MkCheck) obj;
            result = this.identifier == other.identifier
                && this.storage.equals(other.storage)
                && this.coordinates.equals(other.coordinates)
                && this.pull.equals(other.pull);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.storage.hashCode();
        result = 31 * result + this.coordinates.hashCode();
        result = 31 * result + this.pull.hashCode();
        result = 31 * result + this.identifier;
        return result;
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
