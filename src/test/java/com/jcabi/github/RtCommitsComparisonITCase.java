/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
     * SHA of the head commit of the comparison.
     */
    private static final String HEAD =
        "3ebe52aaf7bf7681fa30a19fcbbbb246db7ad8b4";

    /**
     * RtCommitsComparison can count the files in the comparison.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void countsFiles() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            RtCommitsComparisonITCase.files(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * RtCommitsComparison can read the additions of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsAdditions() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of additions",
            RtCommitsComparisonITCase.change().additions(),
            Matchers.equalTo(2)
        );
    }

    /**
     * RtCommitsComparison can read the blob URL of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsBlobUrl() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong blob URL",
            RtCommitsComparisonITCase.change().blobUrl(),
            Matchers.equalTo(
                String.format(
                    "https://github.com/jcabi/jcabi-github/blob/%s/.rultor.yml",
                    RtCommitsComparisonITCase.HEAD
                )
            )
        );
    }

    /**
     * RtCommitsComparison can read the changes of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsChanges() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of changes",
            RtCommitsComparisonITCase.change().changes(),
            Matchers.equalTo(4)
        );
    }

    /**
     * RtCommitsComparison can read the contents URL of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsContentsUrl() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong contents URL",
            RtCommitsComparisonITCase.change().contentsUrl(),
            Matchers.equalTo(
                String.join(
                    "",
                    "https://api.github.com/repos/jcabi/jcabi-github",
                    "/contents/.rultor.yml?ref=",
                    RtCommitsComparisonITCase.HEAD
                )
            )
        );
    }

    /**
     * RtCommitsComparison can read the deletions of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsDeletions() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong amount of deletions",
            RtCommitsComparisonITCase.change().deletions(),
            Matchers.equalTo(2)
        );
    }

    /**
     * RtCommitsComparison can read the name of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsFilename() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong name",
            RtCommitsComparisonITCase.change().filename(),
            Matchers.equalTo(".rultor.yml")
        );
    }

    /**
     * RtCommitsComparison can read the patch of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsPatch() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong patch",
            RtCommitsComparisonITCase.change().patch(),
            Matchers.equalTo(
                Optional.of(
                    String.join(
                        System.lineSeparator(),
                        "@@ -2,7 +2,7 @@ architect:",
                        " - yegor256",
                        " - dmarkov",
                        " install:",
                        "-- sudo gem install -N pdd",
                        "+- sudo gem install --no-rdoc --no-ri pdd",
                        " assets:",
                        "   secring.gpg: yegor256/home#assets/secring.gpg",
                        "   settings.xml: yegor256/home#assets/jcabi/settings.xml",
                        "@@ -37,4 +37,4 @@ release:",
                        "     git commit -am \"${tag}\"",
                        "     mvn clean deploy -Pqulice -Psonatype -Pjcabi --errors --settings ../settings.xml",
                        "     mvn -ntp clean site-deploy -Psite --errors --settings ../settings.xml",
                        "-  commanders: []",
                        "\\ No newline at end of file",
                        "+  commanders: []"
                    )
                )
            )
        );
    }

    /**
     * RtCommitsComparison can read the raw URL of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsRawUrl() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong raw URL",
            RtCommitsComparisonITCase.change().rawUrl(),
            Matchers.equalTo(
                String.format(
                    "https://github.com/jcabi/jcabi-github/raw/%s/.rultor.yml",
                    RtCommitsComparisonITCase.HEAD
                )
            )
        );
    }

    /**
     * RtCommitsComparison can read the SHA of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsSha() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong SHA",
            RtCommitsComparisonITCase.change().sha(),
            Matchers.equalTo("daaa16ef7a19c2071ce80a6545077c11880daac3")
        );
    }

    /**
     * RtCommitsComparison can read the status of the changed file.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void readsStatus() throws IOException {
        MatcherAssert.assertThat(
            "Changed file has a wrong status",
            RtCommitsComparisonITCase.change().status(),
            Matchers.equalTo(FileChange.Status.MODIFIED)
        );
    }

    private static FileChange.Smart change() throws IOException {
        return new FileChange.Smart(
            RtCommitsComparisonITCase.files().iterator().next()
        );
    }

    private static Iterable<FileChange> files() throws IOException {
        return GitHubIT.connect()
            .repos()
            .get(new Coordinates.Simple("jcabi/jcabi-github"))
            .commits().compare(
                "fec537c74da115b01a5c27b225d22a3976545acf",
                RtCommitsComparisonITCase.HEAD
            )
            .files();
    }
}
