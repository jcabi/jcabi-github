package com.jcabi.github;

import com.jcabi.aspects.Immutable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Immutable
public interface Status {

    @NotNull(message = "state is never NULL")
    StatusState state();

    // TODO optional
    String targetUrl();

    // TODO optional
    String description();

    // TODO optional
    String context();
}
