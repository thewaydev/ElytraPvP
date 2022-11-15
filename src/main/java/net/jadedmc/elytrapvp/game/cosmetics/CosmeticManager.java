package net.jadedmc.elytrapvp.game.cosmetics;

import net.jadedmc.elytrapvp.ElytraPvP;
import net.jadedmc.elytrapvp.game.cosmetics.arrowtrails.ArrowTrail;
import net.jadedmc.elytrapvp.game.cosmetics.arrowtrails.ArrowTrailCategory;
import net.jadedmc.elytrapvp.game.cosmetics.hats.Hat;
import net.jadedmc.elytrapvp.game.cosmetics.hats.HatCategory;
import net.jadedmc.elytrapvp.game.cosmetics.killmessages.KillMessage;
import net.jadedmc.elytrapvp.game.cosmetics.tags.Tag;
import net.jadedmc.elytrapvp.game.seasons.Season;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;

import java.util.*;

/**
 * Manages all cosmetics.
 */
public class CosmeticManager {
    private final ElytraPvP plugin;
    private final Map<String, Hat> hats = new LinkedHashMap<>();
    private final Map<String, KillMessage> killMessages = new LinkedHashMap<>();
    private final Map<String, Tag> tags = new LinkedHashMap<>();
    private final Map<String, ArrowTrail> arrowTrails = new LinkedHashMap<>();
    private final Map<Arrow, ArrowTrail> arrows = new HashMap<>();

    /**
     * Creates the manager.
     * @param plugin Instance of the plugin.
     */
    public CosmeticManager(ElytraPvP plugin) {
        this.plugin = plugin;

        // Load all cosmetics from the config.
        loadHats();
        loadKillMessages();
        loadTags();
        loadArrowTrails();
    }

