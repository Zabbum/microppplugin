package work.kubas.microppDsc;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import work.kubas.microppDsc.commands.ColorCommand;
import work.kubas.microppDsc.commands.PrometerCommand;
import work.kubas.microppDsc.config.PluginConfig;
import work.kubas.microppDsc.jda.DiscordBridgeManager;
import work.kubas.microppDsc.jda.DiscordMessageListener;
import work.kubas.microppDsc.jda.RoleSyncEngine;
import work.kubas.microppDsc.listeners.AchievementListener;
import work.kubas.microppDsc.listeners.ChatListener;
import work.kubas.microppDsc.listeners.JoinLeaveListener;
import work.kubas.microppDsc.listeners.PlayerDeathListener;
import work.kubas.microppDsc.storage.CurrenciesStore;
import work.kubas.microppDsc.storage.LinksStore;
import work.kubas.microppDsc.storage.SidebarStore;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public final class MicroppDsc extends JavaPlugin {
    @Getter
    private JDA jda;

    @Getter
    private LinksStore linksStore;

    @Getter
    private SidebarStore sidebarStore;

//    @Getter
//    private CurrenciesStore currenciesStore;

    @Getter
    private LinkCodesManager linkCodesManager;

    @Getter
    private ColorManager colorManager;

    @Getter
    private DiscordBridgeManager discordBridgeManager;

    @Getter
    private RoleSyncEngine roleSyncEngine;

    @Getter
    private PluginConfig pluginConfig;

    @Getter
    private Economy economy;

    private BroadCastManager broadCastManager;

    private PlayerDeathListener playerDeathListener;

    @Override
    public void onEnable() {
        getLogger().info("Initializing μPP discord plugin...");

        saveDefaultConfig();

        // DB init
        try {
            linksStore = new LinksStore(
                    getDataFolder().toPath().resolve("links.db").toString(),
                    getLogger()
            );
            sidebarStore = new SidebarStore(
                    getDataFolder().toPath().resolve("links.db").toString(),
                    getLogger()
            );
//            currenciesStore = new CurrenciesStore(
//                    getDataFolder().toPath().resolve("links.db").toString(),
//                    getLogger()
//            );

        } catch (SQLException e) {
            getLogger().severe("Failed to initiate the links DB: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // LuckPerms init
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe("LuckPerms not found! Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        var luckPerms = LuckPermsProvider.get();

        // Vault init
        try {
            setupVault();

        } catch (NoVaultException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // JDA init
        try {
            initJda();
        } catch (NoTokenException e) {
            getLogger().severe("No discord bot token set in config.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (InterruptedException e) {
            getLogger().severe("Initialization interrupted!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // Role sync engine init
        pluginConfig = new PluginConfig(getConfig());
        roleSyncEngine = new RoleSyncEngine(luckPerms, pluginConfig, getLogger());

        // Link code manager init
        linkCodesManager = new LinkCodesManager();

        // Color manager init
        colorManager = new ColorManager(luckPerms);

        // Init death counter
        initDeathCounter();

        // Commands handlers init
        getCommand("kolor").setExecutor(new ColorCommand());
        getCommand("prometer").setExecutor(new PrometerCommand());

        // Event handlers init
        Bukkit.getPluginManager().registerEvents(new ChatListener(luckPerms), this);
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new AchievementListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);

        // Set sleep percentage
        Bukkit.getWorlds().get(0).setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, 50);

        // Broadcast manager
        broadCastManager = new BroadCastManager();

        getLogger().info("μPP discord plugin initialized!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            shutdownJda();
        } catch (InterruptedException e) {
            getLogger().severe("JDA Shutdown interrupted!");
        }
        if (linksStore != null) linksStore.close();
        if (sidebarStore != null) sidebarStore.close();
//        if (currenciesStore != null) currenciesStore.close();

        getLogger().info("RePP dsc plugin disabled!");
    }

    private void setupVault() throws NoVaultException {
        var rspEconomy = getServer().getServicesManager().getRegistration(Economy.class);

        if (rspEconomy == null) {
            throw new NoVaultException();
        }

        economy = rspEconomy.getProvider();
    }

    private void initJda() throws InterruptedException {
        boolean isDisabled = getConfig().getBoolean("discord.disabled");
        if (isDisabled) {
            getLogger().warning("Discord integration is disabled!");
            return;
        }

        String token = getConfig().getString("discord.token");

        if (token == null || token.isBlank()) {
            throw new NoTokenException();
        }

        jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(new DiscordMessageListener())
                .build();

        discordBridgeManager = new DiscordBridgeManager();

        jda.awaitReady();

        discordBridgeManager.sendToDsc("<:yt_ator_zaczelo_sie:1441854422278869022> **Serwer wystartował!**");
    }

    private void shutdownJda() throws InterruptedException {
        if (jda == null) return;
        discordBridgeManager.sendToDsc("<:murz_shaq_mimir:1441853988533436538> **Serwer wylądował!**");

        jda.shutdownNow();

        try {
            if (jda.awaitShutdown(5, TimeUnit.SECONDS)) {
                getLogger().warning("JDA did not shut within 5 seconds.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            getLogger().warning("JDA shutdown interrupted!");
        }
    }

    private void initDeathCounter() {
        Objective counter = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("prometer");

        if (counter == null) {
            counter = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(
                    "prometer",
                    Criteria.DEATH_COUNT,
                    Component.text("--<", NamedTextColor.GOLD)
                            .append(Component.text(" PROMETER ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text(">--", NamedTextColor.GOLD))
            );
            counter.setDisplaySlot(DisplaySlot.SIDEBAR);
            getLogger().info("Prometer initialized.");
        }
    }

    public static MicroppDsc getInstance() {
        return getPlugin(MicroppDsc.class);
    }
}
