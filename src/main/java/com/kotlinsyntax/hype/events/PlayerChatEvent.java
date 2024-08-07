package com.kotlinsyntax.hype.events;

import java.util.concurrent.TimeUnit;
import org.bukkit.event.EventHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import com.kotlinsyntax.hype.Hype;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class PlayerChat implements Listener
{
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final File playerfile = new File((Object)((Main)Main.getPlugin((Class)Main.class)).getDataFolder() + File.separator, "punishments.yml");
        final FileConfiguration playerData = (FileConfiguration)YamlConfiguration.loadConfiguration(playerfile);
        final String uuid = event.getPlayer().getUniqueId().toString();
        final long unixTime = System.currentTimeMillis() / 1000L;
        if (playerData.contains(uuid) && playerData.getBoolean(String.valueOf((Object)uuid) + ".mute.ismuted")) {
            if (playerData.getInt(String.valueOf((Object)uuid) + ".mute.length") <= unixTime) {
                try {
                    playerData.set(String.valueOf((Object)uuid) + ".mute.ismuted", (Object)false);
                    playerData.set(String.valueOf((Object)uuid) + ".mute.reason", (Object)"");
                    playerData.set(String.valueOf((Object)uuid) + ".mute.length", (Object)0);
                    playerData.set(String.valueOf((Object)uuid) + ".mute.id", (Object)"");
                    playerData.save(playerfile);
                }
                catch (final IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (playerData.getInt(String.valueOf((Object)uuid) + ".mute.length") <= 0) {
                return;
            }
            player.sendMessage("§c§l§m---------------------------------------------");
            player.sendMessage("§cYou are currently muted for " + playerData.getString(String.valueOf((Object)uuid) + ".mute.reason") + ".");
            player.sendMessage("§7Your mute will expire in §c" + calculateTime(playerData.getInt(String.valueOf((Object)uuid) + ".mute.length") - unixTime));
            player.sendMessage("");
            player.sendMessage("§7Find out more here: §e" + ((Main)Main.getPlugin((Class)Main.class)).getConfig().getString("mutedomain"));
            player.sendMessage("§7Mute ID: §f#" + playerData.getString(String.valueOf((Object)uuid) + ".mute.id"));
            player.sendMessage("§c§l§m---------------------------------------------");
            event.setCancelled(true);
        }
    }
    
    public static String calculateTime(final long seconds) {
        final int days = (int)TimeUnit.SECONDS.toDays(seconds);
        final long hours = TimeUnit.SECONDS.toHours(seconds) - days * 24;
        final long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        final long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        final String time = (" " + days + "d " + hours + "h " + minute + "m " + second + "s").toString().replace((CharSequence)" 0d", (CharSequence)"").replace((CharSequence)" 0h", (CharSequence)"").replace((CharSequence)" 0m", (CharSequence)"").replace((CharSequence)" 0s", (CharSequence)"").replaceFirst(" ", "");
        return time;
    }
}
