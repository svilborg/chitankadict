package info.chitankadict.domain;

import info.chitankadict.app.MainActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String DATABASE_NAME = "favorites.db";
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE = "word";

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_MEANING = "meaning";
	public static final String COLUMN_SYNONYMS = "synonyms";
	public static final String COLUMN_MISSPELS = "misspells";

	public static final boolean debug = false;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public Cursor query(SQLiteDatabase db, String query) {
		Cursor cursor = db.rawQuery(query, null);
		if (debug) {
			Log.d(TAG, "Executing Query: " + query);
		}
		return cursor;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/* Create table Logic, once the Application has ran for the first time. */
		String sql = String.format("CREATE TABLE %s ('name' varchar(100) DEFAULT NULL, 'title' longtext,'meaning' longtext, 'synonyms' longtext,'misspells' text, PRIMARY KEY ('name') );", TABLE);

		db.execSQL(sql);

		if (debug) {
			Log.d(TAG, "onCreate Called.");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (debug) {
			Log.d(TAG, "Upgrade: Dropping Table and Calling onCreate");
			Log.w(DbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		}

		db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE));
		this.onCreate(db);
	}
}