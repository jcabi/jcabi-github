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
package com.jcabi.github;

import com.google.common.base.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @since 0.24
 */
public final class RtCommitsComparisonITCase {
    /**
     * RtCommitsComparison can read the file changes in the comparison.
     * @throws Exception If some problem inside
     * @see <a href="https://api.github.com/repos/jcabi/jcabi-github/compare/fec537c74da115b01a5c27b225d22a3976545acf...3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4">The relevant commit comparison</a>
     */
    @Test
    public void readsFiles() throws Exception {
        final String headsha = "3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4";
        final Iterable<FileChange> files = new GithubIT().connect()
            .repos()
            .get(new Coordinates.Simple("jcabi/jcabi-github"))
            .commits()
            .compare("fec537c74da115b01a5c27b225d22a3976545acf", headsha)
            .files();
        MatcherAssert.assertThat(
            files,
            Matchers.<FileChange>iterableWithSize(1)
        );
        final FileChange.Smart file = new FileChange.Smart(
            files.iterator().next()
        );
        MatcherAssert.assertThat(
            file.additions(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            file.blobUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://github.com/jcabi/jcabi-github/blob/3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4/.rultor.yml"
            )
        );
        MatcherAssert.assertThat(
            file.changes(),
            // @checkstyle MagicNumberCheck (1 line)
            Matchers.equalTo(4)
        );
        MatcherAssert.assertThat(
            file.contentsUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://api.github.com/repos/jcabi/jcabi-github/contents/.rultor.yml?ref=3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4"
            )
        );
        MatcherAssert.assertThat(
            file.deletions(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            file.filename(),
            Matchers.equalTo(".rultor.yml")
        );
        MatcherAssert.assertThat(
            file.patch(),
            Matchers.equalTo(
                Optional.of(
                    // @checkstyle LineLength (1 line)
                    "@@ -2,7 +2,7 @@ architect:\n - yegor256\n - dmarkov\n install:\n-- sudo gem install -N pdd\n+- sudo gem install --no-rdoc --no-ri pdd\n assets:\n   secring.gpg: yegor256/home#assets/secring.gpg\n   settings.xml: yegor256/home#assets/jcabi/settings.xml\n@@ -37,4 +37,4 @@ release:\n     git commit -am \"${tag}\"\n     mvn clean deploy -Pqulice -Psonatype -Pjcabi --errors --settings ../settings.xml\n     mvn clean site-deploy -Psite --errors --settings ../settings.xml\n-  commanders: []\n\\ No newline at end of file\n+  commanders: []"
                )
            )
        );
        MatcherAssert.assertThat(
            file.rawUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://github.com/jcabi/jcabi-github/raw/3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4/.rultor.yml"
            )
        );
        MatcherAssert.assertThat(
            file.sha(),
            Matchers.equalTo("daaa16ef7a19c2071ce80a6545077c11880daac3")
        );
        MatcherAssert.assertThat(
            file.status(),
            Matchers.equalTo(FileChange.Status.MODIFIED)
        );
    }

}
