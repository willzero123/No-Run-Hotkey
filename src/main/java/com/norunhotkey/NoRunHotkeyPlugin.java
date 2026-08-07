package com.norunhotkey;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "No Run Hotkey",
        description = "Hold a hotkey to temporarily remove the 'Walk here' menu option.",
        tags = { "menu", "walk", "hide", "hotkey", "run", "misclick" }
)
public class NoRunHotkeyPlugin extends Plugin implements KeyListener
{
    @Inject
    private Client client;

    @Inject
    private NoRunHotkeyConfig config;

    @Inject
    private KeyManager keyManager;

    private boolean hotkeyPressed;

    @Provides
    NoRunHotkeyConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NoRunHotkeyConfig.class);
    }

    @Override
    protected void startUp()
    {
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(this);
        hotkeyPressed = false;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!hotkeyPressed)
        {
            return;
        }

        MenuEntry entry = event.getMenuEntry();
        if (entry.getType() == MenuAction.WALK)
        {
            client.getMenu().removeMenuEntry(entry);
        }
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        if (config.hotkey().matches(event))
        {
            hotkeyPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        if (config.hotkey().matches(event))
        {
            hotkeyPressed = false;
        }
    }

    @Override
    public void focusLost()
    {
        hotkeyPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent event)
    {
        // Not needed
    }
}
