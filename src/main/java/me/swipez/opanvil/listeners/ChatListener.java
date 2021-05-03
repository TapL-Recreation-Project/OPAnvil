package me.swipez.opanvil.listeners;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.gui.AnvilGui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChats(PlayerChatEvent event){
        Player player = event.getPlayer();
        if (OPAnvil.currentlyNaming.contains(player.getUniqueId())){
            OPAnvil.selectedName.put(player.getUniqueId(), event.getMessage());
            OPAnvil.currentlyNaming.remove(player.getUniqueId());
            player.openInventory((AnvilGui.createInventory(player, OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId()))));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            event.setCancelled(true);
        }
    }
}
