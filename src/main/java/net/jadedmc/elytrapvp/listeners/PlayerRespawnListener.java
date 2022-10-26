package net.jadedmc.elytrapvp.listeners;

import net.jadedmc.elytrapvp.ElytraPvP;
import net.jadedmc.elytrapvp.player.CustomPlayer;
import net.jadedmc.elytrapvp.player.Status;
import net.jadedmc.elytrapvp.utils.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    private final ElytraPvP plugin;

    public PlayerRespawnListener(ElytraPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        CustomPlayer customPlayer = plugin.customPlayerManager().getPlayer(player);

        customPlayer.setStatus(Status.LOBBY);
        event.setRespawnLocation(plugin.arenaManager().getSelectedArena().getSpawn());

        ItemUtils.giveLobbyItems(player);
    }
}