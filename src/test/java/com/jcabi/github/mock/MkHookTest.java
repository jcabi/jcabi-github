package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Coordinates;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;

/**
 * Tests for {@link MkHook}.
 * @since 1.0
 */
public final class MkHookTest {

    public void create() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final String login = "paulodamaso";
        final Coordinates coordinates = new Coordinates.Simple(
            "jcabi", "jcabi-github"
        );
        final int number = Tv.FIVE;
        final MkHook hook = new MkHook(storage, login, coordinates, number);
        MatcherAssert.assertThat(
            "Hook returned wrong number",
            hook.number(),
            new IsEqual(number)
        );
        MatcherAssert.assertThat(
            "Hook returned wrong json",
            hook.json(),

        );
    }
}
