/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtMilestones}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
public final class RtMilestonesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    @Test
    public void deleteMilestone() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final RtMilestones milestones = new RtMilestones(
                new ApacheRequest(container.home()),
                RtMilestonesTest.repo()
            );
            milestones.remove(1);
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
