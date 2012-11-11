/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.bee.clang;

import java.math.BigInteger;
import javax.xml.ws.Holder;

/**
 *
 * @author dabloem
 * ClangExceptions are thrown when the Clang WebService returns an error code > 0
 */
public class ClangException extends Exception {

    /**
     * Creates a new instance of <code>ClangException</code> without detail message.
     */
    public ClangException() {
    }


    /**
     * Constructs an instance of <code>ClangException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ClangException(String msg) {
        this(msg, null);
    }

    public ClangException(String message, Throwable exception) {
        super("Clang API returned error code: "+message, exception);
    }

    public ClangException(Holder<BigInteger> code) {
        this(code, null);
    }

    public ClangException(Holder<BigInteger> code, Throwable throwable) {
        super("Clang API returned error code: "+code.value.toString(), throwable);
    }
}
