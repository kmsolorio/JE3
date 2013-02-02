package classes;

import java.io.IOException;

/**
 * This interface defines basic character sequence tokenizing capabilities.
 * It can serve as the underpinnings of simple parser.
 * <p>
 *     The methods of this class fall into three categories:
 *     <ul>
 *         <li>
 *             methods to configure the tokenizer, such as {@link #skipSpaces} and
 *             {@link #tokenizeWords}.
 *         </li>
 *         <li>
 *             methods to read a token: {@link #next}, {@link #nextChar}, and
 *             {@link #scan(char, boolean, boolean, boolean)}.
 *         </li>
 *         <li>
 *             methods to query the current token, such as {@link #tokenType},
 *             {@link #tokenText} and {@link #tokenKeyword}.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     In its default state, a Tokenizer performs no tokenization at all: {@link #next} returns
 *     each input character as an individual token. You must call one or more configuration methods
 *     to specify the type of tokenization to be performed. Note that the configuration methods all
 *     return the Tokenizer object so that repeated method calls can be chained.
 *     For example:
 *     <pre>
 *         Tokenizer t;
 *         t.skipSpaces().tokenizeNumbers().tokenizeWords().quotes("'#", "'\n");
 *     </pre>
 * </p>
 * <p>
 *     One particularly important configuration method is {@link #maximumTokenLength} which is
 *     used to specify the maximum token length in the input. A Tokenizer implementation must
 *     ensure that it can handle tokens at least this long, typically by allocating a buffer at
 *     lest that long.
 * </p>
 * <p>
 *     The constant fields of this interface are token type constants. Note that their values
 *     are all negative. Non-negative token types always represent Unicode characters.
 * </p>
 * <p>
 *     A tokenizer may be in one of three states:
 *     <ol>
 *         <li>
 *             Before any tokens have been read. In this state, {@link #tokenType} always returns
 *             (@link #BOF), and {@link #tokenLine} always returns 0. {@link #maximumTokenLength}
 *             and {@link #trackPosition} may only be called in this state.
 *         </li>
 *         <li>
 *             During tokenization. In this state, {@link #next}, {@link #nextChar}, and
 *             {@link #scan(char, boolean, boolean, boolean)} are being called to tokenize
 *             input characters, but none of these methods has yet returned {@link #EOF}.
 *             Configuration methods other than those listed above may be called from this state
 *             to dynamically change tokenizing behavior.
 *         </li>
 *         <li>
 *             End-of-file. Once one of the tokenizing methods have returned EOF, the tokenizer
 *             has reached the end of its input. Any subsequent calls to the tokenizing methods
 *             or to {@link #tokenType} will return EOF. Most methods may still be called from
 *             this state, although it is not useful to do so.
 *         </li>
 *     </ol>
 * </p>
 * @author David Flanagan
 */

public interface Tokenizer {
    // The following are token type constants.
    /** End-of-file. Returned when there are no more characters to tokenize */
    public static final int EOF = -1;
    /** The token is a run of whitespace. @see #tokenizeSpaces() */
    public static final int SPACE = -2;
    /** The token is a run of digits. @see #tokenizeNumbers() */
    public static final int NUMBER = -3;
    /** The token is a run of word characters. @see #tokenizeWords() */
    public static final int WORD = -4;
    /** The token is a keyword. @see #keywords() */
    public static final int KEYWORD = -5;
    /** The token is arbitrary text returned by {@link #scan(char, boolean, boolean, boolean)}. */
    public static final int TEXT = -6;
    /** Beginning-of-file. This is the value returned by {@link #tokenType} when it is
     * called before tokenization begins.
     */
    public static final int BOF = -7;
    /** Special return value for {@link #scan(char, boolean, boolean, boolean)}. */
    public static final int OVERFLOW = -8; // internal buffer overflow

    /**
     *  Specify whether to skip or return them.
     *  @param skip if false (the default), then return whitespace characters or tokens.
     *              If true, then next() never returns whitespace.
     *  @return this Tokenizer object for method chaining.
     *  @see #tokenizeSpaces
     */
    public Tokenizer skipSpaces(boolean skip);

    /**
     * Specify whether adjacent whitespace characters should be coalesced into a single SPACE token.
     * This has no effect if spaces are being skipped. The default is false.
     * @param tokenize whether {@link #next} should coalesce adjacent whitespace into a single
     *                 {@link #SPACE} token.
     * @return this Tokenizer object for method chaining.
     * @see #skipSpaces
     */
    public Tokenizer tokenizeSpaces(boolean tokenize);

    /**
     * Specify whether adjacent digit characters should be coalesced into a single token. The default is
     * false.
     * @param tokenize whether {@link #next} should be coalesced adjacent digits into a single
     *                 {@link #NUNBER} token.
     * @return this Tokenizer object for method chaining.
     */
    public Tokenizer tokenizeNumbers(boolean tokenize);

    /**
     * Specify whether adjacent word characters should be coalesced into a single token.  The default is
     * false. Word characters are defined by a {@link WordRecognizer}.
     * @param tokenize wheather {@link #next} should coalesce adjacent word characters into a single
     *                 {@link #WORD} token.
     * @return this Tokenizer object for method chaining.
     * @see #wordRecognizer
     */
    public Tokenizer tokenizeWords(boolean, tokenize);

    /**
     * Specify a {@link Tokenizer.WordRecognizer} to define what constitutes a word. If set to null
     * (the default), then words are defined by {@link Character#isJavaIdentifierStart} and
     * {@link Character#isJavaIdentifierPart}.
     * This has no effect if word tokenizing has not been enabled.
     * @param wordRecognizer the {@link Tokenizer.WordRecognizer} to use.
     * @return this Tokenizer object for method chaining.
     * @see #tokenizeWords
     */
    public Tokenizer wordRecognizer(WordRecognizer wordRecognizer);

    /**
     * Specify keywords to receive special recognition.
     * If a {@link #WORD} token matches one of these keywords, then the token type will be set to
     * {@link #KEYWORD}, and {@link #tokenKeyword} will return the index of the keyword in the specified
     * array.
     * @param keywords an array of words to be treated as keywords, or null (the default) for no keywords.
     * @return this Tokenizer object for method chaining.
     * @see #tokenizeWords
     */
    public Tokenizer keywords(String[] keywords);

    /**
     * Specify whether the tokenizer should keep track of the line number and column number for each
     * returned token. The default is false.
     * If set to true, then tokenLine() and tokenColumn() return the line and column numbers of the
     * current token.
     * @param track wheather to track the line and column numbers for each token.
     * @return this Tokenizer object for method chaining.
     * @throws java.lang.IllegalStateException if invoked after tokenizing begins
     * @see #tokenizeWords
     */
    public Tokenizer trackPosition(boolean track);
}
