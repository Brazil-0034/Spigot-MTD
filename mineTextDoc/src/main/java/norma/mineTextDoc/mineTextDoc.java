package norma.mineTextDoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class mineTextDoc extends JavaPlugin implements Listener {
	
	String stringSplitter = "|";
	String successPrefix = ChatColor.GREEN + "" + ChatColor.BOLD + "SUCCESS! ";
	String failurePrefix = ChatColor.DARK_RED + "" + ChatColor.BOLD + "FAILURE! ";
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player) sender;
		Location tl1 = null;
		Location tl2 = null;
		if (label.equalsIgnoreCase("t1"))
		{
			if (sender instanceof Player)
			{
				tl1 = player.getLocation();
				tl1.setX(Math.rint(tl1.getX()));
				tl1.setY(Math.rint(tl1.getY()));
				tl1.setZ(Math.rint(tl1.getZ()));
				player.sendMessage(ChatColor.AQUA + "Location " + ChatColor.BOLD + " T1 " + ChatColor.AQUA + " set to " + ChatColor.BLUE + tl1.toString());
				return true;
			}
			return false;
		}
		if (label.equalsIgnoreCase("t2"))
		{
			if (sender instanceof Player)
			{
				tl2 = player.getLocation();
				tl2.setX(Math.rint(tl2.getX()));
				tl2.setY(Math.rint(tl2.getY()));
				tl2.setZ(Math.rint(tl2.getZ()));
				player.sendMessage(tl2.toVector().toString());
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Location " + ChatColor.BOLD + " T2 " + ChatColor.LIGHT_PURPLE + " set to " + ChatColor.DARK_PURPLE + tl2.toString());
				return true;
			}
			return false;
		}
		if (label.equalsIgnoreCase("tsave"))
		{
			Vector playerPos = player.getLocation().toVector();
			File dataFolder = getDataFolder();
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}
			
			if (args[0].isEmpty())
			{
				player.sendMessage(failurePrefix + ChatColor.RED + "No file name specified, aborting.");
			}
			else
			{
				if (tl1.equals(null))
				{
					player.sendMessage(failurePrefix + ChatColor.RED + "First Position Not Set!");
				}
				
				if (tl2.equals(null))
				{
					player.sendMessage(failurePrefix + ChatColor.RED + "Second Position Not Set!");
				}
				
				String fileName = args[0] + ".txt";
				File mtd = new File(dataFolder, fileName);
				FileWriter fw = null;
				try {
					fw = new FileWriter(mtd, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				PrintWriter pw = new PrintWriter(fw);
				Vector max = Vector.getMaximum(tl1.toVector(), tl2.toVector());
				Vector min = Vector.getMinimum(tl1.toVector(), tl2.toVector());
				for (int z = min.getBlockZ(); z < max.getBlockZ(); z++)
				{
					for (int x = min.getBlockX(); x < max.getBlockX(); x++)
					{
						for (int y = min.getBlockY(); y < max.getBlockY(); y++)
						{
							Location currentLoc = new Location(player.getWorld(), x, y, z);
							Location locDiff = player.getLocation().subtract(currentLoc);
	
							if (currentLoc.getBlock().getType() != Material.AIR)
							{
									pw.println(currentLoc.getBlock().getType().toString()
										+ stringSplitter + Math.round(locDiff.getX())
										+ stringSplitter + Math.round(locDiff.getY() * -1)
										+ stringSplitter + Math.round(locDiff.getZ())
										+ stringSplitter + currentLoc.getBlock().getBlockData());
							}
						}
					}
				}
				pw.flush();
				pw.close();
				
				player.sendMessage(successPrefix + ChatColor.AQUA + "Saved build as " + fileName + ".txt");
			}
			
		}
		if (label.equalsIgnoreCase("tpaste"))
		{
			Location playerLoc = player.getLocation();
			JavaPlugin plugin = this;
			if (args[0].isEmpty())
			{
				player.sendMessage(ChatColor.RED + "No file name specified, aborting.");
			}
			else
			{
				File fileToRead = new File(getDataFolder(), args[0] + ".txt");
				if (fileToRead.exists())
				{
					pasteOp(playerLoc, plugin, fileToRead, player.getWorld());
				}
				else
				{
					player.sendMessage(ChatColor.RED + "There is no file with the name: " + fileToRead.getName());
				}
			}
		}
		return false;
	}
	
	public boolean pasteOp(Location playerLoc, JavaPlugin plugin, File fileName, World world)
	{
		if (fileName.exists()) {
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					BlockData data = null;
					Scanner scan = null;
					try {
						scan = new Scanner(fileName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					while (scan.hasNextLine())
					{
						String currentLine = scan.nextLine();
						if (currentLine != "")
						{
							String[] lineContents = currentLine.split("\\|");

							String currentBlockStr = lineContents[0];
							int xpos = Integer.parseInt(lineContents[1]);
							int ypos = Integer.parseInt(lineContents[2]);
							int zpos = Integer.parseInt(lineContents[3]);
							Location blockLoc = new Location(world, xpos, ypos, zpos).add(playerLoc);
							Material currentBlock = getCurrentMaterial(currentBlockStr);
							if (currentBlock != null)
							{
								data = returnBlockData(lineContents[4]);
								blockLoc.getBlock().setType(currentBlock);
								blockLoc.getBlock().setBlockData(data);
							}
						}
					}
					this.cancel();
				}
			}.runTaskTimer(plugin, 1, 1);
		}
		return true;
	}
	
	public BlockData returnBlockData(String str)
	{
		String stri = str.substring(25, str.length() - 1);
		BlockData d = null;
		try {
			d = Bukkit.createBlockData(stri);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	Material getCurrentMaterial(String mat)
	{
		//matchMaterial is not thread safe
		return Material.matchMaterial(mat);
	}
}
