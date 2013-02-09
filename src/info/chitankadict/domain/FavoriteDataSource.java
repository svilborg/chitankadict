package info.chitankadict.domain;

import info.chitankadict.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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
		Word newWord = new Word();

		if (word != null && word.getName() != null) {

			ContentValues values = new ContentValues();

			values.put(DbHelper.COLUMN_NAME, word.getName());
			values.put(DbHelper.COLUMN_TITLE, word.getTitle());
			values.put(DbHelper.COLUMN_MEANING, word.getMeaning());
			values.put(DbHelper.COLUMN_MISSPELS, word.getMisspells());

			ArrayList<String> wordSyns = word.getSynonyms();

			JSONArray jsonSyns = new JSONArray(wordSyns);

			values.put(DbHelper.COLUMN_SYNONYMS, jsonSyns.toString());

			long insertId = database.insert(DbHelper.TABLE, null, values);

			Log.d(MainActivity.class.getSimpleName(), "LAST INSERT ID ::" + insertId);

			Cursor cursor = database.query(DbHelper.TABLE, allColumns, DbHelper.COLUMN_NAME + " = ? ", new String[] { word.getName() }, null, null, null);
			cursor.moveToFirst();

			newWord = cursorToWord(cursor);
			cursor.close();
		}

		return newWord;
	}

	public void delete(Word word) {
		String name = word.getName();

		database.delete(DbHelper.TABLE, DbHelper.COLUMN_NAME + " = " + name, null);
	}

	public Word getWord(String name) {
		Word word = new Word();

		Cursor cursor = database.query(DbHelper.TABLE, allColumns, DbHelper.COLUMN_NAME + " = ? ", new String[] { name }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			word = cursorToWord(cursor);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		return word;
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

	public List<String> getAllFavoriteNames() {
		List<String> names = new ArrayList<String>();

		Cursor cursor = database.query(DbHelper.TABLE, new String[] { DbHelper.COLUMN_NAME }, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			// Word word = cursorToWord(cursor);
			if (cursor != null && cursor.getString(0) != null) {
				names.add(cursor.getString(0));
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return names;
	}

	private Word cursorToWord(Cursor cursor) {
		Word word = new Word();

		// Log.d(MainActivity.class.getName(), cursor.toString());

		if (cursor != null && cursor.getString(0) != null) {
			word.setName(cursor.getString(0));
			word.setTitle(cursor.getString(1));
			word.setMeaning(cursor.getString(2));
			// word.set(cursor.getString(2));
			word.setMisspells(cursor.getString(4));
		}

		return word;
	}
}