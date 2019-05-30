/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.ucs.pip.exception;

/**
 * A wrapper for exceptions related to the PIP operations (i.e. errors occurred
 * during file configuration parsing, wrong attributes when creating user search
 * base, etc.) and LDAP operations (i.e. invalid credentials)
 * @author Fabio Bindi and Filippo Lauria
 */
public class PIPException extends Exception {
    private static final long serialVersionUID = 1488407629633894422L;

    public PIPException( String msg ) {
        super( msg );
    }
}
