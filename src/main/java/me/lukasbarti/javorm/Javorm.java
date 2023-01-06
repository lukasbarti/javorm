package me.lukasbarti.javorm;

import javax.naming.OperationNotSupportedException;
import java.sql.Connection;

public class Javorm {

    public static Javorm forConnection(Connection connection) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Not yet implemented.");
    }

}
