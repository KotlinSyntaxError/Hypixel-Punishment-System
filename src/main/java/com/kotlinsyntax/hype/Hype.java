package com.kotlinsyntax.hype;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;

 class FunCommands extends JavaPlugin {

	public void onEnable() {

		getLogger().info("§eLoading configuration...");

		this.loadConfig();

		getLogger().info("§aLoaded configuration!");

		getLogger().info("§eRegistering commands...");

		initializeCommands();

		getLogger().info("§aRegistered commands!");

		getLogger().info("§eInitializing Gamerules...");

		initializeGameRules();

		getLogger().info("§aInitizlized Gamerules!");

		getLogger().info("§e------------------------------------");
        getLogger().info("§bHypixel Punishments plugin has been enabled!");
		getLogger().info("§bAuthor: KotlinSyntaxError");
        getLogger().info("§e------------------------------------");
  }
   
@Override
public void onDisable() {
	getLogger().info("§e-------------------------------------");
    getLogger().info("§bFun plugin has been disabled!");
  getLogger().info("§bAuthor: FuryloxxTheDev");
    getLogger().info("§e-------------------------------------");
	}}
