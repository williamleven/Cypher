package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * This class provides access to matrix like endpoints trough
 * ordinary java methods returning Json Objects.
 *
 * <p>Most endpoints require a session to
 * have been successfully initiated as the data provided by the
 * endpoint itself isn't publicly available.
 *
 * <p>A session can be initiated with {@link #login(String, String, String)}
 *
 * @see <a href="https://matrix.org/docs/api/client-server/">matrix.org</a>
 */
public interface ApiLayer {

	/**
	 * Synchronise the client's state and receive new messages.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_sync">matrix.org</a>
	 * @param filter Id of filter to be used on the sync data
	 * @param since Point in time of last sync request
	 * @param fullState Shall all events be collected
	 * @param setPresence User status
	 * @return Valid Json response
	 */
	JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence) throws RestfulHTTPException, IOException;

	/**
	 * Lists the public rooms on the server.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_discovery/get_matrix_client_r0_publicRooms">matrix.org</a>
	 * @param server A homeserver to fetch public rooms from (e.g. example.org:8448 or matrix.org)
	 * @return Valid Json response
	 */
	JsonObject publicRooms(String server) throws RestfulHTTPException, IOException;

	/**
	 * Authenticates the user and crates a new session
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Session_management/post_matrix_client_r0_login">matrix.org</a>
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough (e.g. example.org:8448 or matrix.org)
	 */
	void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException;
}
