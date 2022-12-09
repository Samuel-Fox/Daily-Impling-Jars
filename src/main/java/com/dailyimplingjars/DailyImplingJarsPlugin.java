package com.dailyimplingjars;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Daily Impling Jars"
)
public class DailyImplingJarsPlugin extends Plugin
{
	private static final int ONE_DAY = 86400000;
	private static final int DAILY_JARS_STATE = 11769;
	private static final String IMPLING_JARS_MESSAGE = "You have impling jars waiting to be collected from Elnock Inquisitor.";
	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	private long lastReset;
	private boolean loggingIn;

	@Override
	public void startUp()
	{
		loggingIn = true;
	}

	@Override
	public void shutDown()
	{
		lastReset = 0L;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGING_IN)
		{
			loggingIn = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		long currentTime = System.currentTimeMillis();
		boolean dailyReset = !loggingIn && (currentTime - lastReset > ONE_DAY);
		if((dailyReset || loggingIn) && client.getVarcIntValue(VarClientInt.MEMBERSHIP_STATUS) == 1)
		{
			lastReset = (long) Math.floor(currentTime / ONE_DAY) * ONE_DAY;
			loggingIn = false;
			checkJars(dailyReset);
		}
	}

	private void checkJars(boolean dailyReset)
	{
		if(client.getVarbitValue(DAILY_JARS_STATE) < 10 || dailyReset)
		{
			sendChatMessage(IMPLING_JARS_MESSAGE);
		}
	}

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}
}
