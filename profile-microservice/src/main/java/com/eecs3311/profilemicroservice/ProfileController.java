package com.eecs3311.profilemicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eecs3311.profilemicroservice.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class ProfileController {
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_FULLNAME = "fullName";
	public static final String KEY_USER_PASSWORD = "password";
	public static final String KEY_FRIEND_USER_NAME = "friendUserName";
	public static final String KEY_SONG_ID = "songId";

	@Autowired
	private final ProfileDriverImpl profileDriver;

	@Autowired
	private final PlaylistDriverImpl playlistDriver;

	OkHttpClient client = new OkHttpClient();

	public ProfileController(ProfileDriverImpl profileDriver, PlaylistDriverImpl playlistDriver) {
		this.profileDriver = profileDriver;
		this.playlistDriver = playlistDriver;
	}

	@RequestMapping(value = "/addSongNode/{songId}", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addSongNode(@PathVariable("songId") String songId, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("POST %s", Utils.getUrl(request)));

		//TODO: Add the parameters - String userName, String fullName, String password
		DbQueryStatus dbQueryStatus = playlistDriver.addSongNode(songId);

		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/deleteSongNode/{songId}", method = RequestMethod.DELETE)
	public ResponseEntity<Map<String, Object>> deleteSongNode(@PathVariable("songId") String songId, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("DELETE %s", Utils.getUrl(request)));

		//TODO: Add the parameters - String userName, String fullName, String password
		DbQueryStatus dbQueryStatus = playlistDriver.deleteSongNode(songId);

		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("POST %s", Utils.getUrl(request)));

		//TODO: Add the parameters - String userName, String fullName, String password
		String userName = params.get("userName");
		String fullName = params.get("fullName");
		String password = params.get("password");
		DbQueryStatus dbQueryStatus = profileDriver.createUserProfile(userName, fullName, password);

		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/followFriend", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> followFriend(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById
		String userName = params.get("userName");
		String friendUsername = params.get("friendUserName");
		DbQueryStatus dbQueryStatus = profileDriver.followFriend(userName, friendUsername);

		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/getAllFriendFavouriteSongTitles/{userName}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllFriendFavouriteSongTitles(@PathVariable("userName") String userName,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById
		DbQueryStatus dbQueryStatus = profileDriver.getAllSongFriendsLike(userName);
		response.put("message", dbQueryStatus.getMessage());
		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("data", dbQueryStatus.getData());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}


	@RequestMapping(value = "/unfollowFriend", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> unfollowFriend(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById
		String userName = params.get("userName");
		String friendUserName = params.get("friendUserName");

		DbQueryStatus dbQueryStatus = profileDriver.unfollowFriend(userName, friendUserName);
		response.put("message", dbQueryStatus.getMessage());
		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("data", dbQueryStatus.getData());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/likeSong", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> likeSong(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById

		//TODO: Add the parameters - String userName, String songId
		String userName = params.get("userName");
		String songId = params.get("songId");
		DbQueryStatus dbQueryStatus = playlistDriver.likeSong(userName, songId);

		response.put("message", dbQueryStatus.getMessage());
		response.put("status", dbQueryStatus.getdbQueryExecResult());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/unlikeSong", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> unlikeSong(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById

		//TODO: Add the parameters - String userName, String songId
		String userName = params.get("userName");
		String songId = params.get("songId");
		DbQueryStatus dbQueryStatus = playlistDriver.unlikeSong(userName, songId);

		response.put("message", dbQueryStatus.getMessage());
		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("data", dbQueryStatus.getData());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/generateMixedPlaylist", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> generateMixedPlaylist(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		// TODO: add any other values to the map following the example in SongController.getSongById

		//TODO: Add the parameters - String userName, String songId
		String userName = params.get("userName");
		String friendUserName = params.get("friendUserName");
		DbQueryStatus dbQueryStatus = playlistDriver.generateMixedPlaylist(userName, friendUserName);

		response.put("message", dbQueryStatus.getMessage());
		response.put("status", dbQueryStatus.getdbQueryExecResult());
		response.put("data", dbQueryStatus.getData());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}
}