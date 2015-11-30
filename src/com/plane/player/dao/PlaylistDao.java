package com.plane.player.dao;

import java.util.List;

import com.plane.player.domain.Playlist;

public interface PlaylistDao {
	void createPlaylist(String name);

	void removePlaylist(String id);

	List<Playlist> getAllPlaylist();

}
