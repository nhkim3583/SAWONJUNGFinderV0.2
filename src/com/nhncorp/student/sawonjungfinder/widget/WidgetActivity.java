package com.nhncorp.student.sawonjungfinder.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.nhncorp.student.sawonjungfinder.R;
import com.nhncorp.student.sawonjungfinder.constants.Constants;

public class WidgetActivity extends AppWidgetProvider {

	private static final String TAG = "WidgetActivity";

	@Override
	public void onEnabled(Context context) {
		Log.i(TAG,
				"======================= onEnabled() =======================");
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.i(TAG, "======================= onUpdate() =======================");

		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int i = 0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.activity_widget);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG,
				"======================= onDeleted() =======================");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		Log.i(TAG,
				"======================= onDisabled() =======================");
		super.onDisabled(context);
	}

	// UI 설정
	public void initUI(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i(TAG, "======================= initUI() =======================");
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.activity_widget);

		Intent widgetIntent = new Intent(Constants.WIDGET_BUTTON);

		PendingIntent widgetPIntent = PendingIntent.getBroadcast(context, 0,
				widgetIntent, 0);

		views.setOnClickPendingIntent(R.id.widgetBtn, widgetPIntent);

		// 현재 사용자가 사용하고 있는 모든 widget을 update해줘야 하기 때문에 다음 구문이 필요하다
		for (int appWidgetId : appWidgetIds) {
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	// Receiver 수신
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction();
		Log.d(TAG, "onReceive() action = " + action);

		// Default Recevier
		if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {

		} else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			initUI(context, manager, manager.getAppWidgetIds(new ComponentName(
					context, getClass())));
		} else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {

		} else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {

		}

		// Custom Recevier
		else if (Constants.WIDGET_BUTTON.equals(action)) {
			Toast.makeText(context, "Receiver 수신 완료", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
