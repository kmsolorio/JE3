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

}
