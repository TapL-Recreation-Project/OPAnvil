package me.swipez.opanvil.command;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.gui.AnvilGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            if (args.length == 2){
                int level = Integer.parseInt(args[0]);
                int storedItems = Integer.parseInt(args[1]);
                Player player = (Player) sender;
                OPAnvil.putInItems.put(player.getUniqueId(), 0);
                OPAnvil.anvilLevel.put(player.getUniqueId(), level);
                OPAnvil.selectedItem.remove(player.getUniqueId());
                player.openInventory(AnvilGui.createInventory(player, level, storedItems));
            }
        }
        return true;
    }
}
