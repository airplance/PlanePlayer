package com.plane.player.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.plane.player.R;
import com.plane.player.activity.HomeActivity;
import com.plane.player.utils.Constants;

public class UpdateUiBroadcastReceiver extends BroadcastReceiver {
	private HomeActivity mainActivity;

	public UpdateUiBroadcastReceiver(HomeActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.UPDATE_UI_ACTION.equals(intent.getAction())) {
			mainActivity.findViewById(R.id.list_back).setVisibility(
					View.INVISIBLE);
		}

	}

}
