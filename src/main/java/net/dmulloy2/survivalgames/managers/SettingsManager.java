package net.dmulloy2.survivalgames.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class SettingsManager
{
	private FileConfiguration spawns;
	private FileConfiguration system;
	private FileConfiguration kits;
	private FileConfiguration messages;
	private FileConfiguration chest;

	private File f; // spawns
	private File f2; // system
	private File f3; // kits
	private File f4; // messages
	private File f5; // chest

	private @Getter final int KIT_VERSION = 1;
	private @Getter final int MESSAGE_VERSION = 1;
	private @Getter final int CHEST_VERSION = 0;
	private @Getter final int SPAWN_VERSION = 0;
	private @Getter final int SYSTEM_VERSION = 0;

	private final SurvivalGames plugin;

	public SettingsManager(SurvivalGames plugin)
	{
		this.plugin = plugin;

		if (plugin.getConfig().getInt("config-version") == plugin.getConfigVersion())
		{
			plugin.setConfigUpToDate(true);
		}
		else
		{
			File config = new File(plugin.getDataFolder(), "config.yml");
			config.delete();
		}

		plugin.saveDefaultConfig();
		plugin.reloadConfig();

		f = new File(plugin.getDataFolder(), "spawns.yml");
		f2 = new File(plugin.getDataFolder(), "system.yml");
		f3 = new File(plugin.getDataFolder(), "kits.yml");
		f4 = new File(plugin.getDataFolder(), "messages.yml");
		f5 = new File(plugin.getDataFolder(), "chest.yml");

		try
		{
			if (!f.exists())
				f.createNewFile();
			if (!f2.exists())
				f2.createNewFile();
			if (!f3.exists())
				loadFile("kits.yml");
			if (!f4.exists())
				loadFile("messages.yml");
			if (!f5.exists())
				loadFile("chest.yml");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		reloadSystem();
		saveSystemConfig();

		reloadSpawns();
		saveSpawns();

		reloadKits();
		// saveKits();

		reloadChest();

		reloadMessages();
		saveMessages();
	}

	public void set(String arg0, Object arg1)
	{
		plugin.getConfig().set(arg0, arg1);
	}

	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}

	public FileConfiguration getSystemConfig()
	{
		return system;
	}

	public FileConfiguration getSpawns()
	{
		return spawns;
	}

	public FileConfiguration getKits()
	{
		return kits;
	}

	public FileConfiguration getChest()
	{
		return chest;
	}

	public FileConfiguration getMessageConfig()
	{
		return messages;
	}

	public World getGameWorld(int game)
	{
		if (system.getString("sg-system.arenas." + game + ".world") == null)
		{
			return null;

		}

		return plugin.getServer().getWorld(system.getString("sg-system.arenas." + game + ".world"));
	}

	public void reloadConfig()
	{
		plugin.reloadConfig();
	}

	public boolean moveFile(File ff)
	{
		plugin.$("Moving outdated config file. " + f.getName());
		String name = ff.getName();
		File ff2 = new File(plugin.getDataFolder(), getNextName(name, 0));
		return ff.renameTo(ff2);
	}

	public String getNextName(String name, int n)
	{
		File ff = new File(plugin.getDataFolder(), name + ".old" + n);
		if (!ff.exists())
		{
			return ff.getName();
		}
		else
		{
			return getNextName(name, n + 1);
		}
	}

	public void reloadSpawns()
	{
		spawns = YamlConfiguration.loadConfiguration(f);
		if (spawns.getInt("version", 0) != SPAWN_VERSION)
		{
			moveFile(f);
			reloadSpawns();
		}
		spawns.set("version", SPAWN_VERSION);
		saveSpawns();
	}

	public void reloadSystem()
	{
		system = YamlConfiguration.loadConfiguration(f2);
		if (system.getInt("version", 0) != SYSTEM_VERSION)
		{
			moveFile(f2);
			reloadSystem();
		}
		system.set("version", SYSTEM_VERSION);
		saveSystemConfig();
	}

	public void reloadKits()
	{
		kits = YamlConfiguration.loadConfiguration(f3);
		if (kits.getInt("version", 0) != KIT_VERSION)
		{
			moveFile(f3);
			loadFile("kits.yml");
			reloadKits();
		}
	}

	public void reloadMessages()
	{
		messages = YamlConfiguration.loadConfiguration(f4);
		if (messages.getInt("version", 0) != MESSAGE_VERSION)
		{
			moveFile(f4);
			loadFile("messages.yml");
			reloadKits();
		}
		messages.set("version", MESSAGE_VERSION);
		saveMessages();
	}

	public void reloadChest()
	{
		chest = YamlConfiguration.loadConfiguration(f5);
		if (chest.getInt("version", 0) != CHEST_VERSION)
		{
			moveFile(f5);
			loadFile("chest.yml");
			reloadKits();
		}
	}

	public void saveSystemConfig()
	{
		try
		{
			system.save(f2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveSpawns()
	{
		try
		{
			spawns.save(f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveKits()
	{
		try
		{
			kits.save(f3);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveMessages()
	{
		try
		{
			messages.save(f4);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveChest()
	{
		try
		{
			chest.save(f5);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public int getSpawnCount(int gameid)
	{
		return spawns.getInt("spawns." + gameid + ".count");
	}

	// TODO: Implement per-arena settings aka flags
	public HashMap<String, Object> getGameFlags(int a)
	{
		HashMap<String, Object> flags = new HashMap<String, Object>();

		flags.put("AUTOSTART_PLAYERS", system.getInt("sg-system.arenas." + a + ".flags.autostart"));
		flags.put("AUTOSTART_VOTE", system.getInt("sg-system.arenas." + a + ".flags.vote"));
		flags.put("ENDGAME_ENABLED", system.getBoolean("sg-system.arenas." + a + ".flags.endgame-enabled"));
		flags.put("ENDGAME_PLAYERS", system.getInt("sg-system.arenas." + a + ".flags.endgame-players"));
		flags.put("ENDGAME_CHEST", system.getBoolean("sg-system.arenas." + a + ".flags.endgame-chest"));
		flags.put("ENDGAME_LIGHTNING", system.getBoolean("sg-system.arenas." + a + ".flags.endgame-lightning"));
		flags.put("DUEL_PLAYERS", system.getInt("sg-system.arenas." + a + ".flags.endgame-duel-players"));
		flags.put("DUEL_TIME", system.getInt("sg-system.arenas." + a + ".flags.endgame-duel-time"));
		flags.put("DUEL_ENABLED", system.getBoolean("sg-system.arenas." + a + ".flags.endgame-duel"));
		flags.put("ARENA_NAME", system.getString("sg-system.arenas." + a + ".flags.arena-name"));
		flags.put("ARENA_COST", system.getInt("sg-system.arenas." + a + ".flags.arena-cost"));
		flags.put("ARENA_REWARD", system.getInt("sg-system.arenas." + a + ".flags.arena-reward"));
		flags.put("ARENA_MAXTIME", system.getInt("sg-system.arenas." + a + ".flags.arena-maxtime"));
		flags.put("SPONSOR_ENABLED", system.getBoolean("sg-system.arenas." + a + ".flags.sponsor-enabled"));
		flags.put("SPONSOR_MODE", system.getInt("sg-system.arenas." + a + ".flags.sponsor-mode"));

		return flags;

	}

	public void saveGameFlags(HashMap<String, Object> flags, int a)
	{
		system.set("sg-system.arenas." + a + ".flags.autostart", flags.get("AUTOSTART_PLAYERS"));
		system.set("sg-system.arenas." + a + ".flags.vote", flags.get("AUTOSTART_VOTE"));
		system.set("sg-system.arenas." + a + ".flags.endgame-enabled", flags.get("ENDGAME_ENABLED"));
		system.set("sg-system.arenas." + a + ".flags.endgame-players", flags.get("ENDGAME_PLAYERS"));
		system.set("sg-system.arenas." + a + ".flags.endgame-chest", flags.get("ENDGAME_CHEST"));
		system.set("sg-system.arenas." + a + ".flags.endgame-lightning", flags.get("ENDGAME_LIGHTNING"));
		system.set("sg-system.arenas." + a + ".flags.endgame-duel-players", flags.get("DUEL_PLAYERS"));
		system.set("sg-system.arenas." + a + ".flags.endgame-duel-time", flags.get("DUEL_TIME"));
		system.set("sg-system.arenas." + a + ".flags.endgame-duel", flags.get("DUEL_ENABLED"));
		system.set("sg-system.arenas." + a + ".flags.arena-name", flags.get("ARENA_NAME"));
		system.set("sg-system.arenas." + a + ".flags.arena-cost", flags.get("ARENA_COST"));
		system.set("sg-system.arenas." + a + ".flags.arena-reward", flags.get("ARENA_REWARD"));
		system.set("sg-system.arenas." + a + ".flags.arena-maxtime", flags.get("ARENA_MAXTIME"));
		system.set("sg-system.arenas." + a + ".flags.sponsor-enabled", flags.get("SPONSOR_ENABLED"));
		system.set("sg-system.arenas." + a + ".flags.sponsor-mode", flags.get("SPONSOR_MODE"));

		saveSystemConfig();
	}

	public Location getLobbySpawn()
	{
		try
		{
			return new Location(plugin.getServer().getWorld(system.getString("sg-system.lobby.spawn.world")),
					system.getInt("sg-system.lobby.spawn.x"), system.getInt("sg-system.lobby.spawn.y"),
					system.getInt("sg-system.lobby.spawn.z"));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Location getSpawnPoint(int gameid, int spawnid)
	{
		return new Location(getGameWorld(gameid), spawns.getInt("spawns." + gameid + "." + spawnid + ".x"), spawns.getInt("spawns." + gameid
				+ "." + spawnid + ".y"), spawns.getInt("spawns." + gameid + "." + spawnid + ".z"));
	}

	public void setLobbySpawn(Location l)
	{
		system.set("sg-system.lobby.spawn.world", l.getWorld().getName());
		system.set("sg-system.lobby.spawn.x", l.getBlockX());
		system.set("sg-system.lobby.spawn.y", l.getBlockY());
		system.set("sg-system.lobby.spawn.z", l.getBlockZ());
	}

	public void setSpawn(int gameid, int spawnid, Vector v)
	{
		spawns.set("spawns." + gameid + "." + spawnid + ".x", v.getBlockX());
		spawns.set("spawns." + gameid + "." + spawnid + ".y", v.getBlockY());
		spawns.set("spawns." + gameid + "." + spawnid + ".z", v.getBlockZ());
		if (spawnid > spawns.getInt("spawns." + gameid + ".count"))
		{
			spawns.set("spawns." + gameid + ".count", spawnid);
		}
		try
		{
			spawns.save(f);
		}
		catch (IOException e)
		{
			//
		}

		plugin.getGameManager().getGame(gameid).addSpawn();

	}

	public String getSqlPrefix()
	{
		return getConfig().getString("sql.prefix");
	}

	public void loadFile(String file)
	{
		File t = new File(plugin.getDataFolder(), file);
		plugin.$("Writing new file: " + t.getAbsolutePath());

		try
		{
			t.createNewFile();
			FileWriter out = new FileWriter(t);
			plugin.debug(file);
			InputStream is = getClass().getResourceAsStream("/" + file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null)
			{
				out.write(line + "\n");
				plugin.debug(line);
			}
			out.flush();
			is.close();
			isr.close();
			br.close();
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}