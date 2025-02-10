package dev.fileeditor.oauth2.Session;

/**
 * An abstract controller for {@link Session Sessions},
 * <p>
 * Implementations should be able to create their respectively controlled implementations
 * using an instance of {@link SessionData}
 * and maintain the created instances for the entire lifetime of the session.
 *
 * @param  <S>
 *         The type of the Session for this to handle.
 */
public interface SessionController<S extends Session> {

	/**
	 * Gets a {@link Session} that
	 * was previously created using the provided identifier.
	 *
	 * <p>It is very important for implementations of SessionController to hold
	 * a contract that Sessions created using {@link #createSession(SessionData)}
	 * will be maintained and retrievable by external sources at any time.
	 *
	 * <p>Note that Sessions that have elapsed their effective
	 * {@link SessionData#getExpiration() expiration}
	 * are not necessary to maintain, unless they have been refreshed in which case they
	 * should be updated to reflect this.
	 *
	 * @param  identifier
	 *         The identifier to get a Session by.
	 *
	 * @return The Session mapped to the identifier provided.
	 */
	S getSession(String identifier);

	/**
	 * Creates a new {@link Session} using the specified {@link SessionData}.
	 *
	 * <p>Sessions should be kept mapped outside just creation so that they can be
	 * retrieved using {@link SessionController#getSession(String)} later for further
	 * manipulation, as well as to keep updated if they are refreshed.
	 *
	 * @param  data
	 *         The data to create a Session using.
	 *
	 * @return A new Session.
	 */
	S createSession(SessionData data);

}
