package com.example.sleeplog;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepLogMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sleeplog");

    @Override
    public void onInitialize() {
        LOGGER.info("SleepLog loaded");
    }
}
