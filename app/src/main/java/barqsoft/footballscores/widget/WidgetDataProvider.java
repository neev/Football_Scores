package barqsoft.footballscores.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by neeraja on 10/29/2015.
 */
@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsFactory {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    List<itemValues> mCollections = new ArrayList<itemValues>();
    itemValues mItemValues;

    private Cursor data = null;
    Context mContext = null;
    public String[] fragmentdate = new String[1];


    private class itemValues{


        String home_name;
        String match_time;
        String away_name;

        public itemValues(String home_name, String match_time, String away_name) {
            this.home_name = home_name;
            this.match_time = match_time;
            this.away_name = away_name;
        }

        public String getHome_name() {
            return home_name;
        }

        public void setHome_name(String home_name) {
            this.home_name = home_name;
        }

        public String getMatch_time() {
            return match_time;
        }

        public void setMatch_time(String match_time) {
            this.match_time = match_time;
        }

        public String getAway_name() {
            return away_name;
        }

        public void setAway_name(String away_name) {
            this.away_name = away_name;
        }
    }



    public void setFragmentDate(String todayDate)
    {
        fragmentdate[0] = todayDate;
    }

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate=mformat.format(date);

        setFragmentDate(todayDate);

        Uri todayWidgetUri = DatabaseContract.scores_table.buildScoreWithDate();

        data =  context.getContentResolver().query(todayWidgetUri,
                null, null, fragmentdate, null);
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_detail_list_item);
        itemValues x = mCollections.get(position);
        mView.setTextViewText(R.id.home_name, x.home_name);
        mView.setTextViewText(R.id.data_textview, x.match_time);
        mView.setTextViewText(R.id.away_name, x.away_name);
        mView.setTextColor(R.id.data_textview, Color.BLACK);
        mView.setTextColor(R.id.home_name, Color.BLACK);
        mView.setTextColor(R.id.away_name, Color.BLACK);

        final Intent fillInIntent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putString(DetailWidgetProvider.EXTRA_STRING,
                String.valueOf(position));
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        mCollections.clear();
        int i = 0;
        data.moveToFirst();
        while (!data.isAfterLast())
        {
            i++;
            String home_name = data.getString(COL_HOME);
            String match_time = data.getString(COL_MATCHTIME);
            String away_name = data.getString(COL_AWAY);
            mItemValues = new itemValues(home_name,match_time,away_name);
           mCollections.add(mItemValues);
            data.moveToNext();
        }
    }

    @Override
    public void onDestroy() {

    }

}