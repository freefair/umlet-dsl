package io.freefair.umletsimple.definition;

public class RelationDefinition extends UMLetDefinition {
	private UMLetDefinition source;
	private String sourceMultiplicity;
	private String type;
	private UMLetDefinition destination;
	private String destinationMultiplicity;

	@Override
	public String toString() {
		return source.getName() + "[" + sourceMultiplicity + "] " + type + " " + destination.getName() + "[" + destinationMultiplicity + "]";
	}

	public UMLetDefinition getSource() {
		return this.source;
	}

	public String getSourceMultiplicity() {
		return this.sourceMultiplicity;
	}

	public String getType() {
		return this.type;
	}

	public UMLetDefinition getDestination() {
		return this.destination;
	}

	public String getDestinationMultiplicity() {
		return this.destinationMultiplicity;
	}

	public void setSource(UMLetDefinition source) {
		this.source = source;
	}

	public void setSourceMultiplicity(String sourceMultiplicity) {
		this.sourceMultiplicity = sourceMultiplicity;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDestination(UMLetDefinition destination) {
		this.destination = destination;
	}

	public void setDestinationMultiplicity(String destinationMultiplicity) {
		this.destinationMultiplicity = destinationMultiplicity;
	}
}
