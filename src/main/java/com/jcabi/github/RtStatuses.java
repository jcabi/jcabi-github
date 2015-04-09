package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class RtStatuses implements Statuses {

    /**
     * RESTful request.
     */
    private final transient Request request;

    private final transient Commit hash;

    RtStatuses(final Request req, final Commit commit) {
        final Coordinates coords = commit.repo().coordinates();
        this.request = req.uri()
                .path("/repos")
                .path(coords.user())
                .path(coords.repo())
                .path("/statuses")
                .path(commit.sha())
                .back();
        this.hash = commit;
    }

    @Override
    @NotNull(message = "toString is never NULL")
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    @NotNull(message = "Commit hash can't be NULL")
    public Commit commit() {
        return hash;
    }

    @Override
    public Status create(@NotNull(message = "status can't be NULL") Status status) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
                .add("state", status.state().name())
                .add("target_url", status.targetUrl())
                .add("description", status.description())
                .add("context", status.context())
                .build();

        JsonObject jsonObject = this.request.method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject();
        return new RtStatus(StatusState.valueOf(jsonObject.getString("state")),
                jsonObject.getString("target_url"),
                jsonObject.getString("description"),
                jsonObject.getString("context"));
    }

    @Override
    @NotNull(message = "list of statuses can't be NULL")
    public List<Statuses> list(@NotNull(message = "ref can't be NULL") String ref) {
        return null;
    }

    @Override
    @NotNull(message = "JSON can't be NULL")
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public int compareTo(
            @NotNull(message = "Commit can't be NULL") final Statuses statuses
    ) {
        return this.commit().sha().compareTo(statuses.commit().sha());
    }
}
