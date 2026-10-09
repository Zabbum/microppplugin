package work.kubas.microppDsc.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public abstract class AbstractStore {

    protected final Connection con;
    protected final Logger log;

    public AbstractStore(String dbPath, Logger log) throws SQLException {
        this.log = log;
        this.con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement stmt = con.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        createTable();
    }

    protected void createTable() throws SQLException {
        throw new RuntimeException("Not implemented yet");
    }

    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            log.warning("Failed to close database connection: " + e.getMessage());
        }
    }
}
