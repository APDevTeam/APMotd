package io.github.cccm5.APMotd;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.config.Settings;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MotdMain extends JavaPlugin implements Listener{
    private FileConfiguration config;
    private Logger logger;
    private List<City> cities = new ArrayList<>();
    private String motd;
    private boolean debug;

    @Override
    public void onEnable() {

        config = getConfig();
        config.addDefault("Motd", "&6Airship Pirates &2Survival &9 Airships, TnT, Factions!");
        config.options().copyDefaults(true);
        this.saveConfig();
        motd = ChatColor.translateAlternateColorCodes('&', config.getString("Motd"));
        debug = config.getBoolean("debug");

        logger = this.getLogger();
        getServer().getPluginManager().registerEvents(this, this);

        if(getServer().getPluginManager().getPlugin("Movecraft") == null || !getServer().getPluginManager().getPlugin("Movecraft").isEnabled()) {
            logger.log(Level.SEVERE, "Movecraft not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Map<String,Integer> siegeTimes = Settings.SiegeScheduleStart;
        Map<String, Integer> siegeDays = Settings.SiegeDayOfTheWeek;
        if(siegeTimes == null || siegeTimes.size() == 0 || siegeDays == null || siegeDays.size() == 0){
            logger.log(Level.SEVERE, "Siege not configured!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        for(String name : siegeTimes.keySet()){
            if(siegeDays.get(name)==null)
                for(int i = 1; i<=7;i++)
                    cities.add(new City(name,new SiegeTime(siegeTimes.get(name)%100, siegeTimes.get(name)/ 100, i)));
            else
                cities.add(new City(name,new SiegeTime(siegeTimes.get(name)%100, siegeTimes.get(name)/ 100, siegeDays.get(name) )));
        }
        Collections.sort(cities);

    }

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent e) {
        if(Movecraft.getInstance().assaultsRunning.size()!=0) {
            e.setMotd(motd + ChatColor.RESET + "\n" + "The assault of " + Movecraft.getInstance().assaultsRunning.iterator().next() + " is underway!");
            return;
        }
        City nextSiege = getNextSiege();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("MST"));
        int minutesToNextSiege = SiegeTime.siegeTimetoMinutes(nextSiege.getTime()) - calendar.get(Calendar.MINUTE) - calendar.get(Calendar.HOUR_OF_DAY) * 60 - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * 1440;
        if (minutesToNextSiege < 0)
            minutesToNextSiege = 10080 + minutesToNextSiege;
        //SiegeTime timeToNextSiege = nextSiege.getTime().getInterval(new SiegeTime(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DAY_OF_WEEK)));
        if (minutesToNextSiege > 1440 && !debug)
            e.setMotd(motd + ChatColor.RESET + "\n" + minutesToNextSiege / 1440 + " days until the siege of " + nextSiege.getName());
        else if (minutesToNextSiege > 60 && !debug)
            e.setMotd(motd + ChatColor.RESET + "\n" + minutesToNextSiege / 60 + " hours until the siege of " + nextSiege.getName());
        else
            e.setMotd(motd + ChatColor.RESET + "\n" + minutesToNextSiege + " minutes until the siege of " + nextSiege.getName());
        if (debug) {
            logger.info("Current date: Day " + calendar.get(Calendar.DAY_OF_WEEK) + ", hour " + calendar.get(Calendar.HOUR_OF_DAY) + ", minute " + calendar.get(Calendar.MINUTE));
            logger.info("Siege date: Day " + nextSiege.getTime().getDay() + ", hour " + nextSiege.getTime().getHour() + ", minute " + nextSiege.getTime().getMinute());
        }
    }

    private City getNextSiege(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("MST"));
        SiegeTime rightNow = new SiegeTime(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DAY_OF_WEEK));
        City scanCity = null;
        for(City city: cities){
            if(city.getTime().compareTo(rightNow) > 0) {
                scanCity = city;
                break;
            }
        }
        if(scanCity==null)
            return cities.get(0);
        return scanCity;
    }
}
