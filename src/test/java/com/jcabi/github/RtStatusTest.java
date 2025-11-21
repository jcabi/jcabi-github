/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtStatus}.
 * @since 0.24
 */
public final class RtStatusTest {
    /**
     * RtStatus can fetch its ID number.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesId() throws IOException {
        final int ident = 666;
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtStatus(
                RtStatusTest.commit(),
                Json.createObjectBuilder().add("id", ident).build()
            ).identifier(),
            Matchers.equalTo(ident)
        );
    }

    /**
     * RtStatus can fetch its URL.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesUrl() throws IOException {
        final String url = "http://api.jcabi-github.invalid/whatever";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtStatus(
                RtStatusTest.commit(),
                Json.createObjectBuilder().add("url", url).build()
            ).url(),
            Matchers.equalTo(url)
        );
    }

    /**
     * RtStatus can fetch its associated commit.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesCommit() throws IOException {
        final Commit cmmt = RtStatusTest.commit();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtStatus(cmmt, Json.createObjectBuilder().build()).commit(),
            Matchers.equalTo(cmmt)
        );
    }

    /**
     * Returns a test commit to work with.
     * @return Commit
     * @throws IOException If there is an I/O problem.
     */
    private static Commit commit() throws IOException {
        return new MkGitHub().randomRepo().git().commits()
            .get("d288364af5028c72e2a2c91c29343bae11fffcbe");
    }
}
