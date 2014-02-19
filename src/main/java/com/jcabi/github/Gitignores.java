/**
 * Copyright (c) 2013-2014, JCabi.com
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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Github Gitignore.
 * <p>Defines storage of .gitignore templates
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/gitignore/#gitignore">Gitignore</a>
 * @since 0.8
 */
@Immutable
public interface Gitignores {
    /**
     * Get its owner.
     * @return Github
     */
    @NotNull(message = "github is never NULL")
    Github github();

    /**
     * Iterate them all.
     * @return Iterator of Gitignote template names
     * @throws IOException If it fails due to I/O problem
     * @see <a href="http://developer.github.com/v3/gitignore/#listing-available-templates">Listing available templates</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<String> iterate() throws IOException;

    /**
     * Gets raw gitignore template.
     * @param name Name of the template
     * @return Raw template
     * @throws IOException If it fails due to I/O problem
     */
    @NotNull(message = "Raw template is never NULL")
    String template(
        @NotNull(message = "Template name can't be NULL") String name)
        throws IOException;
}
