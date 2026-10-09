package work.kubas.microppDsc.storage;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Logger;

public class SidebarStore extends AbstractStore {

    public SidebarStore(String dbPath, Logger log) throws SQLException {
        super(dbPath, log);
    }

    @Override
    protected void createTable() throws SQLException {
        Statement stmt = con.createStatement();
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS players_sidebars (
                        mc_uuid TEXT PRIMARY KEY,
                        is_hidden INTEGER NOT NULL DEFAULT FALSE
                    )
                """);
    }

    public void insertPlayer(Player player) throws SQLException {
        String sql = "INSERT INTO players_sidebars (mc_uuid) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        }
    }

    public Optional<Boolean> isHidden(Player player) throws SQLException {
        String sql = "SELECT is_hidden FROM players_sidebars WHERE mc_uuid = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getBoolean("is_hidden")) : Optional.empty();
            }
        }
    }

    public void hideSidebar(Player player) throws SQLException {
        String sql = "UPDATE players_sidebars SET is_hidden = TRUE WHERE mc_uuid = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        }
    }

    public void showSidebar(Player player) throws SQLException {
        String sql = "UPDATE players_sidebars SET is_hidden = FALSE WHERE mc_uuid = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        }
    }
}
