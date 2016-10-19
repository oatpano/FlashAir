package ibs.ctdm.testflashair;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ibs.ctdm.flashair.FlashAirFileInfo;
import ibs.ctdm.flashair.FlashAirUtils;

//dev
public class MainActivity extends AppCompatActivity {

    private TextView tvCount;
    private ListView listView;

    FileListAdapter fileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAutoUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_wifi_setting){
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_reload) {
            String dir = "/DCIM/100__TSB";
            getFileCount(dir);
            getFileList(dir);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        tvCount = (TextView) findViewById(R.id.tvCount);
        listView = (ListView) findViewById(R.id.listView);

        fileListAdapter = new FileListAdapter(this, new ArrayList<FlashAirFileInfo>());
        listView.setAdapter(fileListAdapter);
    }

    private void initAutoUpdate() {
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return FlashAirUtils.hasUpdate();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result){

                }
            }
        }.execute();
    }

    private void getFileCount(final String dir) {
        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                return FlashAirUtils.getFileCount(dir);
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                tvCount.setText(getString(R.string.image_count_format, result));
            }
        }.execute();
    }

    private void getFileList(final String dir){
        new AsyncTask<Void, Void, List<FlashAirFileInfo>>(){

            @Override
            protected List<FlashAirFileInfo> doInBackground(Void... params) {
                return FlashAirUtils.getFileList(dir);
            }

            @Override
            protected void onPostExecute(List<FlashAirFileInfo> result) {
                super.onPostExecute(result);
                fileListAdapter.setImageResults(result);
                fileListAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private class FileListAdapter extends ArrayAdapter<FlashAirFileInfo> {
        LayoutInflater mInflater;
        private List<FlashAirFileInfo> imageResults;

        public FileListAdapter(Context context, List<FlashAirFileInfo> data) {
            super(context, 0, data);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (imageResults == null){
                return 0;
            }
            return imageResults.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.view_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.tvTitle);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ivThumbnail);

            FlashAirFileInfo item = imageResults.get(position);

            textView.setText(item.mFileName);

            String url = FlashAirUtils.getThumbnailUrl(item.mDir, item.mFileName);

            Glide.with(getContext())
                    .load(url)
                    .error(android.R.drawable.sym_def_app_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            return convertView;
        }

        public void setImageResults(List<FlashAirFileInfo> imageResults){
            this.imageResults = imageResults;
        }
    }

}
