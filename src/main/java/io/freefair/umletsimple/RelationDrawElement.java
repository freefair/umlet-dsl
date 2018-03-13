package io.freefair.umletsimple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelationDrawElement {
	private RelationType relationType;
	private int x;
	private int y;
	private int width;
	private int height;
	private String panelAttributes;
	private List<Point> points = new ArrayList<>();

	public DrawElement toDrawElement() {
		DrawElement result = new DrawElement();

		result.setId("Relation");
		result.setPanelAttributes(panelAttributes);
		result.setAdditionalAttributes(points.stream().map(p -> String.valueOf(p.getX()) + ";" + String.valueOf(p.getY()))
				.collect(Collectors.joining(";")));
		result.setY(y);
		result.setX(x);
		result.setWidth(width);
		result.setHeight(height);

		return result;
	}

	public RelationType getRelationType() {
		return this.relationType;
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

	public List<Point> getPoints() {
		return this.points;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
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

	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