    /**
     * Load all arrow trails from the config.
     */
    private void loadArrowTrails() {
        ConfigurationSection section = plugin.settingsManager().getArrowTrails().getConfigurationSection("ArrowTrails");

        // Makes sure the config file isn't null.
        if(section == null) {
            return;
        }

        // Loops through each trail in the config file.
        for(String id : section.getKeys(false)) {
            ConfigurationSection arrowTrail = section.getConfigurationSection(id);

            // Makes sure the trail isn't null.
            if(arrowTrail == null) {
                continue;
            }

            // Loads the arrow trail.
            arrowTrails.put(id, new ArrowTrail(plugin, id, arrowTrail));
        }

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            // Loop through all arrows that have an arrow trail.
            for(Arrow arrow : new ArrayList<>(getArrows().keySet())) {
                // If the arrow doesn't exist anymore, remove it.
                if(arrow == null || arrow.isDead()) {
                    getArrows().remove(arrow);
                    continue;
                }

                // Otherwise, spawn the error.
                getArrows().get(arrow).getCurrentStep().spawnParticles(arrow.getLocation());
            }

            // Incremenent the step of each arrow trail.
            getArrowTrails().forEach(ArrowTrail::nextStep);
        }, 1, 1);
    }

    /**
     * Load all hats from the config.
     */
    private void loadHats() {
        ConfigurationSection section = plugin.settingsManager().getHats().getConfigurationSection("Hats");

        if(section != null) {
            // Loop through each hat.
            for(String hatID : section.getKeys(false)) {
                ConfigurationSection hatConfig = section.getConfigurationSection(hatID);
                String name = hatConfig.getString("name");
                String texture = hatConfig.getString("texture");
                HatCategory category = HatCategory.valueOf(hatConfig.getString("category"));

                CosmeticType unlockType = CosmeticType.NORMAL;
                if(hatConfig.isSet("unlockType")) {
                    unlockType = CosmeticType.valueOf(hatConfig.getString("unlockType"));
                }

                int price = 50;
                if(hatConfig.isSet("price")) {
                    price = hatConfig.getInt("price");
                }

                Season season = Season.NONE;
                // Load the required season of the cosmetic if set.
                if(hatConfig.isSet("season")) {
                    season = Season.valueOf(hatConfig.getString("season"));
                }

                // Loads the hat.
                Hat hat = new Hat(plugin, hatID, name, unlockType, texture, category);
                hat.setPrice(price);
                hat.setSeason(season);

                // Caches the hat.
                hats.put(hatID, hat);
            }
        }
    }

    /**
     * Load all kill messages from the config.
     */
    private void loadKillMessages() {
        ConfigurationSection section = plugin.settingsManager().getKillMessages().getConfigurationSection("KillMessages");

        if(section != null) {
            // Loop through each kill message.
            for(String id : section.getKeys(false)) {
                ConfigurationSection config = section.getConfigurationSection(id);
                String message = config.getString("Message");

                CosmeticType unlockType = CosmeticType.NORMAL;
                if(config.isSet("unlockType")) {
                    unlockType = CosmeticType.valueOf(config.getString("unlockType"));
                }

                KillMessage killMessage = new KillMessage(plugin, id, message, unlockType);

                if(config.isSet("Price")) {
                    killMessage.setPrice(config.getInt("Price"));
                }

                // Load the required season of the cosmetic if set.
                if(config.isSet("season")) {
                    killMessage.setSeason(Season.valueOf(config.getString("season")));
                }

                killMessages.put(id, killMessage);
            }
        }
    }

    /**
     * Load all tags from the config.
     */
    private void loadTags() {
        ConfigurationSection section = plugin.settingsManager().getTags().getConfigurationSection("Tags");

        if(section != null) {
            // Loop through each kill message.
            for(String id : section.getKeys(false)) {
                ConfigurationSection config = section.getConfigurationSection(id);
                String tagText = config.getString("Tag");

                CosmeticType unlockType = CosmeticType.NORMAL;
                if(config.isSet("unlockType")) {
                    unlockType = CosmeticType.valueOf(config.getString("unlockType"));
                }

                Tag tag = new Tag(plugin, id, tagText, unlockType);

                if(config.isSet("Tag")) {
                    tag.setPrice(config.getInt("Price"));
                }

                // Load the required season of the cosmetic if set.
                if(config.isSet("season")) {
                    tag.setSeason(Season.valueOf(config.getString("season")));
                }

                tags.put(id, tag);
            }
        }
    }

    /**
     * Get a map of arrows and their arrow trails.
     * @return Arrows and their arrow trails.
     */
    public Map<Arrow, ArrowTrail> getArrows() {
        return arrows;
    }

    /**
     * Get an arrow trail based off its id.
     * @param id Id of the arrow trail.
     * @return Correcsponding arrow trail.
     */
    public ArrowTrail getArrowTrail(String id) {
        return arrowTrails.get(id);
    }

    /**
     * Get a collection of all arrow trails.
     * @return All arrow trails.
     */
    public Collection<ArrowTrail> getArrowTrails() {
        return arrowTrails.values();
    }

    /**
     * Get all hats from a specific category.
     * @param category Category to get hats of.
     * @return All hats in that category.
     */
    public Collection<ArrowTrail> getArrowTrails(ArrowTrailCategory category) {
        List<ArrowTrail> arrowTrailList = new ArrayList<>();

        for(ArrowTrail arrowTrail : getArrowTrails()) {
            if(arrowTrail.getCategory() == category) {
                arrowTrailList.add(arrowTrail);
            }
        }

        return arrowTrailList;
    }

    /**
     * Get all cosmetics for a specific season.
     * @param season Season to get cosmetics of.
     * @return List of cosmetics.
     */
    public List<Cosmetic> getCosmetics(Season season) {
        List<Cosmetic> cosmetics = new ArrayList<>();

        // Hats
        for(Hat hat : getHats()) {
            if(hat.getSeason() == season) {
                cosmetics.add(hat);
            }
        }

        // Kill Messages
        for(KillMessage killMessage : getKillMessages()) {
            if(killMessage.getSeason() == season) {
                cosmetics.add(killMessage);
            }
        }

        // Tags
        for(Tag tag : getTags()) {
            if(tag.getSeason() == season) {
                cosmetics.add(tag);
            }
        }

        // Arrow Trails
        for(ArrowTrail arrowTrail : getArrowTrails()) {
            if(arrowTrail.getSeason() == season) {
                cosmetics.add(arrowTrail);
            }
        }

        return cosmetics;
    }

    /**
     * Get a hat based off its id.
     * @param id Id of the hat.
     * @return Corresponding hat.
     */
    public Hat getHat(String id) {
        return hats.get(id);
    }

    /**
     * Get a collection of all hats.
     * @return All hats.
     */
    public Collection<Hat> getHats() {
        return hats.values();
    }

    /**
     * Get all hats from a specific category.
     * @param category Category to get hats of.
     * @return All hats in that category.
     */
    public Collection<Hat> getHats(HatCategory category) {
        List<Hat> hatList = new ArrayList<>();

        for(Hat hat : getHats()) {
            if(hat.getCategory() == category) {
                hatList.add(hat);
            }
        }

        return hatList;
    }

    /**
     * Get a kill message based off its id.
     * @param id id of the kill message.
     * @return Corresponding kill message.
     */
    public KillMessage getKillMessage(String id) {
        return killMessages.get(id);
    }

    /**
     * Get a collection of all kill messages.
     * @return All kill messages.
     */
    public Collection<KillMessage> getKillMessages() {
        return killMessages.values();
    }

    /**
     * Get a tag based off its id.
     * @param id Id of the tag.
     * @return Corresponding tag.
     */
    public Tag getTag(String id) {
        return tags.get(id);
    }

    /**
     * Get a collection of all tags.
     * @return All tags.
     */
    public Collection<Tag> getTags() {
        return tags.values();
    }
}