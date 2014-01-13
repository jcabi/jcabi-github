package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import javax.validation.constraints.NotNull;

/**
 * Github repository collaborators.
 * @author Aleksey Popov
 * @version $id$
 * @since 1.0
 */
@Immutable
public interface Collaborators {
    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Check if a user is collaborator.
     * @param user User
     * @return true is a user is a collaborator, otherwise returns false
     * @see <a href="http://developer.github.com/v3/repos/collaborators/#get">Check if a user is collaborator</a>
     */
    boolean isCollabborator(@NotNull(message = "User is never null")User user);

    /**
     * Add user as a collaborator.
     * @param user User
     * @see <a href="http://developer.github.com/v3/repos/collaborators/#add-collaborator">Add user as a collaborator</a>
     */
    void add(@NotNull(message = "User is never null")User user);

    /**
     * Remove user as a collaborator.
     * @param user User
     * @see <a href="http://developer.github.com/v3/repos/collaborators/#remove-collaborator">Remove user as a collaborator</a>
     */
    void remove(User user);
}
