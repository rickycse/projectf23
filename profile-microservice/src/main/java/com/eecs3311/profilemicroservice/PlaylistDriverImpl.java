package com.eecs3311.profilemicroservice;

import org.neo4j.driver.v1.*;
import org.springframework.stereotype.Repository;
import org.neo4j.driver.*;

@Repository
public class PlaylistDriverImpl implements PlaylistDriver {
	// Initialize the Neo4j driver from the ProfileMicroserviceApplication.
	Driver driver = ProfileMicroserviceApplication.driver;

	public static void InitPlaylistDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nPlaylist:playlist) ASSERT exists(nPlaylist.plName)";
				trans.run(queryStr);
				trans.success();
			} catch (Exception e) {
				if (e.getMessage().contains("An equivalent constraint already exists")) {
					System.out.println("INFO: Playlist constraint already exist (DB likely already initialized), should be OK to continue");
				} else {
					// something else, yuck, bye
					throw e;
				}
			}
			session.close();
		}
	}
	/**
	 * Method to like a song. It creates a relationship between a User and a Song node in the Neo4j database.
	 * @param playlistId The username of the user liking the song.
	 * @param songId The ID of the song being liked.
	 * @return DbQueryStatus The status of the database query including any error or success messages.
	 */
	@Override
	public DbQueryStatus likeSong(String playlistId, String songId) {
		// Initialize the query status with a default success message.
		DbQueryStatus dbQueryStatus = new DbQueryStatus("Like Song", DbQueryExecResult.QUERY_OK);

		try(Session session = driver.session()){
			// Cypher query to create a CONTAINS relationship between the Playlist and the Song.
			String query = "MATCH (p:Playlist {id: $playlistId}), (s:Song {id: $songId}) "+
					"MERGE (p)-[:CONTAINS]->(s) "+
					"RETURN s";
			// Execute the query with the provided parameters.
			StatementResult result = session.run(query, Values.parameters("playlistId", playlistId, "songId", songId));

			// Check if the query did not find the playlist or the song.
			if(!result.hasNext()){
				dbQueryStatus.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
				dbQueryStatus.setMessage("Playlist or song not found");
			} else {
				dbQueryStatus.setMessage("Song added to playlist successfully");
			}
		} catch (Exception e) {
			// Handle any exceptions by setting the query status to an error.
			dbQueryStatus.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			dbQueryStatus.setData(e.getMessage());
		}

		return dbQueryStatus;
	}


	/**
	 * Method to unlike a song. It deletes a relationship between a User and a Song node in the Neo4j database.
	 * @param playlistId The username of the user unliking the song.
	 * @param songId The ID of the song being unliked.
	 * @return DbQueryStatus The status of the database query including any error or success messages.
	 */
	@Override
	public DbQueryStatus unlikeSong(String playlistId, String songId) {
		// Initialize the query status with a default success message.
		DbQueryStatus dbQueryStatus = new DbQueryStatus("Unlike Song", DbQueryExecResult.QUERY_OK);

		try(Session session = driver.session()){
			// Cypher query to delete the CONTAINS relationship between the Playlist and the Song.
			String query = "MATCH (p:Playlist {id: $playlistId})-[r:CONTAINS]->(s:Song {id: $songId}) " +
					"DELETE r " +
					"RETURN s";
			// Execute the query with the provided parameters.
			StatementResult result = session.run(query, Values.parameters("playlistId", playlistId, "songId", songId));

			// Check if the query did not find the contains relationship.
			if(!result.hasNext()){
				dbQueryStatus.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
				dbQueryStatus.setMessage("Relationship between playlist and song not found");
			} else {
				dbQueryStatus.setMessage("Song removed from playlist successfully");
			}
		} catch (Exception e) {
			// Handle any exceptions by setting the query status to an error.
			dbQueryStatus.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			dbQueryStatus.setData(e.getMessage());
		}

		return dbQueryStatus;

	}
}
