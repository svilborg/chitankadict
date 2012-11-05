package info.chitankadict.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Word implements Comparable<Word>, Serializable {

	public static final int WORD_NOT_FOUND = 1;
	public static final int WORD_MISSPELLED = 2;

	private static final long serialVersionUID = 188899988812L;

	private String name = null;
	private String title = null;

	private String meaning = null;
	private ArrayList<String> synonyms;

	private String misspells;
	private String correct;
	private int error = 0; 

	public Word() {
		this.synonyms = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		if (!("").equals(name)) {
			this.name = name.trim();
		}
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (!("").equals(title)) {
			this.title = title.trim();
		}
	}

	public String getMisspells() {
		return misspells;
	}

	public void setMisspells(String misspells) {
		this.misspells = misspells.trim();
	}

	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	public void addSynonym(String synonym) {
		this.synonyms.add(synonym);
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		if (!("").equals(meaning)) {
			this.meaning = meaning.trim();
		}
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public Word copy() {
		Word copy = new Word();
		copy.name = name;
		copy.title = title;
		copy.meaning = meaning;
		copy.synonyms = synonyms;
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(name);
		sb.append('\n');
		sb.append("Title: ");
		sb.append(title);
		sb.append('\n');
		sb.append("Meaning: ");
		sb.append(meaning);
		sb.append('\n');
		sb.append("Synonyms: ");
		sb.append(synonyms);
		sb.append('\n');
		sb.append("Misspells: ");
		sb.append(misspells);
		sb.append('\n');
		sb.append("Error: ");
		sb.append(error);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((synonyms == null) ? 0 : synonyms.hashCode());
		result = prime * result + ((meaning == null) ? 0 : meaning.hashCode());
		result = prime * result + ((error == 0) ? 0 : error);
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result
				+ ((misspells == null) ? 0 : misspells.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		if (meaning == null) {
			if (other.meaning != null)
				return false;
		} else if (!meaning.equals(other.meaning))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (misspells == null) {
			if (other.misspells != null)
				return false;
		} else if (!misspells.equals(other.misspells))
			return false;
		return true;
	}

	public int compareTo(Word another) {
		if (another == null)
			return 1;
		// sort descending by name
		return another.name.compareTo(name);
	}

}