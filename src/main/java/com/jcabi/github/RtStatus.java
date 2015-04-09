package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"state"})
public final class RtStatus implements Status {

    private final transient StatusState state;

    private final transient String targetUrl;

    private final transient String description;

    private final transient String context;

    public RtStatus(
            @NotNull(message = "state can't be NULL") final StatusState state,
            final String targetUrl,
            final String description,
            final String context
    ) {
        this.targetUrl = targetUrl;
        this.description = description;
        this.context = context;
        this.state = state;
    }

    @Override
    public StatusState state() {
        return this.state;
    }

    @Override
    public String targetUrl() {
        return this.targetUrl;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String context() {
        return this.context;
    }
}
