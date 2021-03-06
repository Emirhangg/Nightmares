package net.darkhax.nightmares;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.lib.MCColor;
import net.darkhax.bookshelf.lib.WeightedSelector;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.nightmares.entity.EntityHag;
import net.darkhax.nightmares.entity.EntityPhantasmicSpider;
import net.darkhax.nightmares.entity.EntityShadow;
import net.darkhax.nightmares.entity.render.RenderHag;
import net.darkhax.nightmares.entity.render.RenderPhantasmicSpider;
import net.darkhax.nightmares.entity.render.RenderShadow;
import net.darkhax.nightmares.handler.NightmareTracker;
import net.darkhax.nightmares.nightmare.INightmare;
import net.darkhax.nightmares.nightmare.NightmareBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "nightmares", name = "Nightmares", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.2.464,);", certificateFingerprint = "@FINGERPRINT@")
public class Nightmares {

	public static final String MOD_ID = "nightmares";
    public static final RegistryHelper HELPER = new RegistryHelper(MOD_ID).enableAutoRegistration();
    public static final LoggingHelper LOG = new LoggingHelper(MOD_ID);

    /**
     * A weighted registry of all the nightmare situations.
     */
    public static final WeightedSelector<INightmare> NIGHTMARE_REGISTRY = new WeightedSelector<>();

    /**
     * Creature type used by all nightmare mobs.
     */
    public static final EnumCreatureAttribute NIGHTMARE = EnumHelper.addCreatureAttribute("NIGHTMARE");

    public static final ResourceLocation LOOT_ENTITIES_HAG = LootTableList.register(new ResourceLocation(MOD_ID, "entities/hag"));
    public static final ResourceLocation LOOT_ENTITIES_SPIDER = LootTableList.register(new ResourceLocation(MOD_ID, "entities/phantom_spider"));
    public static final ResourceLocation LOOT_ENTITIES_SHADOW = LootTableList.register(new ResourceLocation(MOD_ID, "entities/shadow"));
    
    @EventHandler
    public void onPreInit (FMLPreInitializationEvent event) {

    	new ConfigurationHandler(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);
        HELPER.registerMob(EntityHag.class, "hag", 0, MCColor.DYE_LIME.getRGB(), MCColor.DYE_YELLOW.getRGB());
        HELPER.registerMob(EntityShadow.class, "shadow", 1, MCColor.DYE_BLACK.getRGB(), MCColor.DYE_WHITE.getRGB());
        HELPER.registerMob(EntityPhantasmicSpider.class, "spider", 2, MCColor.DYE_MAGENTA.getRGB(), MCColor.DYE_PURPLE.getRGB());

        NIGHTMARE_REGISTRY.addEntry(new NightmareBase("nightmares:hag").addSpawn("nightmares:hag", 1, 1), 25);
        NIGHTMARE_REGISTRY.addEntry(new NightmareBase("nightmares:shadow").addSpawn("nightmares:shadow", 1, 3), 25);
        NIGHTMARE_REGISTRY.addEntry(new NightmareBase("nightmares:spiders").addSpawn("nightmares:spider", 3, 8), 25);
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void onClientPreInit (FMLPreInitializationEvent event) {

        RenderingRegistry.registerEntityRenderingHandler(EntityHag.class, new RenderHag.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityShadow.class, new RenderShadow.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityPhantasmicSpider.class, new RenderPhantasmicSpider.Factory());
    }

    @SubscribeEvent
    public void playerSleep (PlayerSleepInBedEvent event) {

        if (!event.getEntityPlayer().getEntityWorld().isRemote && MathsUtils.tryPercentage(ConfigurationHandler.nightmareChance)) {

            new NightmareTracker(event.getEntityPlayer());
        }
    }
}
