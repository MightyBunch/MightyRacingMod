package com.mightyracing;

import com.mightyracing.config.Config;
import com.mightyracing.events.MightyDataCopy;
import com.mightyracing.events.MightyTick;
import com.mightyracing.events.PlayerDisconnect;
import com.mightyracing.events.WorldLoad;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MightyRacingMod implements ModInitializer {
	public static final String MOD_ID = "mighty-racing";
	public static final String DATA_ID = "MightyRacingData";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("MightyRacingMod initializing...");


		Config.reg();

		CommandRegistrationCallback.EVENT.register(MightyRacingCommand::register);

		ServerPlayConnectionEvents.DISCONNECT.register(new PlayerDisconnect());
		ServerLifecycleEvents.SERVER_STARTED.register(new WorldLoad());
		ServerTickEvents.END_SERVER_TICK.register(new MightyTick());
		ServerPlayerEvents.COPY_FROM.register(new MightyDataCopy());

		LOGGER.info("MightyRacingMod initialized!");
	}
}