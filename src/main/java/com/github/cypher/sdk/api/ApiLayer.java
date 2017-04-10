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

	/**
	 * This API returns a list of message and state events for a room. It uses pagination query parameters to paginate history in the room.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_rooms_roomId_messages">matrix.org</a>
	 * @param roomId The unique ID of a room (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 * @return Valid Json response
	 */
	JsonObject roomMessages(String roomId) throws RestfulHTTPException, IOException;

	/**
	 * Get the list of members for this room.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_rooms_roomId_members">matrix.org</a>
	 * @param roomId The unique ID of a room (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 * @return Valid Json response
	 */
	JsonObject roomMembers(String roomId) throws RestfulHTTPException, IOException;

	/**
	 * Get the combined profile information for this user.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the combined profile information for the user
	 */
	JsonObject userProfile(String userId) throws RestfulHTTPException, IOException;

	/**
	 * Get the user's avatar URL.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_avatar_url">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the user avatar url
	 */
	JsonObject userAvatarUrl(String userId) throws RestfulHTTPException, IOException;

	/**
	 * Get the user's display name.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_displayname">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the user display name
	 */
	JsonObject userDisplayName(String userId) throws RestfulHTTPException, IOException;

	/**
	 * This endpoint is used to send a message event to a room.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/put_matrix_client_r0_rooms_roomId_send_eventType_txnId">matrix.org</a>
	 * @param roomId The unique ID of the room (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 * @param eventType The type of event to send (e.g. "m.room.message")
	 * @param transactionId The transaction ID for this event. The client should generate an ID unique across requests with the same access token; it will be used by the server to ensure idempotency of requests
	 * @param content Event content (e.g. '{"msgtype": "m.text", "body": "I know kung fu" }')
	 * @return Valid Json response containing the event id generated by the server
	 */
	JsonObject roomSendEvent(String roomId, String eventType, int transactionId, JsonObject content) throws RestfulHTTPException, IOException;
}