package com.reflexit.magiccards.core.model.storage.db;

import java.sql.SQLException;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DBException extends SQLException {

    /**
     * New Database Exception
     * @param s
     */
    public DBException(String s) {
        super(s);
    }
}
