package com.jcabi.github;

/**
 * Represents a GitHub notificatios thread.
 * @author Dmitri Pisarenko (dp@altruix.co)
 * @version $Id$
 * @since 1.0
 */
public interface GitHubThread {
    /**
     * Marks this thread as read.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#mark-a-thread-as-read">Mark a thread as read</a>
     */
    void markAsRead();

    /**
     * Checks, if the current user is subscribed to this thread.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#get-a-thread-subscription">Get a Thread Subscription</a>
     * @return Subscription data, if the user is subscribe, null otherwise.
     */
    ThreadSubscription getSubscription();


}
