/**
 *
 */
package info.chitankadict.app;

import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.app.ActionBar;

import info.chitankadict.domain.DbHelper;
import info.chitankadict.domain.FavoriteDataSource;
import android.content.Context;
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
public class FavoritesActivity extends SherlockListActivity implements ActionBar.OnNavigationListener {

	private FavoriteDataSource datasource;
	private ListView listView;

	/**
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

		listView = (ListView) findViewById(android.R.id.list);

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.order, R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);

		loadFavorites(DbHelper.COLUMN_NAME);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		String[] sortList = { DbHelper.COLUMN_NAME, DbHelper.COLUMN_NAME + " DESC", DbHelper.COLUMN_ID + " DESC", DbHelper.COLUMN_ID };

		String order = sortList[itemPosition];

		loadFavorites(order);

		return true;
	}

	private void loadFavorites(String order) {
		List<String> values = datasource.getAllFavoriteNames(order);

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = getIntent();

				i.putExtra("favWord", parent.getItemAtPosition(position).toString());

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
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
