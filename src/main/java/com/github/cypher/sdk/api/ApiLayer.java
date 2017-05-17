package com.github.cypher.sdk.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
	 * Represents presence states of a user
	 */
	enum Presence{ONLINE, OFFLINE, UNAVAILABLE};

	/**
	 * Gets the session
	 * @return Session
	 */
	Session getSession();

	/**
	 * Sets the session
	 * @param session Session
	 */
	void setSession(Session session);

	/**
	 * Synchronise the client's state and receive new messages.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_sync">matrix.org</a>
	 * @param filter Id of filter to be used on the sync data
	 * @param since Point in time of last sync request
	 * @param fullState Shall all events be collected
	 * @param setPresence User status
	 * @param timeout The maximum time to poll in milliseconds before returning this request
	 * @return Valid Json response
	 */
	JsonObject sync(String filter, String since, boolean fullState, Presence setPresence, int timeout) throws RestfulHTTPException, IOException;

	/**
	 * Lists the public rooms on the server.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_discovery/get_matrix_client_r0_publicRooms">matrix.org</a>
	 * @param server A homeserver to fetch public rooms from (e.g. example.org:8448 or matrix.org)
	 * @return Valid Json response
	 */
	JsonObject getPublicRooms(String server) throws RestfulHTTPException, IOException;

	/**
	 * Authenticates the user and creates a new session.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Session_management/post_matrix_client_r0_login">matrix.org</a>
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough (e.g. example.org:8448 or matrix.org)
	 */
	void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException;

	/**
	 * Invalidates the current session.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Session32management/post_matrix_client_r0_logout">matrix.org</a>
	 */
	void logout() throws RestfulHTTPException, IOException;

	/**
	 * Use a refresh token to create a new Session
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Session32management/post_matrix_client_r0_tokenrefresh">matrix.org</a>
	 */
	void refreshToken() throws RestfulHTTPException, IOException;

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
	void setUserAvatarUrl(URL avatarUrl) throws RestfulHTTPException, IOException;

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
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Presence/get_matrix_client_r0_presence_list_userId">matrix.org</a>
	 * @param userId The unique ID of the user (e.g. "@bob:matrix.org")
	 * @return Valid Json response containing the user presence list
	 */
	JsonArray getUserPresence(String userId) throws IOException;

	/**
	 * This endpoint is used to send a message event to a room.
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Room_participation/put_matrix_client_r0_rooms_roomId_send_eventType_txnId">matrix.org</a>
	 * @param roomId The unique ID of the room (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 * @param eventType The type of event to send (e.g. "m.room.message")
	 * @param content Event content (e.g. '{"msgtype": "m.text", "body": "I know kung fu" }')
	 * @return Valid Json response containing the event id generated by the server
	 */
	JsonObject roomSendEvent(String roomId, String eventType, JsonObject content) throws RestfulHTTPException, IOException;

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
	 * @param roomAlias The alias of a given room.
	 * @return Valid Json response containing the room ID.
	 */
	JsonObject deleteRoomAlias(String roomAlias) throws RestfulHTTPException, IOException;

	/**
	 * This endpoint is used to set the current rooms Room Alias.
	 * @param roomAlias The rquested roomAlias.
	 * @param roomId The rooms ID.
	 * @return Valid Json response containing the Room ID.

	 */
	JsonObject putRoomAlias(String roomAlias, String roomId) throws RestfulHTTPException, IOException;

	/**
	 * Used to create a room.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_creation/post_matrix_client_r0_createRoom">matrix.org</a>
	 * @param roomCreation JsonObject containing all required and optional parameters for room creation.
	 * @return Valid Json response containing the Room ID.
	 */
	JsonObject postCreateRoom(JsonObject roomCreation) throws RestfulHTTPException, IOException;

	/**
	 * Used to join a given room.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_membership/post_matrix_client_r0_rooms_roomId_join">matrix.org</a>
	 * @param roomId The rooms ID.
	 * @param thirdPartySigned Proof of invite from third party entity.
	 * @return Valid Json response containing the Room ID.   CURRENTLY RETURNING NOTHING, BUT THE HTTP REQUEST IS GOING THROUGH

	 */
	JsonObject postJoinRoom(String roomId, JsonObject thirdPartySigned) throws RestfulHTTPException, IOException;

	/**
	 * Used to leave a given room.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_membership/post_matrix_client_r0_rooms_roomId_leave">matrix.org</a>
	 * @param roomId The rooms ID.
	 */
	void postLeaveRoom(String roomId) throws RestfulHTTPException, IOException;
	/**
	 * Used to kick someone from a given room.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_membership/post_matrix_client_r0_rooms_roomId_kick">matrix.org</a>
	 * @param roomId The rooms ID.
	 * @param reason The given reason for posting the kick.
	 * @param userId The userId of the one being kicked.
	 */
	void postKickFromRoom(String roomId, String reason, String userId) throws RestfulHTTPException, IOException;

	/**
	 * Sends an invite.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_membership/post_matrix_client_r0_rooms_roomId_invite_0">matrix.org</a>
	 * @param roomId The rooms ID
	 * @param idServer The hostname+port of the identity server which should be used for third party identifier lookups.
	 * @param address  The invitee's third party identifier.
	 * @param medium The kind of address being passed in the address field, for example email.
	 */
	void postInviteToRoom(String roomId, String address, String idServer, String medium/*contains "id_server","medium" and "address", or simply "user_id"*/) throws RestfulHTTPException, IOException;

	/**
	 * Sends invite to specific matrix user.
	 * @param roomId The rooms ID
	 * @param userId The invitees user ID
	 */
	void postInviteToRoom(String roomId, String userId) throws RestfulHTTPException, IOException;

	/**
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/User_data/get_matrix_client_r0_account_3pid">matrix.org</a>
	 * @return Valid Json response containing the Room ID.
	 */
	JsonObject get3Pid() throws RestfulHTTPException, IOException;

	/**
	 * Download a file
	 * @param mediaUrl The URL for the media content (e.g. "mxc://matrix.org/wefh34uihSDRGhw34")
	 * @see <a href="https://matrix.org/docs/api/client-server/#!/Media/get_matrix_media_r0_download_serverName_mediaId">matrix.org</a>
	 * @return An InputStream from which the media content can be read
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	InputStream getMediaContent(URL mediaUrl) throws RestfulHTTPException, IOException;
}
