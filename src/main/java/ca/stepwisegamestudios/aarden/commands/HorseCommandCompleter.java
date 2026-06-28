package ca.stepwisegamestudios.aarden.commands;

import ca.stepwisegamestudios.aarden.Horsetopia;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HorseCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>(Arrays.asList("spawn", "despawn", "neuter"));
            if (sender.hasPermission("betterhorses.reload")) {
                suggestions.add("reload");
            }
            if (Horsetopia.getInstance().isDebugModeEnabled()) {
                suggestions.add("info");
            }
            return suggestions;
        }
        return List.of();
    }
}
