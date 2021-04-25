package net.nighthawkempires.permissions.scoreboard;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.scoreboard.NEScoreboard;
import net.nighthawkempires.core.settings.ConfigModel;
import net.nighthawkempires.core.util.StringUtil;
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
        top.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.BLUE);
        top.setPrefix("");
        top.setSuffix("");
        Team middle = scoreboard.registerNewTeam("middle");
        middle.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.GREEN);
        middle.setPrefix("");
        middle.setSuffix("");
        Team bottom = scoreboard.registerNewTeam("bottom");
        bottom.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.GOLD);
        bottom.setPrefix("");
        bottom.setSuffix("");

        objective.getScore(ChatColor.GRAY + " Main Group" + ChatColor.GRAY + ": ").setScore(9);
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.BLUE).setScore(8);
        objective.getScore(ChatColor.DARK_PURPLE + " ").setScore(7);
        objective.getScore(ChatColor.GRAY + "Groups" + ChatColor.GRAY + ": ").setScore(6);
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.GREEN).setScore(5);
        objective.getScore(ChatColor.YELLOW + "  ").setScore(4);
        objective.getScore(ChatColor.GRAY + " Status" + ChatColor.GRAY + ": ").setScore(3);
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " ➛  " + ChatColor.GOLD).setScore(2);
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "━━━━━━━━━━━━━━━━━━━━━━")
                .setScore(1);

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.getPlugin(), () -> {
            top.setSuffix(userModel.getHighestRankingGroup().getColoredName());
            middle.setSuffix(ChatColor.GOLD + "" + userModel.getGroups().size());
            if (userModel.getStatus() == null)
                bottom.setSuffix(ChatColor.GRAY + "None");
            else
                bottom.setSuffix(userModel.getStatus().getColoredName());
        }, 0 , 5);
        return scoreboard;
    }

    private ConfigModel getConfig() {
        return CorePlugin.getConfigg();
    }
}
