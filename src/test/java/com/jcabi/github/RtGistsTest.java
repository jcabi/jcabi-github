package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;

import java.net.HttpURLConnection;
import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtGists}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtGistsTest {

    /**
     * RtGists can create new files.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canCreateFiles() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"id\":\"1\"}"
            )
        ).start();
        final RtGists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.create(Collections.singletonMap("test", "")),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                container.take().body(),
                Matchers.startsWith("{\"files\":{\"test\":{\"content\":")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtGists can retrieve a specific Gist.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRetrieveSpecificGist() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")
        ).start();
        final RtGists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.get("gist"),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtGists can iterate through its contents.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canIterateThrouRtGists() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "[{\"id\":\"hello\"}]"
            )
        ).start();
        final RtGists gists = new RtGists(
            new MkGithub(),
            new ApacheRequest(container.home())
        );
        try {
            MatcherAssert.assertThat(
                gists.iterate().iterator().next(),
                Matchers.notNullValue()
            );
        } finally {
            container.stop();
        }
    }
    
    /**
     * RtGists can remove a gist by name.
     * @throws Exception
     */
    @Test
    public void canRemoveGistByName() throws Exception{
		final MkContainer container = new MkGrizzlyContainer().next(
				new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, ""))
				.start();
		final RtGists gists = new RtGists(new MkGithub(), new ApacheRequest(
				container.home()));

		try {
			gists.remove("gist");
			final MkQuery query = container.take();
			MatcherAssert.assertThat(query.method(),
					Matchers.equalTo(Request.DELETE));
			MatcherAssert.assertThat(query.body(),
					Matchers.isEmptyOrNullString());
		} finally {
			container.stop();
		}
    }

}