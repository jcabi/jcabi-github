/**
 * Copyright (c) 2013-2025, jcabi.com
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
import com.jcabi.github.Check;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github check.
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
        final Status status = Status.fromString(
            node.xpath("@status").get(0)
        );
        final Conclusion conclusion = Conclusion.fromString(
            node.xpath("@conclusion").get(0)
        );
        return status == Status.COMPLETED
            && conclusion == Conclusion.SUCCESS;
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
