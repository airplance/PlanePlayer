package com.plane.player.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.ViewInjectInfo;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.plane.player.BelmotPlayer;
import com.plane.player.R;
import com.plane.player.adapter.MusicListAdapter;
import com.plane.player.adapter.OnlineSearchAdapter;
import com.plane.player.dao.JRequestCallBack;
import com.plane.player.dao.impl.AudioDaoImpl;
import com.plane.player.domain.OnLineAudio;
import com.plane.player.utils.Constants.PopupMenu;

public class SearchMusicActivity extends BaseListActivity {
	private AudioDaoImpl audioDao = new AudioDaoImpl(this);

	@ViewInject(R.id.search_button)
	private ImageButton search_btn;
	private Context context;
	private Set<Integer> popUpMenu = new HashSet<Integer>();
	private OnlineSearchAdapter adapter;
	private List<OnLineAudio> mList = new ArrayList<OnLineAudio>();
	private ListView list;

	@ViewInject(R.id.search_edit)
	private AutoCompleteTextView autoCompleteTextView;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.search_main_activity);
		ViewUtils.inject(this);
		context = this;
		initPopupMenu();
		dialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);

		list = getListView();
		list.setOnScrollListener(scroll);
		search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoDefault();
				Gkey = autoCompleteTextView.getText().toString().trim();
				if ("".equals(Gkey)) {
					Toast.makeText(
							context,
							getResources().getString(R.string.search_edit_hint),
							Toast.LENGTH_LONG).show();
				} else {
					dialog.show();
					String key = getURL(Gkey);
					audioDao.getJLocalOnLineAudioList(key, jCallBack);
					// list.setAdapter(new MusicListAdapter(context, audioDao
					// .getLocalAudioListByName(autoCompleteTextView
					// .getText().toString()), null));
				}
			}
		});

		super.onCreate(savedInstanceState);

	}

	@Override
	protected Set<Integer> getPopUpMenu() {
		return popUpMenu;
	}

	private void initPopupMenu() {
		popUpMenu.add(PopupMenu.ADD_ALL_TO.getMenu());
		popUpMenu.add(PopupMenu.CREATE_LIST.getMenu());
		popUpMenu.add(PopupMenu.EXIT.getMenu());
		popUpMenu.add(PopupMenu.HELP.getMenu());
		popUpMenu.add(PopupMenu.SETTING.getMenu());
	}

	private JRequestCallBack jCallBack = new JRequestCallBack() {

		@Override
		public void onSuccess(List<OnLineAudio> onlinelist) {
			// TODO Auto-generated method stub
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (isDefault) {
				mList.clear();
				isDefault = false;
			}
			mList.addAll(onlinelist);
			if (adapter == null) {
				adapter = new OnlineSearchAdapter(context, mList, null);
				list.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			BelmotPlayer.getInstance().getPlayerEngine().setmListOnLine(mList);
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub

		}
	};

	private OnScrollListener scroll = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 == OnScrollListener.SCROLL_STATE_IDLE && isBotton) {
				String key = getURL(Gkey);
				dialog.show();
				audioDao.getJLocalOnLineAudioList(key, jCallBack);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			if (visibleItemCount + firstVisibleItem == totalItemCount) {
				isBotton = true;
			} else {
				isBotton = false;
			}
		}
	};

	private void DoDefault() {
		isDefault = true;
		page = 1;
		Gkey = "";
	}

	private String getURL(String search) {
		String url = KeyUrl + "&page=" + page + "&keyword=" + search;
		page++;
		return url;
	}

	private int page = 1;// 搜索的字符串的page数
	private String Gkey = "";// 搜索的字符串
	private boolean isBotton = false;// 是不是到达底部
	private final static String KeyUrl = "http://lib9.service.kugou.com/websearch/index.php?cmd=100&pagesize=0";
	private boolean isDefault = false;// 是不是点击后恢复默认数据

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (adapter != null) {
			adapter.initPlayerEngine();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
