package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by neeraja on 10/23/2015.
 */
public class TodayWidgetIntentService extends IntentService {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    public String[] fragmentdate = new String[1];


    public TodayWidgetIntentService() {

        super("TodayWidgetIntentService");
    }
    public void setFragmentDate(String todayDate)
    {
        fragmentdate[0] = todayDate;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));


        // Get today's data from the ContentProvider

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate=mformat.format(date);

        setFragmentDate(todayDate);

        Uri todayWidgetUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor cursor = getContentResolver().query(todayWidgetUri,
                null, null, fragmentdate, null);
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        // Extract the weather data from the Cursor
        String home_name = cursor.getString(COL_HOME);
        int home_crest_image = Utilies.getTeamCrestByTeamName(cursor.getString(COL_HOME));
        String match_time = cursor.getString(COL_MATCHTIME);
        String away_name = cursor.getString(COL_AWAY);
        int away_crest_image= Utilies.getTeamCrestByTeamName(cursor.getString(COL_AWAY));


        cursor.close();



        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
            int layoutId;
            if (widgetWidth >= largeWidth) {
                layoutId = R.layout.widget_today_large;
            } else if (widgetWidth >= defaultWidth) {
                layoutId = R.layout.widget_today_large;
            } else {
                layoutId = R.layout.widget_today_large;
            }
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

                // Add the data to the RemoteViews
            views.setTextViewText(R.id.home_name, home_name);
            views.setImageViewResource(R.id.home_crest, home_crest_image);



            views.setTextViewText(R.id.data_textview,match_time);

            views.setTextViewText(R.id.away_name, away_name);
            views.setImageViewResource(R.id.away_crest , away_crest_image);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.home_crest, description);

    }
}