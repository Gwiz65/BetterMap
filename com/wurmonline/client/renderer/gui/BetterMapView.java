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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.wurmonline.client.WurmClientBase;
import com.wurmonline.client.renderer.PickData;
import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.resources.textures.ResourceTexture;
import com.wurmonline.shared.xml.XmlNode;
import com.wurmonline.shared.xml.XmlParser;

public class BetterMapView extends FlexComponent {

	public ResourceTexture texture;
	public int parentWidth;
	public int parentHeight;
	private int zoomFactor;
	private int map_x;
	private int map_y;
	private int lastMouseX;
	private int lastMouseY;
	public List<BetterMapMarker> markerList;

	public BetterMapView() {
		super("BetterMapView");
		this.texture = null;
		this.setInitialSize(hud.getHeight(), hud.getHeight(), false);
		this.zoomFactor = 20;
		this.map_x = 0;
		this.map_y = 0;
		this.markerList = new ArrayList<>();
	}

	public void LoadXml(String mapname) {
		this.markerList.clear();
		XmlNode rootNode = null;
		try {
			final InputStream input = WurmClientBase.getResourceManager()
					.getResourceAsStream("bettermap_" + mapname + ".xml");
			rootNode = XmlParser.parse(input);
		} catch (Exception e) {
			System.out.println("No mapping found for bettermap_" + mapname + ".xml.");
		}
		if (rootNode != null) {
			final List<XmlNode> markers = rootNode.getChildren();
			for (final XmlNode markernode : markers) {
				String name = "";
				int xpos = 0;
				int ypos = 0;
				int type = 0;
				final List<XmlNode> markerProperties = markernode.getChildren();
				for (final XmlNode property : markerProperties) {
					final String propertyName = property.getName();
					if (propertyName.equalsIgnoreCase("name")) {
						name = property.getText();
					} else if (propertyName.equalsIgnoreCase("xpos")) {
						xpos = Integer.parseInt(property.getText());
					} else if (propertyName.equalsIgnoreCase("ypos")) {
						ypos = Integer.parseInt(property.getText());
					} else if (propertyName.equalsIgnoreCase("type")) {
						type = Integer.parseInt(property.getText());
					}
				}
				this.markerList.add(new BetterMapMarker(name, xpos, ypos, type));
			}
		}
	}

	@Override
	public void pick(PickData pickData, int xMouse, int yMouse) {
		for (BetterMapMarker element : this.markerList) {
			element.pick(pickData, xMouse, yMouse);
		}
	}

	@Override
	protected void renderComponent(Queue queue, float alpha) {
		super.renderComponent(queue, alpha);
		if (this.texture != null) {
			Renderer.texturedQuadAlphaBlend(queue, this.texture, 1.0F, 1.0F, 1.0F, 1.0F, this.x, this.y,
					this.parentWidth - 6.0F, this.parentHeight - 37.0F, this.map_x * 0.001F, this.map_y * 0.001F,
					this.zoomFactor / 20.0F, this.zoomFactor / 20.0F);
			for (BetterMapMarker element : this.markerList) {
				element.textureButton.setLocalPositionX((int) Math.round(
						(((((double) element.worldX / hud.getWorld().getWorldSize()) * 1000.0) - this.map_x + 0.2)
								/ (this.zoomFactor * 50.0)) * (this.parentWidth - 6.0)));
				element.textureButton.setLocalPositionY((int) Math
						.round(((((double) element.worldY / hud.getWorld().getWorldSize() * 1000.0) - this.map_y - 0.7)
								/ (this.zoomFactor * 50.0)) * (this.parentHeight - 37.0)));
				element.gameTick(this.x, this.y);
				element.render(queue);
			}
		}
	}

	@Override
	protected void mouseWheeled(int xMouse, int yMouse, int wheelDelta) {
		int oldZoomfactor = this.zoomFactor;
		if (wheelDelta > 0) {
			this.zoomFactor += 1;
			this.zoomFactor = Math.min(this.zoomFactor, 20);
			if (this.zoomFactor != oldZoomfactor) {
				this.map_x -= 25;
				this.map_y -= 25;
			}
		}
		if (wheelDelta < 0) {
			this.zoomFactor -= 1;
			this.zoomFactor = Math.max(this.zoomFactor, 1);
			if (this.zoomFactor != oldZoomfactor) {
				this.map_x += 25;
				this.map_y += 25;
			}
		}
		this.map_x = Math.min(Math.max(this.map_x, 0), 1000 - (this.zoomFactor * 50));
		this.map_y = Math.min(Math.max(this.map_y, 0), 1000 - (this.zoomFactor * 50));
	}

	private float screenToMap(int num) {
		return (num * (this.zoomFactor * 50)) / (this.parentWidth - 6);
	}

	@Override
	protected void mouseDragged(int xMouse, int yMouse) {
		this.map_x += (int) Math.ceil(screenToMap(this.lastMouseX) - screenToMap(xMouse));
		this.map_y += (int) Math.ceil(screenToMap(this.lastMouseY) - screenToMap(yMouse));
		this.map_x = Math.min(Math.max(map_x, 0), 1000 - (this.zoomFactor * 50));
		this.map_y = Math.min(Math.max(map_y, 0), 1000 - (this.zoomFactor * 50));
		this.lastMouseX = xMouse;
		this.lastMouseY = yMouse;
	}

	@Override
	protected void leftPressed(int xMouse, int yMouse, int clickCount) {
		this.lastMouseX = xMouse;
		this.lastMouseY = yMouse;
	}
}