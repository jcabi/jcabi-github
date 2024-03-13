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
 * Mock Github Checks.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @version $Id$
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
                String.format("%s/check", this.xpath()),
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
