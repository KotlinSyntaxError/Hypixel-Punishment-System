package com.kotlinsyntax.hype.events;

import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import com.kotlinsyntax.hype.Hype;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class Unmute implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("hypixelpunishments.unmute")) {
            if (args.length >= 1) {
                final Player target = Bukkit.getPlayerExact(args[0]);
                final File playerfile = new File((Object)((Main)Main.getPlugin((Class)Main.class)).getDataFolder() + File.separator, "punishments.yml");
                final FileConfiguration playerData = (FileConfiguration)YamlConfiguration.loadConfiguration(playerfile);
                String uuid = null;
                if (target != null) {
                    uuid = target.getPlayer().getUniqueId().toString();
                }
                if (uuid == null) {
                    for (final String key : playerData.getKeys(false)) {
                        if (playerData.getString(String.valueOf((Object)key) + ".name").equalsIgnoreCase(args[0])) {
                            uuid = key;
                        }
                    }
                }
                if (uuid == null) {
                    sender.sendMessage("§cPlayer does not exist.");
                    return false;
                }
                if (playerData.contains(uuid)) {
                    if (playerData.getBoolean(String.valueOf((Object)uuid) + ".mute.ismuted")) {
                        try {
                            playerData.set(String.valueOf((Object)uuid) + ".mute.ismuted", (Object)false);
                            playerData.set(String.valueOf((Object)uuid) + ".mute.reason", (Object)"");
                            playerData.set(String.valueOf((Object)uuid) + ".mute.length", (Object)0);
                            playerData.set(String.valueOf((Object)uuid) + ".mute.id", (Object)"");
                            playerData.save(playerfile);
                            if (target != null) {
                                sender.sendMessage("§aUnmuted " + Bukkit.getPlayer(args[0]).getName());
                            }
                            else {
                                sender.sendMessage("§aUnmuted " + args[0]);
                            }
                        }
                        catch (final IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    else {
                        sender.sendMessage("§cPlayer is not muted!");
                    }
                }
            }
            else {
                sender.sendMessage("§cInvalid syntax. Correct: /unmute <name>");
            }
        }
        else {
            sender.sendMessage("§cYou do not have permission to execute this command!");
        }
        return false;
    }
}
