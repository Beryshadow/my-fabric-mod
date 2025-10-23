package com.example;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import jnr.ffi.LibraryLoader;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // public class HelloRust2 {
    //         public interface LibRust {
    //             int double_up(int x);
    //         }

    //         /** Calls the native Rust library. */
    //         public static int callDouble(int input) {
    //             // Adjust path to your built library (without the platform‑specific extension for JNR).
    //             String libPath = "myrustlib";
    //             LibRust lib = LibraryLoader.create(LibRust.class).load(libPath);
    //             return lib.double_up(input);
    //             // return input * 2;
    //         }
    // }

    public class HelloRust2 {
        public interface LibRust {
            int double_up(int x);
        }

        /**
         * Extracts the bundled native library from inside the jar
         * and loads it for JNR.
         */
        public static int callDouble(int input) {
            try {
                String libName = "myrustlib";
                String libFileName = "libmyrustlib.so"; // Linux-specific for now
                String resourcePath = "/native/" + libFileName;

                // Extract the .so file from inside the jar to a temp directory
                java.io.InputStream in = HelloRust2.class.getResourceAsStream(resourcePath);
                if (in == null) {
                    ExampleMod.LOGGER.error("❌ Could not find native library resource: {}", resourcePath);
                    return -1;
                }

                java.io.File tempFile = java.io.File.createTempFile(libName, ".so");
                tempFile.deleteOnExit();

                try (java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
                    in.transferTo(out);
                }

                ExampleMod.LOGGER.info("✅ Extracted native library to: {}", tempFile.getAbsolutePath());

                // Load using JNR
                LibRust lib = jnr.ffi.LibraryLoader.create(LibRust.class).load(tempFile.getAbsolutePath());
                int result = lib.double_up(input);

                ExampleMod.LOGGER.info("🦀 Rust function returned: {}", result);
                return result;

            } catch (Exception e) {
                ExampleMod.LOGGER.error("⚠️ Failed to call Rust function", e);
                return -1;
            }
        }
    }




    @Override
    public void onInitialize() {
        LOGGER.info("Hello from {}! Mod is initializing. ", MOD_ID);

        // int result = HelloRust2.callDouble(2);
        // LOGGER.info("Hello 2 from {}! Mod is initializing. {} ", MOD_ID, result);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("test_command")
                    .then(
                        CommandManager.argument("value", IntegerArgumentType.integer())
                            .executes(ctx -> commandWithArg(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value")))
                    )
            );
        });

        LOGGER.debug("Command /test_command registered.");
    }

    private static int commandWithArg(ServerCommandSource source, int value) {
        int result = HelloRust2.callDouble(value);
        source.sendFeedback(() -> Text.of(String.format("%d x 2 = %d", value, result)), false);
        LOGGER.debug("Rust function called: {} * 2 = {}", value, result);
        return SINGLE_SUCCESS;
    }
    
}
