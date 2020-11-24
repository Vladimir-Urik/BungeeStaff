package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CoreCommand extends CommandBase {

    public CoreCommand(BungeeStaffPlugin plugin) {
        super(plugin, "bungeestaff", "", "bstaff");
        setPermissionKey("Core-Command");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            for (String s : BungeeStaffPlugin.getInstance().getMessages().getStringList("BungeeStaff-Module.No-Argument")) {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(s));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            for (String h : BungeeStaffPlugin.getInstance().getMessages().getStringList("BungeeStaff-Module.Help")) {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(h));
            }
            return;
        } else if (args[0].equalsIgnoreCase("reload")) {
            BungeeStaffPlugin.getInstance().registerConfig();
            sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("BungeeStaff-Module.Reload")));
        } else if (args[0].equalsIgnoreCase("group")) {
            if (args.length == 1) {
                for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                }
                return;
            }
            if (args[1].equalsIgnoreCase("help")) {
                for (String oof2 : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.Help")) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof2));
                }
                return;
            } else if (args[1].equalsIgnoreCase("search")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                ProxiedPlayer p2 = BungeeCord.getInstance().getPlayer(args[2]);

                if (p2 == null) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Not-Found")));
                    return;
                }
                String rank = Data.onlinestaff.get(p2.getName());
                String prefix = BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix");
                if (Data.prefix.containsKey(p2.getName())) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Search")
                            .replaceAll("%rank%", rank))
                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(prefix)).replaceAll("%player%", p2.getName()));
                } else {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Search-Null").replaceAll("%player%", p2.getName())));

                }
            } else if (args[1].equalsIgnoreCase("list")) {
                for (String groups : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.Group-List")) {
                    String gro = BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").toString();
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(groups.replaceAll("%groups%", gro).replaceAll("\\[", "")).replaceAll("]", ""));
                }
                return;
            } else if (args[1].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                String group = args[2];
                if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Found")
                            .replaceAll("%group%", group.toLowerCase())));
                } else {
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".prefix", "[unknown] ");
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".users", "");

                        List<String> glist = BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List");
                        glist.add(group.toLowerCase());
                        BungeeStaffPlugin.getInstance().getConfig().set("Rank-List", glist);

                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Created"))
                            .replaceAll("%group%", group.toLowerCase()));
                }
                return;
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                String group = args[2];
                if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".prefix", null);
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".users", null);
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase(), null);
                        List<String> glist = BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List");
                        glist.remove(group.toLowerCase());
                        BungeeStaffPlugin.getInstance().getConfig().set("Rank-List", glist);

                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Deleted"))
                                .replaceAll("%group%", group.toLowerCase()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Not-Found"))
                            .replaceAll("%group%", group));
                }
                return;
            } else if (args[1].equalsIgnoreCase("setprefix")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                String group = args[2].toLowerCase();

                if (args.length == 3) {
                    if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Prefix"))
                                .replaceAll("%group%", group).replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + group + ".prefix")))
                                .replaceAll("%player%", sender.getName()));
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Not-Found"))
                                .replaceAll("%group%", group));
                    }
                    return;
                }
                StringBuilder s = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    s.append(args[i]).append(" ");
                }
                try {
                    if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                        BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".prefix", s.toString());

                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Set-Prefix"))
                                .replaceAll("%group%", group.toLowerCase()).replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(s.toString())));
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Not-Found"))
                                .replaceAll("%group%", group));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            } else if (args[1].equalsIgnoreCase("add")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                ProxiedPlayer tar = ProxyServer.getInstance().getPlayer(args[2]);

                if (args.length == 3) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                String group = args[3].toLowerCase();

                if (tar == null) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Not-Found")));
                    return;
                }
                if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {

                    if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Ranks." + group.toLowerCase() + ".users").contains(tar.getUniqueId().toString())) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Exists"))
                                .replaceAll("%player%", tar.getName()));
                    } else {
                        try {
                            File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                            List<String> glist = BungeeStaffPlugin.getInstance().getConfig().getStringList("Ranks." + group.toLowerCase() + ".users");
                            glist.add(tar.getUniqueId().toString());
                            BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".users", glist);

                            BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                            Data.onlinestaff.put(tar.getName(), group);

                            String rank = Data.onlinestaff.get(tar.getName());
                            String prefix = BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix");

                            Data.prefix.put(tar.getName(), prefix);

                            sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Add"))
                                    .replaceAll("%player%", tar.getName()).replaceAll("%group%", group));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Not-Found"))
                            .replaceAll("%group%", group));
                }
                return;
            } else if (args[1].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                ProxiedPlayer tar = ProxyServer.getInstance().getPlayer(args[2]);

                if (args.length == 3) {
                    for (String oof : BungeeStaffPlugin.getInstance().getMessages().getStringList("Group-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(oof));
                    }
                    return;
                }
                String group = args[3].toLowerCase();

                if (tar == null) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Not-Found")));
                    return;
                }
                if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Rank-List").contains(group.toLowerCase())) {
                    if (BungeeStaffPlugin.getInstance().getConfig().getStringList("Ranks." + group.toLowerCase() + ".users").contains(tar.getUniqueId().toString())) {
                        try {
                            File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                            List<String> glist = BungeeStaffPlugin.getInstance().getConfig().getStringList("Ranks." + group.toLowerCase() + ".users");
                            glist.remove(tar.getUniqueId().toString());
                            BungeeStaffPlugin.getInstance().getConfig().set("Ranks." + group.toLowerCase() + ".users", glist);
                            Data.prefix.remove(tar.getName());

                            BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);

                            sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Remove"))
                                    .replaceAll("%player%", tar.getName()).replaceAll("%group%", group));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Player-Group-Not-Exist"))
                                .replaceAll("%player%", tar.getName()));
                    }
                } else {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Group-Module.Group-Not-Found"))
                            .replaceAll("%group%", group));
                }
                return;
            }

        } else {
            for (String s : BungeeStaffPlugin.getInstance().getMessages().getStringList("BungeeStaff-Module.No-Such-Argument")) {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(s));
            }
        }
    }
}
