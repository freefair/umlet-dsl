package io.freefair.umletsimple.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDefinition extends UMLetDefinition {
	private String custom;
	private List<ClassMemberDefinition> classMembers = new ArrayList<>();

	@Override
	public String toString() {
		return "class " + getName() + " {\n\t"
					+ classMembers.stream().map(ClassMemberDefinition::toString).collect(Collectors.joining("\n")) + "\n\t"
					+ "\t" + custom
					+ "\n}";
	}

	public String getCustom() {
		return this.custom;
	}

	public List<ClassMemberDefinition> getClassMembers() {
		return this.classMembers;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public void setClassMembers(List<ClassMemberDefinition> classMembers) {
		this.classMembers = classMembers;
	}
}
