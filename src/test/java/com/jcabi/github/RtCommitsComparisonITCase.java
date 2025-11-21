/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtCommitsComparison}.
 * @since 0.24
 */
final class RtCommitsComparisonITCase {
    /**
     * RtCommitsComparison can read the file changes in the comparison.
     * @see <a href="https://api.github.com/repos/jcabi/jcabi-github/compare/fec537c74da115b01a5c27b225d22a3976545acf...3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4">The relevant commit comparison</a>
     */
    @Test
    void readsFiles() throws IOException {
        final String headsha = "3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4";
        final Iterable<FileChange> files = GitHubIT.connect()
            .repos()
            .get(new Coordinates.Simple("jcabi/jcabi-github"))
            .commits()
            .compare("fec537c74da115b01a5c27b225d22a3976545acf", headsha)
            .files();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            files,
            Matchers.iterableWithSize(1)
        );
        final FileChange.Smart file = new FileChange.Smart(
            files.iterator().next()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.additions(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.blobUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://github.com/jcabi/jcabi-github/blob/3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4/.rultor.yml"
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.changes(),
            // @checkstyle MagicNumberCheck (1 line)
            Matchers.equalTo(4)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.contentsUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://api.github.com/repos/jcabi/jcabi-github/contents/.rultor.yml?ref=3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4"
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.deletions(),
            Matchers.equalTo(2)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.filename(),
            Matchers.equalTo(".rultor.yml")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.patch(),
            Matchers.equalTo(
                Optional.of(
                    // @checkstyle LineLength (1 line)
                    "@@ -2,7 +2,7 @@ architect:\n - yegor256\n - dmarkov\n install:\n-- sudo gem install -N pdd\n+- sudo gem install --no-rdoc --no-ri pdd\n assets:\n   secring.gpg: yegor256/home#assets/secring.gpg\n   settings.xml: yegor256/home#assets/jcabi/settings.xml\n@@ -37,4 +37,4 @@ release:\n     git commit -am \"${tag}\"\n     mvn clean deploy -Pqulice -Psonatype -Pjcabi --errors --settings ../settings.xml\n     mvn clean site-deploy -Psite --errors --settings ../settings.xml\n-  commanders: []\n\\ No newline at end of file\n+  commanders: []"
                )
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.rawUrl(),
            Matchers.equalTo(
                // @checkstyle LineLength (1 line)
                "https://github.com/jcabi/jcabi-github/raw/3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4/.rultor.yml"
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.sha(),
            Matchers.equalTo("daaa16ef7a19c2071ce80a6545077c11880daac3")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            file.status(),
            Matchers.equalTo(FileChange.Status.MODIFIED)
        );
    }

}
