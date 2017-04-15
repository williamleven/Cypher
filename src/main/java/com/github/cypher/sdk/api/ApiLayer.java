package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;

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
	JsonObject getPublicRooms(String server) throws RestfulHTTPException, IOException;

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
	JsonObject getRoomMessages(String roomId) throws RestfulHTTPException, IOException;

	/**
	 * Get the list of members for this room.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_rooms_roomId_members">matrix.org</a>
	 * @param roomId The unique ID of a room (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 * @return Valid Json response
	 */
	JsonObject getRoomMembers(String roomId) throws RestfulHTTPException, IOException;

	/**
	 * Get the combined profile information for this user.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the combined profile information for the user
	 */
	JsonObject getUserProfile(String userId) throws RestfulHTTPException, IOException;

	/**
	 * Get the user's avatar URL.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_avatar_url">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the user avatar url
	 */
	JsonObject getUserAvatarUrl(String userId) throws RestfulHTTPException, IOException;

	/**
	 * Set the user's avatar.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_avatar_url">matrix.org</a>
	 * @param avatarUrl The matrix media URL of the image (e.g. "avatar_url": "mxc://matrix.org/wefh34uihSDRGhw34")
	 */
	void setUserAvatarUrl(String avatarUrl) throws RestfulHTTPException, IOException;

	/**
	 * Get the user's display name.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_displayname">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the user display name
	 */
	JsonObject getUserDisplayName(String userId) throws RestfulHTTPException, IOException;

	/**
	 * Set the user's display name.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_profile_userId_displayname">matrix.org</a>
	 * @param displayName The new display name to be set
	 */
	void setUserDisplayName(String displayName) throws RestfulHTTPException, IOException;

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

	/**
	 * This endpoint is used to get RoomID from the Room Alias.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_directory/get_matrix_client_r0_directory_room_roomAlias">matrix.org</a>
	 * @param roomAlias The alias of a given room.
	 * @return Valid Json response containing roomID.
	 */
	JsonObject getRoomIDFromAlias(String roomAlias) throws MalformedURLException, IOException;

	/**
	 * This endpoint is used to delete the Room Alias from the current room.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_directory/delete_matrix_client_r0_directory_room_roomAlias">matrix.org</a>
	 * @param roomAlias
	 * @return Valid Json response containing the event id generated by the server.
	 */
	JsonObject deleteRoomAlias(String roomAlias) throws RestfulHTTPException, IOException;

	/**
	 * This endpoint is used to set the current rooms Room Alias.
	 * @param roomAlias The rquested roomAlias.
	 * @param roomID The rooms ID.
	 * @return Valid Json response containing the event id generated by the server.

	 */
	JsonObject putRoomAlias(String roomAlias, JsonObject roomID) throws RestfulHTTPException, IOException;

	/**
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_creation/post_matrix_client_r0_createRoom">matrix.org</a>
	 * @param roomCreation JsonObject containing all required and optional parameters for room creation.
	 * @return Valid Json response containing the event id generated by the server.
	 */
	JsonObject postCreateRoom(JsonObject roomCreation) throws RestfulHTTPException, IOException;
}
