/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
*/

package org.gwiz.wurmunlimited.mods;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.Versioned;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import org.gotti.wurmunlimited.modsupport.console.ConsoleListener;
import org.gotti.wurmunlimited.modsupport.console.ModConsole;

import com.wurmonline.client.renderer.gui.BetterMapWindow;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class BetterMap implements WurmClientMod, Initable, Versioned, ConsoleListener {

	private static final String version = "1.0";
	private BetterMapWindow betterMapWindow;

	@CallbackApi
	public void setMapWindow(BetterMapWindow betterMapWindow) {
		this.betterMapWindow = betterMapWindow;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public boolean handleInput(String string, Boolean aBoolean) {
		if (string == null)
			return false;
		String[] args = string.split("\\s+");
		if (!args[0].equals("bettermap"))
			return false;
		if (args.length > 1) {
			String command = args[1];
			switch (command) {
			case "toggle":
				this.betterMapWindow.toggle();
				return true;
			}
		}
		return true;
	}

	@Override
	public void init() {
		ModConsole.addConsoleListener(this);
		// add our jar to class loader
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		String clientJar = ((URLClassLoader) cl).getURLs()[0].toString();
		URL jarURL;
		try {
			jarURL = new URI(clientJar.substring(0, clientJar.lastIndexOf('/')) + "/mods/bettermap/bettermap.jar")
					.toURL();
			ReflectionUtil.callPrivateMethod(cl,
					ReflectionUtil.getMethod(cl.getClass(), "addURL", new Class[] { URL.class }), jarURL);
		} catch (MalformedURLException | URISyntaxException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		try {
			// hook into hud init and create our window
			ClassPool hookClassPool = HookManager.getInstance().getClassPool();
			CtClass ctHeadsUpDisplay = hookClassPool.getCtClass("com.wurmonline.client.renderer.gui.HeadsUpDisplay");
			HookManager.getInstance().addCallback(ctHeadsUpDisplay, "bettermap", this);
			ctHeadsUpDisplay.addField(
					new CtField(hookClassPool.getCtClass("com.wurmonline.client.renderer.gui.BetterMapWindow"),
							"betterMapWindow", ctHeadsUpDisplay));
			ctHeadsUpDisplay.getDeclaredMethod("init").insertAfter("{ this.betterMapWindow = new com.wurmonline."
					+ "client.renderer.gui.BetterMapWindow(); this.hudSettings.registerComponent(\"Better Map Window\", "
					+ "this.betterMapWindow); this.mainMenu.registerComponent(\"Better Map\", this.betterMapWindow); "
					+ "this.savePosManager.registerAndRefresh(this.betterMapWindow, \"bettermap\"); this.hideComponent("
					+ "this.betterMapWindow); this.mainMenu.setEnabled(this.betterMapWindow, false); this.bettermap."
					+ "setMapWindow(this.betterMapWindow); }");
			// hook into hud addComponent to load on window open
			ctHeadsUpDisplay.getDeclaredMethod("addComponent")
					.insertAfter("if (comp == this.betterMapWindow) this.betterMapWindow.load();");
		} catch (CannotCompileException | NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}