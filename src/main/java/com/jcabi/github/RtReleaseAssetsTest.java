package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.io.IOException;

/**
 * Test case for {@link RtReleaseAssets}.
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class RtReleaseAssetsTest {

    /**
     * RtReleaseAssets can fetch empty list of release assets.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfReleaseAssets() throws Exception {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withBody("[]"),
            RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(
            assets.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtReleaseAssets can fetch non empty list of release assets.
     */
    @Test
    public void canFetchNonEmptyListOfReleaseAssets() {
        final int number = 1;
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest().withBody(
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("id", number)
                        .add("tag_name", "v1.0.0")
                        .add("name", "v1.0.0")
                        .add("body", "Asset")
                ).build().toString()
            ),
            RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(
            assets.iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }


    /**
     * RtReleaseAssets can fetch a single release asset.
     * @throws java.io.IOException If some problem inside
     */
    @Test
    public void canFetchSingleReleaseAsset() throws IOException {
        final ReleaseAssets assets = new RtReleaseAssets(
            new FakeRequest(), RtReleaseAssetsTest.repo()
        );
        MatcherAssert.assertThat(assets.get(1), Matchers.notNullValue());
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "assets"))
            .when(repo).coordinates();
        return repo;
    }

}
