package io.github.cccm5.APMotd;

import net.countercraft.movecraft.config.Settings;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
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

public class MotdMain extends JavaPlugin implements Listener{
    private Configuration config;
    private CraftManager craftManager;
    private Logger logger;
    private List<City> cities = new ArrayList<>();
    private String motd;

    @Override
    public void onEnable() {

        config = getConfig();
        config.addDefault("Motd", "Hi mom");
        this.saveConfig();
        motd = ChatColor.translateAlternateColorCodes('&', config.getString("Motd"));

        logger = this.getLogger();

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
            cities.add(new City(name,new SiegeTime(siegeTimes.get(name)%100, siegeTimes.get(name)/ 100, siegeDays.get(name) )));
        }
        Collections.sort(cities);

    }

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent e){
        City nextSiege = getNextSiege();
        e.setMotd(motd + "\n" + getNextSiege());
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
