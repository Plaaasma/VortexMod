package org.nerdorg.vortexmod.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Config {

    public static final String CATEGORY_TELEPORTATION = "teleportation";
    public static final String CATEGORY_ASSEMBLY = "assembly";
    public static final String CATEGORY_PHYSICS = "physics";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.DoubleValue TELEPORTATION_SPEED_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue MIN_TARGET_DISTANCE;

    public static ForgeConfigSpec.IntValue MAX_ASSEMBLY_BLOCKS;
    public static ForgeConfigSpec.BooleanValue ASSEMBLY_DIAGONALS;

    public static ForgeConfigSpec.DoubleValue DESCEND_SPEED;
    public static ForgeConfigSpec.DoubleValue ASCEND_SPEED;
    public static ForgeConfigSpec.DoubleValue TURN_ACCELERATION;
    public static ForgeConfigSpec.DoubleValue MAX_TURN_SPEED;
    public static ForgeConfigSpec.DoubleValue TURN_BANKING;
    public static ForgeConfigSpec.DoubleValue LINEAR_MAX_MASS;
    public static ForgeConfigSpec.DoubleValue LINEAR_MASS_SCALING;
    public static ForgeConfigSpec.DoubleValue LINEAR_BASE_MASS;
    public static ForgeConfigSpec.DoubleValue BASE_SPEED;
    public static ForgeConfigSpec.DoubleValue MAX_BASE_SPEED;
    public static ForgeConfigSpec.DoubleValue MAX_CASUAL_SPEED;
    public static ForgeConfigSpec.DoubleValue ROTOR_POWER;

    static {
        // TELEPORTATION
        COMMON_BUILDER.comment("Teleportation Settings").push(CATEGORY_TELEPORTATION);
        TELEPORTATION_SPEED_THRESHOLD = COMMON_BUILDER.comment("The speed at which you teleport to your target when the space circuit is engaged. Default is 39.3 (88 mph).")
                .defineInRange("teleportation_speed_threshold", 39.33, 0, Double.MAX_VALUE);

        MIN_TARGET_DISTANCE = COMMON_BUILDER.comment("The minimum distance you need to be from your target to teleport.")
                .defineInRange("min_target_distance", 100, 0, Double.MAX_VALUE);
        COMMON_BUILDER.pop();

        // ASSEMBLY
        COMMON_BUILDER.comment("Assembly Settings").push(CATEGORY_ASSEMBLY);
        MAX_ASSEMBLY_BLOCKS = COMMON_BUILDER.comment("Max amount of blocks that can be assembled by the flight computer.")
                .defineInRange("max_assembly_blocks", 16384, 0, Integer.MAX_VALUE);

        ASSEMBLY_DIAGONALS = COMMON_BUILDER.comment("Search for blocks diagonally while assembling.")
                .define("assembly_diagonals", true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Make sure config changes are duplicated on both Clients and the Server when running a dedicated Server,")
                .comment(" as the config isn't synced between Clients and Server.");

        // PHYSICS
        COMMON_BUILDER.comment("Physics Settings").push(CATEGORY_PHYSICS);
        DESCEND_SPEED = COMMON_BUILDER.comment("The speed at which you descend when holding the descend key.")
                .defineInRange("descend_speed", 16, 0, Double.MAX_VALUE);

        ASCEND_SPEED = COMMON_BUILDER.comment("The speed at which you ascend when holding the ascend key.")
                .defineInRange("ascend_speed", 8, 0, Double.MAX_VALUE);

        TURN_ACCELERATION = COMMON_BUILDER.comment("The acceleration of how quickly you turn.")
                .defineInRange("turn_acceleration", 50, 0, Double.MAX_VALUE);

        MAX_TURN_SPEED = COMMON_BUILDER.comment("The max speed you can turn at.")
                .defineInRange("max_turn_speed", 100, 0, Double.MAX_VALUE);

        TURN_BANKING = COMMON_BUILDER.comment("The amount the ship tilts when turning.")
                .defineInRange("turn_banking", 0.1, 0, Double.MAX_VALUE);

        LINEAR_MAX_MASS = COMMON_BUILDER.comment("Max smoothing value, will smooth out before reaching max value.")
                .defineInRange("linear_max_mass", 10000, 0, Double.MAX_VALUE);

        LINEAR_MASS_SCALING = COMMON_BUILDER.comment("How fast a ship will stop and accelerate.")
                .defineInRange("linear_mass_scaling", 0.0002, 0, Double.MAX_VALUE);

        LINEAR_BASE_MASS = COMMON_BUILDER.comment("Base mass for linear acceleration in Kg.")
                .defineInRange("linear_base_mass", 50, 0, Double.MAX_VALUE);

        BASE_SPEED = COMMON_BUILDER.comment("The speed a ship with no rotors can move at.")
                .defineInRange("base_speed", 0, 0, Double.MAX_VALUE);

        MAX_BASE_SPEED = COMMON_BUILDER.comment("The max speed that can be added to the base speed by rotors.")
                .defineInRange("max_base_speed", 60, 0, Double.MAX_VALUE);

        MAX_CASUAL_SPEED = COMMON_BUILDER.comment("The max speed of a ship.")
                .defineInRange("max_casual_speed", 100, 0, Double.MAX_VALUE);

        ROTOR_POWER = COMMON_BUILDER.comment("The amount of speed each rotor will add to the base speed when being powered at 64 RPM.")
                .defineInRange("rotor_power", 13.2, 0, Double.MAX_VALUE);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
