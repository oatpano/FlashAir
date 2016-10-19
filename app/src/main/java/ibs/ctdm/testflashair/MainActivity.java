package ibs.ctdm.testflashair;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ibs.ctdm.flashair.FlashAirFileInfo;
import ibs.ctdm.flashair.FlashAirUtils;
//dev
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void getFileCount(final String dir) {
        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {
                return FlashAirUtils.getFileCount(dir);
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(getString(R.string.image_count_format, result));
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
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(new FileListAdapter(MainActivity.this, result));
            }
        }.execute();
    }

    private class FileListAdapter extends ArrayAdapter<FlashAirFileInfo> {
        public FileListAdapter(Context context, List<FlashAirFileInfo> data) {
            super(context, android.R.layout.simple_list_item_1, data);
        }
    }
}
