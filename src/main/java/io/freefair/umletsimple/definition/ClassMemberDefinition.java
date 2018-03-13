package io.freefair.umletsimple.definition;

public class ClassMemberDefinition extends UMLetDefinition {
	private String type;

	@Override
	public String toString() {
		return type + " " + getName();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
