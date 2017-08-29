package org.parabot.environment.servers;

import com.google.inject.Inject;
import org.objectweb.asm.Opcodes;
import org.parabot.api.io.build.BuildPath;
import org.parabot.core.Context;
import org.parabot.core.Core;
import org.parabot.core.asm.ASMClassLoader;
import org.parabot.core.asm.hooks.HookFile;
import org.parabot.core.asm.interfaces.Injectable;
import org.parabot.core.bdn.api.servers.ServerDownloader;
import org.parabot.core.desc.ServerDescription;
import org.parabot.core.parsers.hooks.HookParser;
import org.parabot.core.user.SharedUserAuthenticator;
import org.parabot.core.user.implementations.UserAuthenticatorAccess;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.Script;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.*;
import java.net.URL;

/**
 * Provides a server to the bot
 *
 * @author Everel
 */
public abstract class ServerProvider implements Opcodes {

    @Inject
    private ServerDownloader serverDownloader;

    private ServerDescription serverDescription;

    private SharedUserAuthenticator userAuthenticator;

    public ServerDescription getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(ServerDescription serverDescription) {
        this.serverDescription = serverDescription;
    }

    /**
     * Get the game/applet dimensions
     *
     * @return game/applet dimensions
     */
    public Dimension getGameDimensions() {
        return new Dimension(765, 503);
    }

    /**
     * Hooks to parse
     *
     * @return URL to hooks file
     *
     * @deprecated use getHookFile() now
     */
    @Deprecated
    public URL getHooks() {
        return null;
    }

    /**
     * Get hook file to parse
     *
     * @return hook file
     */
    public HookFile getHookFile() {
        return null;
    }

    /**
     * Jar to parse
     *
     * @return URL to client jar
     */
    public abstract URL getJar(SharedUserAuthenticator userAuthenticator);

    public abstract Applet fetchApplet();

    public String getAccessorsPackage() {
        return null;
    }

    public void injectHooks() {
        HookFile hookFile = fetchHookFile();

        if (hookFile == null) {
            return;
        }

        HookParser   parser      = hookFile.getParser();
        Injectable[] injectables = parser.getInjectables();
        if (injectables == null) {
            return;
        }
        for (Injectable inj : injectables) {
            inj.inject();
        }
        Core.getInjector().getInstance(Context.class).setHookParser(parser);
    }

    private HookFile fetchHookFile() {
        HookFile hookFile = getHookFile();
        if (hookFile != null) {
            return hookFile;
        }

        URL hookLocation = getHooks();
        if (hookLocation == null) {
            return null;
        }

        return new HookFile(hookLocation, HookFile.TYPE_XML);
    }

    /**
     * Add server items to the bot menu bar
     *
     * @param bar menu bar to add items on
     */
    public void addMenuItems(JMenuBar bar) {
    }

    public AppletStub getStub() {
        return null;
    }

    public void setClientInstance(Object client) {
        Core.getInjector().getInstance(Context.class).setClientInstance(client);
    }

    public void parseJar() {
        Core.getInjector().getInstance(ASMClassLoader.class).classPath.addJar(getJar(userAuthenticator));
    }

    public void initScript(Script script) {

    }

    public void init() {

    }

    public void initMouse() {
        final Context context = Core.getInjector().getInstance(Context.class);
        final Applet  applet  = context.getApplet();
        final Mouse   mouse   = Core.getInjector().getInstance(Mouse.class);

        mouse.setComponent(applet);

//        applet.addMouseListener(mouse);
//        applet.addMouseMotionListener(mouse);
//        context.setMouse(mouse);
    }

    public void initKeyboard() {
        final Context  context  = Core.getInjector().getInstance(Context.class);
        final Applet   applet   = context.getApplet();
        final Keyboard keyboard = Core.getInjector().getInstance(Keyboard.class);

        keyboard.setComponent(applet);

//        applet.addKeyListener(keyboard);
//        context.setKeyboard(keyboard);
    }

    public void unloadScript(Script script) {

    }

    public void setUserAuthenticator(SharedUserAuthenticator userAuthenticator) {
        this.userAuthenticator = userAuthenticator;
    }

    public SharedUserAuthenticator getUserAuthenticator() {
        return userAuthenticator;
    }
}
