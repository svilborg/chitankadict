package info.chitankadict.app;

import info.chitankadict.domain.Word;
import info.chitankadict.parser.JsoupWordParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.protocol.HTTP;
import org.jsoup.HttpStatusException;

import com.actionbarsherlock.app.SherlockActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView; 

public class MainActivity extends SherlockActivity {

	private static final String HTTP_RECHNIK_URL = "http://rechnik.chitanka.info/w/";
	private static final String HTTP_RECHNIK_RANDOM_URL = "http://rechnik.chitanka.info/random/";

	private Word currentWord = null;
	private ShareActionProvider actionProvider = null;

	boolean mIsLargeLayout;

	private class DownloadImageTask extends AsyncTask<Object, Object, Object> {

		private String url;

		private ProgressBar progress = null;

		public DownloadImageTask(String url, final ProgressBar progress) {
			super();

			this.url = url;
			this.progress = progress;

			this.progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);

			Word word = (Word) result;
			loadWord(word);

			this.progress.setVisibility(View.GONE);
		}

		protected Object doInBackground(Object... params) {
			JsoupWordParser jsoup = new JsoupWordParser(this.url);
			Word result = new Word();

			try {
				result = jsoup.parse();
			} catch (HttpStatusException e) {

				result.setError(Word.WORD_NOT_FOUND);

			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Uri data = getIntent().getData();

		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final EditText searchText = (EditText) findViewById(R.id.searchText);
		final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

		progress.setVisibility(View.GONE);

		// Search word from Synonym Link
		if ((data != null)) {

			String synWord = data.getQueryParameter("word");

			searchText.setText(synWord);

			String searchUrl = buildSearchUrl(searchText);

			if (synWord != null) {
				new DownloadImageTask(searchUrl, progress).execute();
			}
		}

		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				cleanWord();

				String searchUrl = buildSearchUrl(searchText);

				new DownloadImageTask(searchUrl, progress).execute();
			}

		});
		
		searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {		
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					cleanWord();

					String searchUrl = buildSearchUrl(searchText);

					new DownloadImageTask(searchUrl, progress).execute();
		            return true;
		        }
		        return false;
		    }
		});

		if (savedInstanceState == null) {
			// On Start Clear the Word
			cleanWord();
		} else {

		}
	}

	private String buildSearchUrl(final EditText searchText) {
		String searchWord = searchText.getText().toString();

		try {
			searchWord = URLEncoder.encode(searchWord, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(HTTP_RECHNIK_URL);
		stringBuilder.append(searchWord);

		return stringBuilder.toString();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("currentWord", currentWord);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Word currWord = (Word) savedInstanceState
				.getSerializable("currentWord");

		if (currWord == null) {
			cleanWord();
		} else {
			loadWord(currWord);
		}

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.action_menu, menu);

		// Set file with share history to the provider and set the share intent.
		MenuItem actionItem = menu.findItem(R.id.menu_item_share);

		actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider
				.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

		assignShareIntent();

		return true;
	}

	/**
	 * Creates a sharing {@link Intent} and assign it to the Action Provider.
	 * 
	 * @return void
	 */
	private void assignShareIntent() {

		if (actionProvider != null) {
			Intent shareIntent = new Intent();

			shareIntent.setAction(Intent.ACTION_SEND);

			if (currentWord != null && currentWord.getTitle() != null) {
				shareIntent.putExtra(Intent.EXTRA_SUBJECT,
						currentWord.getTitle());

				if (currentWord.getMeaning() != null) {
					shareIntent.putExtra(Intent.EXTRA_TEXT,
							Html.fromHtml(currentWord.getMeaning()).toString());
				}
			}
			shareIntent.setType("text/plain");

			// set share
			actionProvider.setShareIntent(shareIntent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_item_random:
			final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);

			progress.setVisibility(View.GONE);

			new DownloadImageTask(HTTP_RECHNIK_RANDOM_URL, progress).execute();

			return true;
		case R.id.menu_item_about:

			showDialog();
			return true;
		}
		return false;
	}

	void showDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle(R.string.menu_about);
		dialog.setCancelable(true);

		TextView text = (TextView) dialog.findViewById(R.id.resultAbout);
		text.setText(Html.fromHtml(getText(R.string.dialog_about).toString()));

		// set up button
		Button button = (Button) dialog.findViewById(R.id.buttonAbout);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
	}

	public void cleanWord() {
		LinearLayout layoutTitle = (LinearLayout) findViewById(R.id.LinearLayoutTitle);
		LinearLayout layoutText = (LinearLayout) findViewById(R.id.LinearLayoutText);
		LinearLayout layoutMiss = (LinearLayout) findViewById(R.id.LinearLayoutMisspells);
		LinearLayout layoutSyn = (LinearLayout) findViewById(R.id.LinearLayoutSyn);
		LinearLayout layoutError = (LinearLayout) findViewById(R.id.LinearLayoutError);

		layoutTitle.setVisibility(View.GONE);
		layoutText.setVisibility(View.GONE);
		layoutMiss.setVisibility(View.GONE);
		layoutSyn.setVisibility(View.GONE);
		layoutError.setVisibility(View.GONE);
	}

	/**
	 * Applies a Word to the UI
	 * 
	 * @param word
	 */
	public void loadWord(Word word) {

		// set current Word
		currentWord = word;

		if (currentWord != null) {

			if (actionProvider != null) {
				assignShareIntent();
			}

			TextView resultTitle = (TextView) findViewById(R.id.resultTitle);
			TextView resultText = (TextView) findViewById(R.id.resultText);
			TextView resultMis = (TextView) findViewById(R.id.resultMis);
			TextView resultSyn = (TextView) findViewById(R.id.resultSyn);
			TextView resultError = (TextView) findViewById(R.id.resultError);

			LinearLayout layoutTitle = (LinearLayout) findViewById(R.id.LinearLayoutTitle);
			LinearLayout layoutText = (LinearLayout) findViewById(R.id.LinearLayoutText);
			LinearLayout layoutMiss = (LinearLayout) findViewById(R.id.LinearLayoutMisspells);
			LinearLayout layoutSyn = (LinearLayout) findViewById(R.id.LinearLayoutSyn);
			LinearLayout layoutError = (LinearLayout) findViewById(R.id.LinearLayoutError);

			resultSyn.setMovementMethod(LinkMovementMethod.getInstance());

			// TODO : Refactor
			layoutTitle.setVisibility(View.GONE);
			layoutText.setVisibility(View.GONE);
			layoutMiss.setVisibility(View.GONE);
			layoutSyn.setVisibility(View.GONE);
			layoutError.setVisibility(View.GONE);

			final EditText searchText = (EditText) findViewById(R.id.searchText);

			if (word.getName() != null) {
				searchText.setText(word.getName());
			}

			if (word.getMeaning() != null) {
				resultText.setText(Html.fromHtml(word.getMeaning()).toString());
				layoutText.setVisibility(View.VISIBLE);
			}

			if (word.getTitle() != null) {
				resultTitle.setText(Html.fromHtml(word.getTitle()).toString());
				layoutTitle.setVisibility(View.VISIBLE);
			}

			if (word.getMisspells() != null
					&& !("").equals(word.getMisspells())) {

				resultMis
						.setText(Html.fromHtml(word.getMisspells()).toString());
				layoutMiss.setVisibility(View.VISIBLE);
			}

			if (word.getSynonyms() != null && !word.getSynonyms().isEmpty()) {

				ArrayList<String> synonyms = word.getSynonyms();

				resultSyn.setText("");

				for (Iterator<String> iterator = synonyms.iterator(); iterator
						.hasNext();) {
					String string = (String) iterator.next();

					string = "<a href='info.chitankadict.app://?word=" + string
							+ "'>" + string + "</a>";

					resultSyn.append(Html.fromHtml(string));

					if (iterator.hasNext()) {
						resultSyn.append(",  ");
					}
				}

				layoutSyn.setVisibility(View.VISIBLE);
			}

			if (word.getError() > 0) {
				if (word.getError() > 0) {
					if (word.getError() == Word.WORD_NOT_FOUND) {
						resultError.setText(R.string.WordNotFound);
					} else if (word.getError() == Word.WORD_MISSPELLED) {

						String errorCorrect = word.getName()
								+ " е грешно изписване на " + word.getCorrect();

						resultError.setText(errorCorrect);
					} else {
						resultError.setText(R.string.HTTPError);
					}
				}

				layoutError.setVisibility(View.VISIBLE);
			}
		}
	}
}