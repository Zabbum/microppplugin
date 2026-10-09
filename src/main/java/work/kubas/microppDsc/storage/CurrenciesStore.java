package work.kubas.microppDsc.storage;

import work.kubas.microppDsc.currencies.CurrencyAlreadyExistsException;
import work.kubas.microppDsc.currencies.InsufficientFundsException;
import work.kubas.microppDsc.currencies.InvalidCurrencyFormatException;
import work.kubas.microppDsc.currencies.NoSuchCurrencyException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CurrenciesStore extends AbstractStore {
    public CurrenciesStore(String dbPath, Logger log) throws SQLException {
        super(dbPath, log);
    }

    @Override
    public void createTable() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS currencies (
                            currency_id VARCHAR(3) PRIMARY KEY,
                            currency_name VARCHAR(100) NOT NULL,
                            creator_id TEXT NOT NULL
                        )
                    """);
        }

        try (Statement stmt = con.createStatement()) {
            stmt.execute("""
                                CREATE TABLE IF NOT EXISTS accounts (
                                    player_id TEXT NOT NULL,
                                    currency_id VARCHAR(3) NOT NULL,
                                    balance BIGINT NOT NULL DEFAULT 0,
                    
                                    PRIMARY KEY (player_id, currency_id),
                                    FOREIGN KEY (currency_id) REFERENCES currencies(currency_id)
                                )
                    """);
        }
    }

    public boolean doesCurrencyExist(String currencyId) throws SQLException {
        String sql = """
                SELECT 1
                FROM currencies
                WHERE currency_id = ?
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, currencyId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public synchronized void createCurrency(String currencyId, String currencyName, UUID creatorId) throws SQLException {
        if (currencyId == null ||
                currencyId.length() != 3 ||
                !currencyId.matches("[A-Z]+")) {
            throw new InvalidCurrencyFormatException(currencyId);
        }

        if (currencyName == null || currencyName.isBlank()) {
            throw new IllegalArgumentException("Currency name cannot be empty");
        }

        if (currencyName.length() > 100) {
            throw new IllegalArgumentException("Currency name is too long");
        }

        if (doesCurrencyExist(currencyId)) {
            throw new CurrencyAlreadyExistsException(currencyId);
        }

        String sql = "INSERT INTO currencies (currency_id, currency_name, creator_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, currencyId);
            ps.setString(2, currencyName);
            ps.setString(3, creatorId.toString());
            ps.executeUpdate();
        }
    }

    public List<String> getAllCurrencies() throws SQLException {
        List<String> currencies = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT currency_id FROM currencies");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                currencies.add(rs.getString("currency_id"));
            }
        }
        return currencies;
    }

    public List<String> getOwnedCurrencies(UUID playerId) throws SQLException {
        List<String> currencies = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement("""
                SELECT currency_id FROM currencies WHERE creator_id = ?
                """);
        ) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    currencies.add(rs.getString("currency_id"));
                }
            }
        }
        return currencies;
    }

    public String getCurrencyName(String currencyId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("""
                SELECT currency_name FROM currencies WHERE currency_id = ?
                """);
        ) {
            ps.setString(1, currencyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NoSuchCurrencyException(currencyId);
                }

                return rs.getString("currency_name");
            }
        }
    }

    public void printCurrency(String currencyId, long amount) throws SQLException, NoSuchCurrencyException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        UUID creatorId;

        try (PreparedStatement ps = con.prepareStatement("""
                SELECT creator_id FROM currencies WHERE currency_id = ?
                """)) {

            ps.setString(1, currencyId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NoSuchCurrencyException(currencyId);
                }

                creatorId = UUID.fromString(rs.getString("creator_id"));
            }
        }

        try (PreparedStatement ps = con.prepareStatement("""
                INSERT INTO accounts (player_id, currency_id, balance)
                VALUES (?, ?, ?)
                ON CONFLICT(player_id, currency_id)
                DO UPDATE SET balance = balance + excluded.balance
                """)) {

            ps.setString(1, creatorId.toString());
            ps.setString(2, currencyId);
            ps.setLong(3, amount);

            ps.executeUpdate();
        }
    }

    public void pay(
            UUID payerId,
            UUID receiverId,
            String currencyId,
            long amount
    ) throws SQLException, NoSuchCurrencyException, InsufficientFundsException {

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        if (payerId.equals(receiverId)) {
            throw new IllegalArgumentException("Payer and receiver cannot be the same player");
        }

        // Make sure the currency exists
        if (!doesCurrencyExist(currencyId)) {
            throw new NoSuchCurrencyException(currencyId);
        }

        try {
            con.setAutoCommit(false);

            // Make sure both accounts exist
            try (PreparedStatement ps = con.prepareStatement("""
                INSERT INTO accounts (player_id, currency_id, balance)
                VALUES (?, ?, 0)
                ON CONFLICT(player_id, currency_id)
                DO NOTHING
                """)) {

                ps.setString(1, payerId.toString());
                ps.setString(2, currencyId);
                ps.executeUpdate();

                ps.setString(1, receiverId.toString());
                ps.executeUpdate();
            }

            // Take money from payer.
            // The WHERE condition prevents the balance from becoming negative.
            try (PreparedStatement ps = con.prepareStatement("""
                UPDATE accounts
                SET balance = balance - ?
                WHERE player_id = ?
                  AND currency_id = ?
                  AND balance >= ?
                """)) {

                ps.setLong(1, amount);
                ps.setString(2, payerId.toString());
                ps.setString(3, currencyId);
                ps.setLong(4, amount);

                int updated = ps.executeUpdate();

                if (updated == 0) {
                    throw new InsufficientFundsException();
                }
            }

            // Give money to receiver
            try (PreparedStatement ps = con.prepareStatement("""
                UPDATE accounts
                SET balance = balance + ?
                WHERE player_id = ?
                  AND currency_id = ?
                """)) {

                ps.setLong(1, amount);
                ps.setString(2, receiverId.toString());
                ps.setString(3, currencyId);

                ps.executeUpdate();
            }

            con.commit();

        } catch (SQLException | RuntimeException e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(true);
        }
    }
}
