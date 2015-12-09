package com.plane.player.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.plane.player.R;
import com.plane.player.BelmotPlayer;
import com.plane.player.media.PlayerEngineImpl.PlaybackMode;
import com.plane.player.utils.Constants;
import com.plane.player.view.LrcView;

public class PlayerActivity extends Activity {
	private BelmotPlayer belmotPlayer;
	private ImageButton back_btn;
	private Intent intent;
	private TextView playback_audio_name_tv;

	private ImageButton playback_mode_btn;
	private TextView playback_current_time_tv;
	private TextView playback_total_time_tv;
	private SeekBar seek_bar;

	private ImageButton playback_pre_btn;

	private ImageButton playback_next_btn;

	private ImageButton playback_toggle_btn;

	private Handler seek_bar_handler = new Handler();

	private LrcView lrcView;
	private TextView lrcViewNo;
	private Runnable refresh = new Runnable() {
		public void run() {
			int currently_Progress = seek_bar.getProgress() + 100;// 加1秒
			seek_bar.setProgress(currently_Progress);
			playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
					.getCurrentTime());// 每1000m刷新歌曲音轨
			seek_bar_handler.postDelayed(refresh, 100);
			if (lrcContent != null) {
				lrcView.changeCurrent(belmotPlayer.getPlayerEngine()
						.getCurrentPosition());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = this.getIntent();
		if (null == belmotPlayer) {
			belmotPlayer = BelmotPlayer.getInstance();
		}
		setContentView(R.layout.playback_activity);

		lrcView = (LrcView) findViewById(R.id.playback_lyrics);
		lrcViewNo = (TextView) findViewById(R.id.playback_lyrics_no);

		back_btn = (ImageButton) findViewById(R.id.playback_list);
		back_btn.setOnTouchListener(back_btn_listener);

		playback_audio_name_tv = (TextView) findViewById(R.id.playback_audio_name);
		String path[] = belmotPlayer.getPlayerEngine().getPlayingPath()
				.split("/");
		if (path.length > 1) {
			playback_audio_name_tv.setText(path[path.length - 1]);
		} else {
			playback_audio_name_tv.setText(path[0]);
		}
		playback_mode_btn = (ImageButton) findViewById(R.id.playback_mode);
		initPlayBackMode();
		playback_mode_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OnClickPlayBackMode();
			}
		});

		playback_current_time_tv = (TextView) findViewById(R.id.playback_current_time);
		playback_total_time_tv = (TextView) findViewById(R.id.playback_total_time);

		if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
				&& null != belmotPlayer.getPlayerEngine().getPlayingPath()) {
			playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
					.getCurrentTime());
			playback_total_time_tv.setText(belmotPlayer.getPlayerEngine()
					.getDurationTime());
		}

		seek_bar = (SeekBar) findViewById(R.id.playback_seeker);
		seek_bar.setOnSeekBarChangeListener(seekbarListener);

		playback_pre_btn = (ImageButton) findViewById(R.id.playback_pre);
		playback_pre_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				seek_bar_handler.removeCallbacks(refresh);
				belmotPlayer.getPlayerEngine().previous();
				doReSetSong();
			}
		});
		playback_next_btn = (ImageButton) findViewById(R.id.playback_next);
		playback_next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				seek_bar_handler.removeCallbacks(refresh);
				belmotPlayer.getPlayerEngine().next();
				doReSetSong();
			}
		});

		playback_toggle_btn = (ImageButton) findViewById(R.id.playback_toggle);

		playback_toggle_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play();

			}
		});
		if (belmotPlayer.getPlayerEngine().isPlaying()) {
			seek_bar.setMax(Integer.valueOf(belmotPlayer.getPlayerEngine()
					.getDuration()));
			seek_bar_handler.postDelayed(refresh, 1000);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.pause_button_default);
		} else {
			playback_toggle_btn
					.setBackgroundResource(R.drawable.play_button_default);
		}

		belmotPlayer.getPlayerEngine().setOnCompletionListener(
				new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						seek_bar_handler.removeCallbacks(refresh);
						belmotPlayer.getPlayerEngine().next();
						doReSetSong();
					}
				});

		initLrc();
	}

	OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
						&& null != belmotPlayer.getPlayerEngine()
								.getPlayingPath()) {
					seek_bar_handler.removeCallbacks(refresh);
					playback_current_time_tv.setText(belmotPlayer
							.getPlayerEngine().getCurrentTime());
				}
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
					&& null != belmotPlayer.getPlayerEngine().getPlayingPath()) {
				belmotPlayer.getPlayerEngine().forward(seekBar.getProgress());
				seek_bar_handler.postDelayed(refresh, 1000);
			} else {
				seek_bar.setProgress(0);
			}
		}
	};

	private OnTouchListener back_btn_listener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				setResult(Constants.MENU_TO_PLAYER_RESULT_CODE, intent);
				finish();
			}
			return false;
		}
	};

	@Override
	protected void onResume() {
		if (belmotPlayer.getPlayerEngine().getPlayingPath() != ""
				&& null != belmotPlayer.getPlayerEngine().getPlayingPath()) {
			seek_bar.setProgress(belmotPlayer.getPlayerEngine()
					.getCurrentPosition());
		}
		super.onResume();
	}

	private void play() {
		if (belmotPlayer.getPlayerEngine().isPlaying()) {
			belmotPlayer.getPlayerEngine().pause();
			seek_bar_handler.removeCallbacks(refresh);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.play_button_default);
		} else if (belmotPlayer.getPlayerEngine().isPause()) {
			belmotPlayer.getPlayerEngine().start();
			seek_bar_handler.postDelayed(refresh, 1000);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.pause_button_default);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 上一首/下一首控件的reset操作
	 */
	public void doReSetSong() {
		// seek_bar_handler.removeCallbacks(refresh);
		seek_bar.setProgress(0);
		seek_bar.setMax(Integer.valueOf(belmotPlayer.getPlayerEngine()
				.getDuration()));
		playback_current_time_tv.setText(belmotPlayer.getPlayerEngine()
				.getCurrentTime());
		playback_total_time_tv.setText(belmotPlayer.getPlayerEngine()
				.getDurationTime());
		String path[] = belmotPlayer.getPlayerEngine().getPlayingPath()
				.split("/");
		if (path.length > 1) {
			playback_audio_name_tv.setText(path[path.length - 1]);
		} else {
			playback_audio_name_tv.setText(path[0]);
		}
		lrcView.setVisibility(View.INVISIBLE);
		lrcViewNo.setVisibility(View.VISIBLE);
		lrcViewNo.setText("搜索歌词中。。。");
		tag = SEARCH;
		lrcView.setReSetLrc();
		initLrc();
		seek_bar_handler.postDelayed(refresh, 1000);
	}

	public void initPlayBackMode() {
		PlaybackMode mode = belmotPlayer.getPlayerEngine().getPlayMode();
		int brid = R.drawable.playmode_sequence_default;
		if (mode == PlaybackMode.NORMAL) {
			brid = R.drawable.playmode_sequence_default;
		} else if (mode == PlaybackMode.SHUFFLE) {
			brid = R.drawable.playmode_repeate_random_default;
		} else if (mode == PlaybackMode.SHUFFLE_AND_REPEAT) {
			brid = R.drawable.playmode_repeate_all_default;
		} else if (mode == PlaybackMode.REPEAT) {
			brid = R.drawable.playmode_repeate_single_default;
		}
		playback_mode_btn.setBackgroundResource(brid);
	}

	/**
	 * 播放歌曲模式的按钮操作
	 */
	public void OnClickPlayBackMode() {
		PlaybackMode mode = belmotPlayer.getPlayerEngine().getPlayMode();
		PlaybackMode m = PlaybackMode.NORMAL;
		int brid = R.drawable.playmode_sequence_default;
		if (mode == PlaybackMode.NORMAL) {
			m = PlaybackMode.SHUFFLE;
			brid = R.drawable.playmode_repeate_random_default;
		} else if (mode == PlaybackMode.SHUFFLE) {
			m = PlaybackMode.SHUFFLE_AND_REPEAT;
			brid = R.drawable.playmode_repeate_all_default;
		} else if (mode == PlaybackMode.SHUFFLE_AND_REPEAT) {
			m = PlaybackMode.REPEAT;
			brid = R.drawable.playmode_repeate_single_default;
		} else if (mode == PlaybackMode.REPEAT) {
			m = PlaybackMode.NORMAL;
			brid = R.drawable.playmode_sequence_default;
		}
		playback_mode_btn.setBackgroundResource(brid);
		belmotPlayer.getPlayerEngine().setPlaybackMode(m);
	}

	private HttpUtils http = new HttpUtils();

	private RequestCallBack<String> callB = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			String result = arg0.result;
			try {
				if (tag == SEARCH) {
					JSONObject jo = new JSONObject(result);
					jo = jo.getJSONArray("song").getJSONObject(0);
					songid = jo.getString("songid");
					tag = LRC;
					initLrc();
				} else if (tag == LRC) {
					JSONObject jo = new JSONObject(result);
					lrcContent = jo.getString("lrcContent");
					lrcView.setLrc(lrcContent);
					lrcView.setVisibility(View.VISIBLE);
					lrcViewNo.setVisibility(View.INVISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				lrcContent = "出错";
				lrcViewNo.setText("暂无歌词");
			}
		}
	};

	private final int SEARCH = 0;
	private final int LRC = 1;
	private int tag = SEARCH;
	private String songid = null, lrcContent = null;

	private void initLrc() {
		String url = "";
		switch (tag) {
		case SEARCH:
			tag = SEARCH;
			String path[] = belmotPlayer.getPlayerEngine().getPlayingPath()
					.split("/");
			String key = null;
			if (path.length > 1) {
				key = path[path.length - 1];
			} else {
				key = path[0];
			}
			key = key.replace(" ", "");
			key = key.replace(".mp3", "");
			url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&query="
					+ key;
			break;
		case LRC:
			tag = LRC;
			url = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.lry&songid="
					+ songid;
			break;
		}
		http.send(HttpMethod.GET, url, callB);
	}
}
