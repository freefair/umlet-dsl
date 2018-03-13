package io.freefair.umletsimple;

public class Point {
	private double x;
	private double y;

	@java.beans.ConstructorProperties({"x", "y"})
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Point)) return false;
		final Point other = (Point) o;
		if (!other.canEqual((Object) this)) return false;
		if (Double.compare(this.getX(), other.getX()) != 0) return false;
		if (Double.compare(this.getY(), other.getY()) != 0) return false;
		return true;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final long $x = Double.doubleToLongBits(this.getX());
		result = result * PRIME + (int) ($x >>> 32 ^ $x);
		final long $y = Double.doubleToLongBits(this.getY());
		result = result * PRIME + (int) ($y >>> 32 ^ $y);
		return result;
	}

	protected boolean canEqual(Object other) {
		return other instanceof Point;
	}

	public String toString() {
		return "io.freefair.umletsimple.Point(x=" + this.getX() + ", y=" + this.getY() + ")";
	}
}
