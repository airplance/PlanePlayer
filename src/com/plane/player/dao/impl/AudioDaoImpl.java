package com.plane.player.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.plane.player.dao.AudioDao;
import com.plane.player.dao.JRequestCallBack;
import com.plane.player.dao.OnlineAudioDao;
import com.plane.player.db.constants.UriConstant;
import com.plane.player.domain.Audio;
import com.plane.player.domain.OnLineAudio;

public class AudioDaoImpl extends ContextWrapper implements AudioDao,
		OnlineAudioDao {
	private Uri uri = Uri.parse(UriConstant.AUDIO_LIST_URI);
	private ContentResolver cr;

	public AudioDaoImpl(Context base) {
		super(base);
	}

	@Override
	public String getMusicPathByName(String name) {
		String path = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.parse(UriConstant.AUDIO_LIST_URI);
		String[] projection = { "audio_path" };
		String selection = "audio_name = ?";
		String[] selectionArgs = { name };
		Cursor c = cr.query(uri, projection, selection, selectionArgs, null);
		if (c.moveToFirst()) {
			path = c.getString(0);
		}
		return path;
	}

	@Override
	public List<String> getMusicPathListByName(String name) {
		List<String> musicList = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.parse(UriConstant.AUDIO_LIST_URI);
		String[] projection = { "audio_path" };
		String selection = "audio_name like ?";
		String[] selectionArgs = { "%" + name + "%" };
		Cursor c = cr.query(uri, projection, selection, selectionArgs, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				musicList.add(c.getString(0));
			}
		}
		return musicList;
	}

	@Override
	public List<Audio> getLocalAudioListByName(String name) {
		List<Audio> musicList = new ArrayList<Audio>();
		ContentResolver resolver = getContentResolver();
		String[] projection = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA };
		String selection = MediaStore.Audio.Media.DATA + " like ?";
		String[] selectionArgs = { "%" + name + "%" };
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				Audio audio = new Audio();
				audio.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
				audio.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
				audio.setPath(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				musicList.add(audio);
			}
		}
		cursor.close();
		return musicList;
	}

	@Override
	public List<String> getMusicListByPId(String id) {
		List<String> musicList = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.parse(UriConstant.AUDIO_LIST_URI);
		String[] projection = { "audio_name" };
		String selection = "playlist_id = ?";
		String[] selectionArgs = { id };
		Cursor c = cr.query(uri, projection, selection, selectionArgs, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				musicList.add(c.getString(0));
			}
		}
		c.close();
		return musicList;

	}

	@Override
	public void addMediaToPlaylist(ContentValues values) {
		Uri uri = Uri.parse(UriConstant.AUDIO_LIST_URI);
		ContentResolver cr = getContentResolver();
		cr.insert(uri, values);
	}

	@Override
	public void removeAudioFromPlaylist(String audioId, String playlistId) {
		ContentResolver cr = getContentResolver();
		cr.delete(uri, "id = ? and playlist_id = ?", new String[] { audioId,
				playlistId });
	}

	@Override
	public List<String> getLocalAudioPathList() {
		List<String> musicList = new ArrayList<String>();
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				musicList.add(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			}
		}
		cursor.close();
		return musicList;
	}

	@Override
	public List<Audio> getAudioListByPlaylistId(String playlistId) {
		cr = getContentResolver();
		List<Audio> audioList = new ArrayList<Audio>();
		String[] projection = { "id", "audio_path", "audio_name", "playlist_id" };
		String selection = "playlist_id = ?";
		Cursor c = cr.query(uri, projection, selection,
				new String[] { playlistId }, null);
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				Audio audio = new Audio();
				c.moveToPosition(i);
				audio.setId(c.getLong(0));
				audio.setPath(c.getString(1));
				audio.setName(c.getString(2));
				audio.setPlaylistId(c.getString(3));
				audioList.add(audio);
			}
		}
		c.close();
		return audioList;
	}

	@Override
	public List<Audio> getLocalAudioList() {
		List<Audio> musicList = new ArrayList<Audio>();
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				Audio audio = new Audio();
				audio.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
				audio.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
				audio.setPath(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				musicList.add(audio);
			}
		}
		cursor.close();
		return musicList;
	}

	// /////////////////////////////////////////////////////////////

	@Override
	public void getJLocalOnLineAudioList(String url,
			final JRequestCallBack jCallBack) {
		HttpUtils http = new HttpUtils();
		// TODO Auto-generated method stub
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result = arg0.result;
				JSONObject jo = null;
				try {
					jo = new JSONObject(result);
					result = jo.getJSONObject("data").getString("songs");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Gson gson = new Gson();

				List<OnLineAudio> temp = gson.fromJson(result,
						new TypeToken<List<OnLineAudio>>() {
						}.getType());
				jCallBack.onSuccess(temp);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	}
}
