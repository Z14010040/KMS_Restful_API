package com.sansec.common.crypto.bip39.wordlists;


import com.sansec.common.crypto.bip39.WordList;

public abstract class BaseWordList implements WordList {

	abstract protected String[] getWords();
	
	@Override
	public String getWord(final int index) {
		return getWords()[index];
	}

	@Override
	public int getIndex(final String word) {
		String[] words = getWords();
		int size = words.length;
		if(word == null) {
			for(int i = 0; i < size; i++)
				if(words[i] == null)
					return i;
		} else {
			for(int i = 0; i < size; i++)
				if(word.equals(words[i]))
					return i;
		}
		return -1;
	}

	@Override
    public char getSpace() {
        return ' ';
    }
}
