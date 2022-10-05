package net.jadedmc.elytrapvp.game.kits.kits;

import net.jadedmc.elytrapvp.game.kits.Kit;
import net.jadedmc.elytrapvp.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TankKit extends Kit {

    public TankKit() {
        super("Tank", 4);

        setPrice(0);
        setIcon(Material.IRON_CHESTPLATE);
        setDescription("Increased health to help take the blows.");

        ItemStack bow = new ItemBuilder(Material.BOW)
                .setDisplayName("&aTank Bow")
                .setUnbreakable(true)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                .build();
        addItem(0, bow);

        addItem(17, new ItemBuilder(Material.ARROW, 64).setDisplayName("&aArrow").build());
        addItem(38, new ItemBuilder(Material.ELYTRA).setDisplayName("&aElytra").setUnbreakable(true).build());
        addItem(40, new ItemBuilder(Material.FIREWORK_ROCKET, 64).setDisplayName("&aFirework").build());

        addEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1));
    }
}