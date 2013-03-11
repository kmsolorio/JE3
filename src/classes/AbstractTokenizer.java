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

    public int tokenLine() {
        if (trackPosition && tokenStart < numChars) return tokenLine;
        else return 0;
    }

    public int tokenColumn() {
        if (trackPosition && tokenStart < numChars) return tokenColumn;
        else return 0;

    }

    public int tokenKeyword() {
        if (tokenType == KEYWORD) return tokenKeyword;
        else return -1;
    }

    public int next() throws IOException {
        int quoteindex;
        beginNewToken();
        if (eof) return tokenType = EOF;

        char c = text[p];

        if ((skipSpaces || tokenizeSpaces) && Character.isWhitespace(c)) {
            tokenType = SPACE;
            do {
                if (trackPosition) updatePosition(text[p]);
                p ++;
                if (p >= numChars) eof = !fillBuffer();
            } while (!eof && Character.isWhitespace(text[p]));

            tokenEnd = p;
        }
        else if (tokenizeNumbers && Character.isDigit(c)){
            tokenType = NUMBER;
            do {
                if (trackPosition) column ++;
                p ++;
                if (p >= numChars) eof = !fillBuffer();
            } while (!eof && Character.isDigit(text[p]));

            tokenEnd = p;
        }
        else if (tokenizeWords &&
                    (wordRecognizer != null
                        ?wordRecognizer.isWordStart(c)
                        :Character.isJavaIdentifierStart(c))) {
            tokenType = WORD;
            do {
                if (trackPosition) column ++;
                p ++;
                if (p > numChars) eof = !fillBuffer();
            } while (!eof &&
                        (wordRecognizer != null
                            ?wordRecognizer.isWordPart(text[p], c)
                            :Character.isJavaIdentifierPart(text[p])));

            if (keywordMap != null) {
                String ident = new String(text, tokenStart, p - tokenStart);
                Integer index = (Integer) keywordMap.get(ident);
                if (index != null) {
                    tokenType = KEYWORD;
                    tokenKeyword = index.intValue();
                }
                tokenEnd = p;
            }
        }
        else if (testquotes && (quoteindex = openquotes.indexOf(c)) != -1) {
            if (trackPosition) column ++;
            p ++;
            char closequote = closequotes.charAt(quoteindex);
            scan(closequote,  false, false, true);
            tokenType = c;
        }
        else {
            if (trackPosition) updatePosition(text[p]);
            tokenType = text[p];
            p ++;
            tokenEnd = p;
        }
        assert text != null && 0 <= tokenStart && tokenStart <= tokenEnd &&
                tokenEnd <= p && p <= numChars && numChars <= text.length;
        return tokenType;
    }

    public int nextChar() throws IOException {
        beginNewToken();
        if (eof) return tokenType = EOF;
        tokenType = text[p];
        if (trackPosition) updatePosition(text[p]);
        tokenEnd = ++p;

        assert text != null && 0 <= tokenStart && tokenStart <= tokenEnd &&
                tokenEnd <= p && p <= numChars && numChars <= text.length;
        return tokenType;
    }
}
