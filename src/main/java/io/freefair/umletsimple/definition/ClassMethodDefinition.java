package io.freefair.umletsimple.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassMethodDefinition extends ClassMemberDefinition {
	private List<MethodParameterDefinition> parameters = new ArrayList<>();

	@Override
	public String toString() {
		return getType() + " " + getName() + "(" + getParameters().stream().map(MethodParameterDefinition::toString).collect(Collectors.joining(",")) + ")";
	}

	public List<MethodParameterDefinition> getParameters() {
		return this.parameters;
	}

	public void setParameters(List<MethodParameterDefinition> parameters) {
		this.parameters = parameters;
	}
}
