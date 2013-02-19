package classes;

import java.util.*;
import java.io.IOException;

public abstract class AbstractTokenizer implements Tokenizer {

    boolean skipSpaces;
    boolean tokenizeSpaces;
    boolean tokenizeNumbers;
    boolean tokenizeWords;
    boolean testquotes;
    Tokenizer.WordRecognizer wordRecognizer;
    Map keywordMap;
    String openquotes, closequotes;
    boolean trackPosition;

    int maximumTokenLength = 16*1024;

    int tokenType = BOF;
    int tokenLine = 0;
    int tokenColumn = 0;
    int tokenKeyword = -1;

    int line=0, column=0;

    boolean eof;

    protected int tokenStart = 0;
    protected int tokenEnd = 0;
    protected int p = 0;
    protected int numChars = 0;
    protected char[] text = null;

    protected abstract void createBuffer(int bufferSize);
    protected abstract boolean fillBuffer() throws IOException;

    public Tokenizer skipSpaces(boolean skip) {
        skipSpaces = skip;
        return this;
    }

    public Tokenizer tokenizeSpaces(boolean tokenize) {
        tokenizeSpaces = tokenize;
        return this;
    }

    public Tokenizer tokenizeNumbers(boolean tokenize) {
        tokenizeNumbers = tokenize;
        return this;
    }

    public Tokenizer tokenizeWords(boolean tokenize) {
        tokenizeWords = tokenize;
        return this;
    }

    public Tokenizer wordRecognizer(Tokenizer.WordRecognizer wordRecognizer) {
        this.wordRecognizer = wordRecognizer;
        return this;
    }

    public Tokenizer quotes(String openquotes, String closequotes) {
        if (openquotes == null || closequotes == null)
            throw new NullPointerException("arguments must be non-null");
        if (openquotes.length() != closequotes.length())
            throw new IllegalArgumentException("argument lengths differ");
        this.openquotes = openquotes;
        this.closequotes = closequotes;
        this.testquotes = openquotes.length() > 0;
        return this;
    }

    public Tokenizer trackPosition(boolean track) {
        if (text != null) throw new IllegalStateException();
        trackPosition = track;
        return this;
    }

    public Tokenizer keywords(String[] keywords) {
        if (keywords != null) {
            keywordMap = new HashMap(keywords.length);
            for (int i = 0; i < keywords.length; i ++)
                keywordMap.put(keywords[i], new Integer(i));
        }
        else keywordMap = null;
        return this;
    }

    public Tokenizer maximumTokenLength(int size) {
        if (size < 1) throw new IllegalArgumentException();
        if (text != null) throw new IllegalStateException();
        maximumTokenLength = size;
        return this;
    }

    public int tokenType() { return tokenType; }

    public String tokenText() {
        if (text == null || tokenStart >= numChars) return null;
        return new String(text, tokenStart, tokenEnd - tokenStart);
    }
}
