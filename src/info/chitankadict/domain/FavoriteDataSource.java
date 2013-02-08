package info.chitankadict.domain;

import info.chitankadict.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FavoriteDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private String[] allColumns = { DbHelper.COLUMN_NAME, DbHelper.COLUMN_TITLE, DbHelper.COLUMN_MEANING, DbHelper.COLUMN_SYNONYMS, DbHelper.COLUMN_MISSPELS };

	public FavoriteDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Word create(Word word) {
		ContentValues values = new ContentValues();

		values.put(DbHelper.COLUMN_NAME, word.getName());
		values.put(DbHelper.COLUMN_TITLE, word.getTitle());
		values.put(DbHelper.COLUMN_MEANING, word.getMeaning());
		// values.put(DbHelper.COLUMN_SYNONYMS, word.getSynonyms());
		values.put(DbHelper.COLUMN_MISSPELS, word.getMisspells());

		long insertId = database.insert(DbHelper.TABLE, null, values);

		Log.d(MainActivity.class.getSimpleName(), "LAST INSERT ID ::" + insertId);

		Cursor cursor = database.query(DbHelper.TABLE, allColumns, DbHelper.COLUMN_NAME + " = " + word.getName(), null, null, null, null);
		cursor.moveToFirst();

		Word newWord = cursorToWord(cursor);
		cursor.close();
		return newWord;
	}

	public void delete(Word word) {
		String name = word.getName();

		database.delete(DbHelper.TABLE, DbHelper.COLUMN_NAME + " = " + name, null);
	}

	public List<Word> getAllFavorites() {
		List<Word> words = new ArrayList<Word>();

		Cursor cursor = database.query(DbHelper.TABLE, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Word word = cursorToWord(cursor);
			words.add(word);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return words;
	}

	private Word cursorToWord(Cursor cursor) {
		Word word = new Word();
		// word.set(cursor.getLong(0));
		// word.setWord(cursor.getString(1));
		return word;
	}
}