package info.chitankadict.parser;

import info.chitankadict.domain.Word;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupWordParser {

	static final String RSS = "rss";

	private final URL feedUrl;

	public JsoupWordParser(String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public Word parse() throws IOException {
		final Word currentWord = new Word();

		Document doc = Jsoup.connect(feedUrl.toString()).get();

		// Extract Word itself
		Elements nameBox = doc.select("h1");

		currentWord.setName(nameBox.text());

		// Extract Title
		Elements titleBox = doc.select("h2 span");

		currentWord.setTitle(titleBox.text());

		// Extract Meaning
		Elements meaningBox = doc.select("div.meaning .data");

		currentWord.setMeaning(meaningBox.html().toString());

		// Extract Synonyms
		Elements synonymsBox = doc.select("div.synonyms ul li a");

		for (Iterator<Element> iterator = synonymsBox.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();

			currentWord.addSynonym(element.text().toString());
		}

		// Extract Possible Misspellings
		Elements misspellsBox = doc.select("div.incorrect-forms li");

		currentWord.setMisspells(misspellsBox.html().toString());

		// The word is Misspelled
		Elements isMisspelled = doc.select("span.incorrect");

		if (!("").equals(isMisspelled.text())) {
			currentWord.setError(Word.WORD_MISSPELLED);

			Elements correctText = doc.select("div#content p a");

			currentWord.setCorrect(correctText.html());
		}

		return currentWord;
	}
}