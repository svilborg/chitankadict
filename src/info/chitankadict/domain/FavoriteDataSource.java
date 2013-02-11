package info.chitankadict.domain;

import info.chitankadict.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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
	private String[] allColumns = { DbHelper.COLUMN_ID, DbHelper.COLUMN_NAME, DbHelper.COLUMN_TITLE, DbHelper.COLUMN_MEANING, DbHelper.COLUMN_SYNONYMS, DbHelper.COLUMN_MISSPELS, DbHelper.COLUMN_CREATED };

	public FavoriteDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Word createAndGet(Word word) {
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

			database.insertOrThrow(DbHelper.TABLE, null, values);

			Cursor cursor = database.query(DbHelper.TABLE, allColumns, DbHelper.COLUMN_NAME + " = ? ", new String[] { word.getName() }, null, null, null);
			cursor.moveToFirst();

			newWord = cursorToWord(cursor);
			cursor.close();
		}

		return newWord;
	}

	public long create(Word word) {
		if (word != null && word.getName() != null) {

			ContentValues values = new ContentValues();

			values.put(DbHelper.COLUMN_NAME, word.getName());
			values.put(DbHelper.COLUMN_TITLE, word.getTitle());
			values.put(DbHelper.COLUMN_MEANING, word.getMeaning());
			values.put(DbHelper.COLUMN_MISSPELS, word.getMisspells());

			ArrayList<String> wordSyns = word.getSynonyms();

			JSONArray jsonSyns = new JSONArray(wordSyns);

			values.put(DbHelper.COLUMN_SYNONYMS, jsonSyns.toString());

			long insertId = database.insertOrThrow(DbHelper.TABLE, null, values);

			return insertId;
		}

		return -1L;
	}

	public void delete(String name) {
		database.delete(DbHelper.TABLE, DbHelper.COLUMN_NAME + " = ? ", new String[] { name });
	}

	public void deleteByWord(Word word) {
		String name = word.getName();

		delete(name);
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

	public List<String> getAllFavoriteNames(String order) {
		List<String> names = new ArrayList<String>();

		Cursor cursor = database.query(DbHelper.TABLE, new String[] { DbHelper.COLUMN_NAME }, null, null, null, null, order);

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

		if (cursor != null && cursor.getString(0) != null) {
			word.setName(cursor.getString(0));
			word.setTitle(cursor.getString(1));
			word.setMeaning(cursor.getString(2));

			String synJson = cursor.getString(3);

			if (synJson != null) {
				try {
					JSONArray jsonArray = new JSONArray(synJson);

					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							word.addSynonym(jsonArray.getString(i));
						}
					}
				} catch (JSONException e) {
					Log.d(MainActivity.class.getName(), e.getMessage());
				}
			}

			word.setMisspells(cursor.getString(4));
		}

		return word;
	}
}