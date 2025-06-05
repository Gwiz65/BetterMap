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

package com.wurmonline.client.renderer.gui;

import java.util.Locale;

import com.wurmonline.client.resources.textures.ResourceTextureLoader;

public class BetterMapWindow extends WWindow {

	public BetterMapView betterMapView;
	private WurmBorderPanel mainPanel;
	public String currentName;

	public BetterMapWindow() {
		super("BetterMapWindow");
		this.setTitle("");
		this.currentName = "";
		this.resizable = true;
		this.mainPanel = new WurmBorderPanel("BetterMapMainPanel");
		this.betterMapView = new BetterMapView();
		this.mainPanel.setComponent(this.betterMapView, 3);
		this.setComponent(this.mainPanel);
		this.setInitialSize(626, 657, false);
	}

	public void load() {
		String servername = hud.getWorld().getServerName();
		String mapname = servername.toLowerCase(Locale.ROOT).replace(" ", "_");
		if ((this.betterMapView.texture == null) || (this.currentName != servername)) {
			this.betterMapView.texture = ResourceTextureLoader
					.getNearestTextureNonScaling("bettermap_" + mapname + ".map");
			this.betterMapView.LoadXml(mapname);
		}
		if (this.betterMapView.texture.getUrl().toString().contains(mapname)) {
			this.currentName = servername;
			this.setTitle(servername);
		} else {
			this.currentName = "";
			this.setTitle("Bettermap server pack not downloaded.");
		}
	}

	protected void closePressed() {
		hud.hideComponent(this);
	}

	public void toggle() {
		hud.toggleComponent(this);
	}

	protected void componentResized() {
		this.width = Math.min(Math.max(this.width, 106), hud.getWidth());
		this.height = this.width + 31;
		if ((this.y + this.height) > hud.getHeight()) {
			this.height = hud.getHeight() - this.y;
			this.width = this.height - 31;
		}
		this.betterMapView.parentWidth = this.width;
		this.betterMapView.parentHeight = this.height;
		super.componentResized();
	}

}
