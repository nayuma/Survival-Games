package net.dmulloy2.survivalgames.commands;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.MessageManager;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Permission;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class CmdSetSpawn extends SurvivalGamesCommand
{
	public CmdSetSpawn(SurvivalGames plugin)
	{
		super(plugin);
		this.name = "setspawn";
		this.requiredArgs.add("id");
		this.description = "Sets a spawn for the arena you are located in";
		
		this.permission = Permission.ADMIN_SETSPAWN;
	}

	@Override
	public void perform()
	{
		loadNextSpawn();

		Location l = player.getLocation();
		int game = gameManager.getBlockGameId(l);

		if (game == -1)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notinarena", player);
			return;
		}
		
		int i = 0;
		if (args[0].equalsIgnoreCase("next"))
		{
			i = next.get(game);
			next.put(game, next.get(game) + 1);
		}
		else
		{
			try
			{
				i = Integer.parseInt(args[0]);
				if (i > next.get(game) + 1 || i < 1)
				{
					messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.between", player,
							"num-" + next.get(game));
					return;
				}
				if (i == next.get(game))
				{
					next.put(game, next.get(game) + 1);
				}
			}
			catch (Exception e)
			{
				messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.badinput", player);
				return;
			}
		}
		
		if (i == -1)
		{
			messageManager.sendFMessage(MessageManager.PrefixType.ERROR, "error.notinside", player);
			return;
		}
		
		plugin.getSettingsManager().setSpawn(game, i, l.toVector());
		messageManager.sendFMessage(MessageManager.PrefixType.INFO, "info.spawnset", player, "num-" + i, "arena-" + game);
		return;
	}
	
	private HashMap<Integer, Integer> next = new HashMap<Integer, Integer>();

	public void loadNextSpawn()
	{
		// Avoid Concurrency problems
		for (Game g : gameManager.getGames().toArray(new Game[0]))
		{ 
			next.put(g.getID(), plugin.getSettingsManager().getSpawnCount(g.getID()) + 1);
		}
	}
}