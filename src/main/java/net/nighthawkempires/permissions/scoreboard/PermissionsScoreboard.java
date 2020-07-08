package net.nighthawkempires.permissions.scoreboard;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.scoreboard.NEScoreboard;
import net.nighthawkempires.core.settings.ConfigModel;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PermissionsScoreboard extends NEScoreboard {

    private int taskId;

    @Override
    public int getPriority() {
        return 2;
    }

    public String getName() {
        return "permissions";
    }

    public int getTaskId() {
        return this.taskId;
    }

    public Scoreboard getFor(Player player) {
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(CorePlugin.getMessages().getMessage(Messages.SCOREBOARD_HEADER).replaceAll("%SERVER%",
                CorePlugin.getMessages().getServerTag(getConfig().getServerType())));
        Team top = scoreboard.registerNewTeam("top");
        top.addEntry(ChatColor.GRAY + " ➛  " + ChatColor.BLUE + "" + ChatColor.BOLD);
        top.setPrefix("");
        top.setSuffix("");
        Team middle = scoreboard.registerNewTeam("middle");
        middle.addEntry(ChatColor.GRAY + " ➛  " + ChatColor.GREEN + "" + ChatColor.BOLD);
        middle.setPrefix("");
        middle.setSuffix("");
        Team bottom = scoreboard.registerNewTeam("bottom");
        bottom.addEntry(ChatColor.GRAY + " ➛  " + ChatColor.GOLD + "" + ChatColor.BOLD);
        bottom.setPrefix("");
        bottom.setSuffix("");

        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "--------------")
                .setScore(10);
        objective.getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " Main Group" + ChatColor.GRAY + ": ").setScore(9);
        objective.getScore(ChatColor.GRAY + " ➛  " + ChatColor.BLUE + "" + ChatColor.BOLD).setScore(8);
        //op.setSuffix(player.getName());
        top.setSuffix(userModel.getHighestRankingGroup().getBoldColoredName());
        objective.getScore(ChatColor.DARK_PURPLE + " ").setScore(7);
        objective.getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " Groups" + ChatColor.GRAY + ": ")
                .setScore(6);
        objective.getScore(ChatColor.GRAY + " ➛  " + ChatColor.GREEN + "" + ChatColor.BOLD).setScore(5);
        middle.setSuffix(userModel.getGroups().size() + "");
        objective.getScore(ChatColor.YELLOW + "  ").setScore(4);
        objective.getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " Status" + ChatColor.GRAY + ": ").setScore(3);
        objective.getScore(ChatColor.GRAY + " ➛  " + ChatColor.GOLD + "" + ChatColor.BOLD).setScore(2);
        if (userModel.getStatus() == null)
            bottom.setSuffix("None");
        else
            bottom.setSuffix(userModel.getStatus().getBoldColoredName());
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "--------------")
                .setScore(1);

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.getPlugin(), () -> {
            top.setSuffix(userModel.getHighestRankingGroup().getBoldColoredName());
            middle.setSuffix(userModel.getGroups().size() + "");
            if (userModel.getStatus() == null)
                bottom.setSuffix("None");
            else
                bottom.setSuffix(userModel.getStatus().getBoldColoredName());
            }, 0 , 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.getPlugin(), () -> {
            Bukkit.getScheduler().cancelTask(getTaskId());
        }, 295);
        return scoreboard;
    }

    private ConfigModel getConfig() {
        return CorePlugin.getConfigg();
    }
}
