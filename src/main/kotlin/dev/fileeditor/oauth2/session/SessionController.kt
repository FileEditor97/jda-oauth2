package dev.fileeditor.oauth2.session

/**
 * An abstract controller for [Sessions][Session],
 *
 *
 * Implementations should be able to create their respectively controlled implementations
 * using an instance of [SessionData]
 * and maintain the created instances for the entire lifetime of the session.
 *
 * @param  <S>
 * The type of the Session for this to handle.
</S> */
interface SessionController<S : Session> {
    /**
     * Gets a [Session] that
     * was previously created using the provided identifier.
     *
     *
     * It is very important for implementations of SessionController to hold
     * a contract that Sessions created using [.createSession]
     * will be maintained and retrievable by external sources at any time.
     *
     *
     * Note that Sessions that have elapsed their effective
     * [expiration][SessionData.expiration]
     * are not necessary to maintain, unless they have been refreshed in which case they
     * should be updated to reflect this.
     *
     * @param  identifier
     * The identifier to get a Session by.
     *
     * @return The Session mapped to the identifier provided.
     */
    fun getSession(identifier: String): S?

    /**
     * Creates a new [Session] using the specified [SessionData].
     *
     *
     * Sessions should be kept mapped outside just creation so that they can be
     * retrieved using [SessionController.getSession] later for further
     * manipulation, as well as to keep updated if they are refreshed.
     *
     * @param  data
     * The data to create a Session using.
     *
     * @return A new Session.
     */
    fun createSession(data: SessionData): S

    /**
     * End current session.
     *
     * @param identifier Session ID.
     */
    fun endSession(identifier: String)
}
