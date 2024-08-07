package com.kotlinsyntax.hype.events;

import java.util.regex.Matcher;
import java.time.temporal.TemporalAmount;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.io.IOException;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import com.kotlinsyntax.hype.Hype;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.regex.Pattern;
import org.bukkit.command.CommandExecutor;

public class Mute implements CommandExecutor
{
    private static final Pattern periodPattern;
    
    static {
        periodPattern = Pattern.compile("([0-9]+)([hdwmy])");
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("hypixelpunishments.mute")) {
            if (args.length >= 3) {
                String reason = "";
                for (int i = 2; i < args.length; ++i) {
                    reason = String.valueOf((Object)reason) + args[i] + " ";
                }
                reason = reason.substring(0, reason.length() - 1);
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
                final long unixTime = System.currentTimeMillis() / 1000L;
                final long muteTime = parsePeriod(args[1]) / 1000L - 1L;
                if (muteTime < 59L) {
                    sender.sendMessage("§cYou can not mute someone for less than 1 minute.");
                    return false;
                }
                if (playerData.contains(uuid)) {
                    if (!playerData.getBoolean(String.valueOf((Object)uuid) + ".mute.ismuted")) {
                        try {
                            playerData.set(String.valueOf((Object)uuid) + ".mute.ismuted", (Object)true);
                            playerData.set(String.valueOf((Object)uuid) + ".mute.reason", (Object)reason);
                            playerData.set(String.valueOf((Object)uuid) + ".mute.length", (Object)(unixTime + muteTime));
                            final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                            final String pwd = RandomStringUtils.random(8, characters);
                            playerData.set(String.valueOf((Object)uuid) + ".mute.id", (Object)pwd);
                            playerData.save(playerfile);
                            if (target != null) {
                                sender.sendMessage("§aMuted " + Bukkit.getPlayer(args[0]).getName() + " for " + args[1] + " for " + reason);
                                target.sendMessage("§c§l§m---------------------------------------------");
                                target.sendMessage("§cYou are currently muted for " + reason + ".");
                                target.sendMessage("§7Your mute will expire in §c" + calculateTime(playerData.getInt(String.valueOf((Object)uuid) + ".mute.length") - unixTime));
                                target.sendMessage("");
                                target.sendMessage("§7Find out more here: §e" + ((Main)Main.getPlugin((Class)Main.class)).getConfig().getString("mutedomain"));
                                target.sendMessage("§7Mute ID: §f#" + playerData.getString(String.valueOf((Object)uuid) + ".mute.id"));
                                target.sendMessage("§c§l§m---------------------------------------------");
                            }
                            else {
                                sender.sendMessage("§aMuted " + args[0] + " for " + args[1] + " for " + reason);
                            }
                        }
                        catch (final IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    else {
                        sender.sendMessage("§cPlayer is already muted!");
                    }
                }
            }
            else {
                sender.sendMessage("§cInvalid syntax. Correct: /mute <name> <length> <reason>");
            }
        }
        else {
            sender.sendMessage("§cYou do not have permission to execute this command!");
        }
        return false;
    }
    
    public static String calculateTime(final long seconds) {
        final int days = (int)TimeUnit.SECONDS.toDays(seconds);
        final long hours = TimeUnit.SECONDS.toHours(seconds) - days * 24;
        final long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        final long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        final String time = (" " + days + "d " + hours + "h " + minute + "m " + second + "s").toString().replace((CharSequence)" 0d", (CharSequence)"").replace((CharSequence)" 0h", (CharSequence)"").replace((CharSequence)" 0m", (CharSequence)"").replace((CharSequence)" 0s", (CharSequence)"").replaceFirst(" ", "");
        return time;
    }
    
    public static Long parsePeriod(String period) {
        if (period == null) {
            return null;
        }
        period = period.toLowerCase(Locale.ENGLISH);
        final Matcher matcher = Mute.periodPattern.matcher((CharSequence)period);
        Instant instant = Instant.EPOCH;
        while (matcher.find()) {
            final int num = Integer.parseInt(matcher.group(1));
            final String typ = matcher.group(2);
            final String s;
            switch ((s = typ).hashCode()) {
                case 100: {
                    if (!s.equals((Object)"d")) {
                        continue;
                    }
                    instant = instant.plus((TemporalAmount)Duration.ofDays((long)num));
                    continue;
                }
                case 104: {
                    if (!s.equals((Object)"h")) {
                        continue;
                    }
                    instant = instant.plus((TemporalAmount)Duration.ofHours((long)num));
                    continue;
                }
                case 109: {
                    if (!s.equals((Object)"m")) {
                        continue;
                    }
                    instant = instant.plus((TemporalAmount)Duration.ofMinutes((long)num));
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        return instant.toEpochMilli();
    }
}
