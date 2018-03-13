package io.freefair.umletsimple;

public class DrawElement {
	private String id;
	private int x;
	private int y;
	private int width;
	private int height;
	private String panelAttributes;
	private String additionalAttributes;

	public String getId() {
		return this.id;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public String getPanelAttributes() {
		return this.panelAttributes;
	}

	public String getAdditionalAttributes() {
		return this.additionalAttributes;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setPanelAttributes(String panelAttributes) {
		this.panelAttributes = panelAttributes;
	}

	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}
}
