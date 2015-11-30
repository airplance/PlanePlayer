package com.plane.player.dao;

import java.util.List;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.plane.player.domain.OnLineAudio;

public interface JRequestCallBack {
	void onSuccess(List<OnLineAudio> list);

	void onFailure(HttpException arg0, String arg1);
}
