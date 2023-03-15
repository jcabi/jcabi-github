package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class RtChecks implements Checks {

    private final Pull pull;
    private final Request request;

    RtChecks(final Request req, final Pull pr) {
        this.pull = pr;
        this.request = req;
    }

    /**
     * Get all checks.
     * JSON schema:
     * <p>{@code
     * {
     *   "total_count": 1,
     *   "check_runs": [
     *     {
     *       "id": 4,
     *       "status": "completed",
     *       ...
     *     }
     *   ]
     * }
     * }</p>
     * @return Checks.
     * @throws IOException If there is any I/O problem.
     */
    @Override
    public Collection<Check> all() throws IOException {
        final Coordinates coords = this.pull.repo().coordinates();
        return this.request.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/commits")
            .path(this.pull.head().ref())
            .path("/check-runs")
            .back()
            .method(Request.GET).fetch().as(JsonResponse.class)
            .json()
            .readObject()
            .getJsonArray("check_runs")
            .stream()
            .map(RtChecks::check)
            .collect(Collectors.toList());
    }

    private static RtCheck check(final JsonValue value) {
        final JsonObject check = value.asJsonObject();
        return new RtCheck(check.getInt("id"), check.getString("status"));
    }
}
