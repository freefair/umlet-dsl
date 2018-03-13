package io.freefair.umletsimple;

import io.freefair.umletsimple.definition.ClassDefinition;
import io.freefair.umletsimple.definition.ClassMethodDefinition;
import io.freefair.umletsimple.definition.PackageDefinition;
import io.freefair.umletsimple.definition.RelationDefinition;
import io.freefair.umletsimple.definition.UMLetDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UMLetXmlBuilder {

	private static final int BOX_DEFAULT_WIDTH = 100;
	private static final int BOX_DEFAULT_HEIGHT = 250;
	private static final int BOX_SPACING = 100;
	private static final int CLASSES_PER_LINE = 4;

	private final List<UMLetDefinition> definitions;
	private final Map<UMLetDefinition, DrawElement> drawElements = new HashMap<>();
	private final Map<RelationDefinition, RelationDrawElement> relationElements = new HashMap<>();
	private final Document document;
	private final Element rootElement;


	public UMLetXmlBuilder(List<UMLetDefinition> definitions) {
		this.definitions = definitions;
		try {
			this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			rootElement = this.document.createElement("diagram");
			rootElement.setAttribute("program", "umlet");
			rootElement.setAttribute("version", "14.2");
			this.document.appendChild(rootElement);
			Element zoomLevel = this.document.createElement("zoom_level");
			zoomLevel.setTextContent("10");
			rootElement.appendChild(zoomLevel);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String build() {
		for(UMLetDefinition definition : definitions) {
			if(definition instanceof ClassDefinition) {
				createUmlClass((ClassDefinition)definition);
			} else if (definition instanceof PackageDefinition) {
				createPackage((PackageDefinition)definition);
			} else if (definition instanceof RelationDefinition) {
				createRelation((RelationDefinition)definition);
			}
		}

		for(int i = 0; i < drawElements.size(); i++) {
			UMLetDefinition umLetDefinition = drawElements.keySet().stream().skip(i).findFirst().orElse(null);
			DrawElement drawElement = drawElements.get(umLetDefinition);
			drawElement.setX((i % CLASSES_PER_LINE) * (BOX_DEFAULT_WIDTH + BOX_SPACING));
			drawElement.setY((i / CLASSES_PER_LINE) * (BOX_DEFAULT_HEIGHT + BOX_SPACING));
		}

		for(int i = 0; i < relationElements.size(); i++){
			RelationDefinition umLetDefinition = relationElements.keySet().stream().skip(i).findFirst().orElse(null);
			RelationDrawElement relationDrawElement = relationElements.get(umLetDefinition);
			if(umLetDefinition.getSource() == null || umLetDefinition.getDestination() == null) continue;
			UMLetDefinition source = drawElements.keySet().stream().filter(d -> umLetDefinition.getSource().equals(d)).findFirst().orElse(null);
			UMLetDefinition destination = drawElements.keySet().stream().filter(d -> umLetDefinition.getDestination().equals(d)).findFirst().orElse(null);
			DrawElement sourceDraw = drawElements.get(source);
			DrawElement destinationDraw = drawElements.get(destination);

			int minX = Math.min(sourceDraw.getX(), destinationDraw.getX());
			int maxX = Math.max(sourceDraw.getX(), destinationDraw.getX());
			int minY = Math.min(sourceDraw.getY(), destinationDraw.getY());
			int maxY = Math.max(sourceDraw.getY(), destinationDraw.getY());
			relationDrawElement.setX(minX + BOX_DEFAULT_WIDTH - 10);
			relationDrawElement.setY(minY + BOX_DEFAULT_HEIGHT / 2);
			int realHeight = maxY - minY;
			relationDrawElement.setHeight(realHeight);
			int realWidth = maxX - minX;
			relationDrawElement.setWidth(realWidth - BOX_DEFAULT_WIDTH + 20);

			int sourceX = sourceDraw.getX() - minX - 10;
			if(sourceX < 0) sourceX = -sourceX;
			else sourceX -= BOX_DEFAULT_WIDTH - 20;
			int sourceY = sourceDraw.getY() - minY - 10;
			if(sourceY < 0) sourceY = -sourceY;
			else sourceY += 20;
			relationDrawElement.getPoints().add(new Point(sourceX, sourceY));
			int destinationX = destinationDraw.getX() - minX - 10;
			if(destinationX < 0) destinationX = -destinationX;
			else destinationX -= BOX_DEFAULT_WIDTH - 20;
			int destinationY = destinationDraw.getY() - minY - 10;
			if(destinationY < 0) destinationY = -destinationY;
			else destinationY += 20;
			relationDrawElement.getPoints().add(new Point(destinationX, destinationY));
		}

		buildXmlElements(drawElements.values());
		buildXmlElements(relationElements.values().stream().filter(f -> f.getRelationType() == RelationType.CUSTOM)
				.map(RelationDrawElement::toDrawElement).collect(Collectors.toList()));

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			transformer.transform(source, new StreamResult(byteArrayOutputStream));
			return byteArrayOutputStream.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void buildXmlElements(Collection<DrawElement> values) {
		for(DrawElement element : values) {
			Element xmlElement = document.createElement("element");
			xmlElement.appendChild(createElement("id", element.getId()));
			Node coordinates = createElement("coordinates", null);
			xmlElement.appendChild(coordinates);
			coordinates.appendChild(createElement("x", String.valueOf(element.getX())));
			coordinates.appendChild(createElement("y", String.valueOf(element.getY())));
			coordinates.appendChild(createElement("w", String.valueOf(element.getWidth())));
			coordinates.appendChild(createElement("h", String.valueOf(element.getHeight())));
			xmlElement.appendChild(createElement("panel_attributes", element.getPanelAttributes()));
			xmlElement.appendChild(createElement("additional_attributes", element.getAdditionalAttributes()));
			rootElement.appendChild(xmlElement);
		}
	}

	private Node createElement(String name, String value) {
		Element element = document.createElement(name);
		if(value != null)
			element.setTextContent(value);
		return element;
	}

	private void createRelation(RelationDefinition definition) {
		RelationDrawElement value = new RelationDrawElement();
		value.setRelationType(Objects.equals(definition.getType(), "@") ? RelationType.CONTAINS : RelationType.CUSTOM);
		StringBuilder builder = new StringBuilder();
		if(value.getRelationType() == RelationType.CUSTOM) {
			builder.append("lt=").append(definition.getType()).append("\n");
		}
		if(definition.getSourceMultiplicity() != null) {
			builder.append("m1=").append(definition.getSourceMultiplicity()).append("\n");
		}
		if(definition.getDestinationMultiplicity() != null) {
			builder.append("m2=").append(definition.getDestinationMultiplicity()).append("\n");
		}
		value.setPanelAttributes(builder.toString());
		relationElements.put(definition, value);
	}

	private void createPackage(PackageDefinition definition) {
		DrawElement drawElement = new DrawElement();
		drawElement.setId("UMLPackage");
		drawElement.setPanelAttributes(definition.getName());
	}

	private void createUmlClass(ClassDefinition definition) {
		DrawElement drawElement = new DrawElement();
		drawElement.setId("UMLClass");
		drawElement.setWidth(BOX_DEFAULT_WIDTH);
		drawElement.setHeight(BOX_DEFAULT_HEIGHT);

		String builder = definition.getName() +
				"\n--\n" +
				definition.getClassMembers().stream().filter(m -> !(m instanceof ClassMethodDefinition))
						.map(m -> m.getName() + ":" + m.getType()).collect(Collectors.joining("\n")) +
				"\n--\n" +
				definition.getClassMembers().stream().filter(m -> (m instanceof ClassMethodDefinition))
						.map(m -> (ClassMethodDefinition) m)
						.map(m -> m.getName() + "(" + m.getParameters().stream().map(p -> p.getName() + ":" + p.getType()).collect(Collectors.joining(","))
								+ ") : " + m.getType()).collect(Collectors.joining("\n"));

		drawElement.setPanelAttributes(builder);

		drawElements.put(definition, drawElement);
	}
}
