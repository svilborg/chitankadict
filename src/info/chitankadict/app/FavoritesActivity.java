/**
 *
 */
package info.chitankadict.app;

import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

import info.chitankadict.domain.DbHelper;
import info.chitankadict.domain.FavoriteDataSource;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Favorites Activity
 */
public class FavoritesActivity extends SherlockListActivity {

	private FavoriteDataSource datasource;

	/**
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		datasource = new FavoriteDataSource(this);
		datasource.open();

		ListView listView = (ListView) findViewById(android.R.id.list);

		List<String> values = datasource.getAllFavoriteNames(DbHelper.COLUMN_NAME);

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Object o = parent.getItemAtPosition(position);
				String text = o.toString();
				Intent i = getIntent();

				i.putExtra("favWord", text);

				setResult(RESULT_OK, i);

				finish();
			}
		});
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();

			break;
		}
		return true;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
