package work.kubas.microppDsc.storage;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class LinksStore extends AbstractStore {

    public LinksStore(String dbPath, Logger log) throws SQLException {
        super(dbPath, log);
    }

    @Override
    protected void createTable() throws SQLException {
        Statement stmt = con.createStatement();
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS links (
                        mc_uuid TEXT PRIMARY KEY,
                        dsc_id INTEGER NOT NULL UNIQUE
                    )
                """);
    }

    public void link(UUID minecraftUuid, long discordId) throws SQLException {
        String sql = "INSERT OR REPLACE INTO links (mc_uuid, dsc_id) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, minecraftUuid.toString());
            ps.setLong(2, discordId);
            ps.executeUpdate();
        }
    }

    public void unlink(UUID minecraftUuid) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM links WHERE mc_uuid = ?")) {
            ps.setString(1, minecraftUuid.toString());
            ps.executeUpdate();
        }
    }

    public Optional<Long> getDiscordId(UUID minecraftUuid) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT dsc_id FROM links WHERE mc_uuid = ?")) {
            ps.setString(1, minecraftUuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getLong("dsc_id")) : Optional.empty();
            }
        }
    }

    public Optional<UUID> getMinecraftUuid(long discordId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT mc_uuid FROM links WHERE dsc_id = ?")) {
            ps.setLong(1, discordId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(UUID.fromString(rs.getString("mc_uuid"))) : Optional.empty();
            }
        }
    }
}
