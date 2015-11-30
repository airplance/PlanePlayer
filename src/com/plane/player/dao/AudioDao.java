package com.plane.player.dao;

import java.util.List;

import android.content.ContentValues;

import com.plane.player.domain.Audio;

public interface AudioDao {
	List<String> getMusicListByPId(String id);

	void addMediaToPlaylist(ContentValues values);

	String getMusicPathByName(String name);

	List<Audio> getLocalAudioList();

	List<String> getMusicPathListByName(String name);

	List<Audio> getLocalAudioListByName(String name);

	void removeAudioFromPlaylist(String audioId, String playlistId);

	List<Audio> getAudioListByPlaylistId(String playlistId);

	List<String> getLocalAudioPathList();
}
