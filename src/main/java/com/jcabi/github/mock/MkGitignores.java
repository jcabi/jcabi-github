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
import com.jcabi.github.Github;
import com.jcabi.github.Gitignores;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Gitignore.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "ghub" })
@SuppressWarnings("PMD.UseConcurrentHashMap")
final class MkGitignores implements Gitignores {

    /**
     * The gitignore templates.
     */
    private static final Map<String, String> GITIGNORES =
        Collections.singletonMap(
            "Java",
            "*.class\n\n# Package Files #\n*.jar\n*.war\n*.ear\n"
        );

    /**
     * Github.
     */
    private final transient MkGithub ghub;

    /**
     * Public ctor.
     * @param github The github
     */
    MkGitignores(@NotNull(message = "github can't NULL")
        final MkGithub github) {
        this.ghub = github;
    }

    @Override
    @NotNull(message = "github is never NULL")
    public Github github() {
        return this.ghub;
    }

    @Override
    @NotNull(message = "Iterable is never NULL")
    public Iterable<String> iterate() throws IOException {
        return GITIGNORES.keySet();
    }

    @Override
    @NotNull(message = "template is never NULL")
    public String template(
        @NotNull(message = "Template name can't be NULL")
        final String name) throws IOException {
        final String template = GITIGNORES.get(name);
        if (template == null) {
            throw new IllegalArgumentException("Template not found.");
        }
        return template;
    }
}
