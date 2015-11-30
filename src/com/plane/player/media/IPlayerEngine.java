package com.plane.player.media;

import java.util.List;

import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.plane.player.media.PlayerEngineImpl.PlaybackMode;

public interface IPlayerEngine {

	void play();

	void playAsync();

	void reset();

	void stop();

	void pause();

	void previous();

	void next();

	void skipTo(int index);

	void forward(int time);

	void rewind(int time);

	boolean isPlaying();

	boolean isPause();

	String getPlayingPath();

	void setPlayingPath(String path);

	void setMediaPathList(List<String> pathList);

	void start();

	void setOnCompletionListener(OnCompletionListener onCompletionListener);

	void setOnPreparedListener(OnPreparedListener OnPreparedListener);

	void setOnBufferingUpdateListener(
			OnBufferingUpdateListener OnBufferingUpdateListener);

	void setPlaybackMode(PlaybackMode playbackMode);

	PlaybackMode getPlayMode();

	String getCurrentTime();

	String getDurationTime();

	int getDuration();

	int getCurrentPosition();

}
