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

import com.wurmonline.client.renderer.PickData;
import com.wurmonline.client.renderer.backend.Queue;

public class BetterMapMarker {

	public final TextureButton textureButton;
	public final int worldX;
	public final int worldY;
	public final int type;

	public BetterMapMarker(String name, int worldX, int worldY, int type) {
		this.type = type;
		switch (type) {
		case 1:
			this.textureButton = new TextureButton("AnnotationIcon11", 16, 16, worldX, worldY, name, 0, 0);
			break;
		case 2:
			this.textureButton = new TextureButton("AnnotationIcon12", 16, 16, worldX, worldY, name, 0, 0);
			break;
		default:
			this.textureButton = new TextureButton("AnnotationIcon15", 16, 16, worldX, worldY, name, 0, 0);
		}
		this.textureButton.loadTexture();
		this.worldX = worldX;
		this.worldY = worldY;
	}

	protected void pick(PickData pickData, int xMouse, int yMouse) {
		this.textureButton.pick(pickData, xMouse, yMouse);
	}

	protected void render(Queue queue) {
		this.textureButton.render(queue, false);
	}

	protected void gameTick(int windowX, int windowY) {
		this.textureButton.gameTick(windowX, windowY);
	}
}
