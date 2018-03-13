package io.freefair.umletsimple;

import io.freefair.umletsimple.definition.ClassDefinition;
import io.freefair.umletsimple.definition.ClassMemberDefinition;
import io.freefair.umletsimple.definition.ClassMethodDefinition;
import io.freefair.umletsimple.definition.MethodParameterDefinition;
import io.freefair.umletsimple.definition.PackageDefinition;
import io.freefair.umletsimple.definition.RelationDefinition;
import io.freefair.umletsimple.definition.UMLetDefinition;
import io.freefair.umletsimple.parser.UMLetSimpleLexer;
import io.freefair.umletsimple.parser.UMLetSimpleParser;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args){
		ArgumentParser parser = ArgumentParsers.newArgumentParser("Checksum")
				.defaultHelp(true)
				.description("Calculate checksum of given files.");
		parser.addArgument("inputFile")
				.help("Input file for umlet transformation");
		parser.addArgument("outputFile")
				.help("Output file for umlet transformation");
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		try {
			File inputFile = new File(ns.getString("inputFile"));
			File outputFile = new File(ns.getString("outputFile"));
			if(!inputFile.exists())
			{
				throw new IOException("File " + inputFile.getName() + " not found!");
			}

			UMLetSimpleLexer umLetSimpleLexer = new UMLetSimpleLexer(new ANTLRInputStream(IOUtils.toString(new FileInputStream(inputFile), "utf-8")));
			UMLetSimpleParser simpleParser = new UMLetSimpleParser(new CommonTokenStream(umLetSimpleLexer));

			UMLetSimpleParser.FileContext file = simpleParser.file();
			List<UMLetDefinition> umLetDefinitions = flattenAndMapFile(file);

			String result = new UMLetXmlBuilder(umLetDefinitions).build();
			IOUtils.write(result, new FileOutputStream(outputFile), "utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
			parser.handleError(new ArgumentParserException(ex, parser));
			System.exit(1);
		}
	}

	private static List<UMLetDefinition> flattenAndMapFile(UMLetSimpleParser.FileContext file) {
		List<UMLetDefinition> result = new ArrayList<>();
		List<UMLetSimpleParser.Rule_Context> rule_contexts = file.rule_();
		for(UMLetSimpleParser.Rule_Context rule : rule_contexts) {
			if(rule.package_() != null)
				mapPackage(rule.package_(), result);
			else if (rule.class_() != null)
				mapClass(rule.class_(), result);
			else if (rule.relation() != null)
				mapRelation(rule.relation(), result);

		}
		return result;
	}

	private static void mapRelation(UMLetSimpleParser.RelationContext relation, List<UMLetDefinition> result) {
		RelationDefinition relationDefinition = new RelationDefinition();

		relationDefinition.setName(relation.id().ID().getText().trim());

		String sourceName = relation.source.typename().id().stream().map(i -> i.ID().getText().trim()).collect(Collectors.joining("."));
		relationDefinition.setSource(result.stream().filter(f -> f.getName().equals(sourceName)).findAny().orElse(null));
		relationDefinition.setSourceMultiplicity(relation.source.multiplicity().Multiplicity().getText().trim());

		String destinationName = relation.destination.typename().id().stream().map(i -> i.ID().getText().trim()).collect(Collectors.joining("."));
		relationDefinition.setDestination(result.stream().filter(f -> f.getName().equals(destinationName)).findAny().orElse(null));
		relationDefinition.setDestinationMultiplicity(relation.destination.multiplicity().Multiplicity().getText().trim());

		relationDefinition.setType(relation.direction().getText());

		result.add(relationDefinition);
	}

	private static ClassDefinition mapClass(UMLetSimpleParser.Class_Context class_context, List<UMLetDefinition> result) {
		ClassDefinition classDefinition = new ClassDefinition();

		String name = class_context.name.ID().getText().trim();
		if(class_context.parent instanceof UMLetSimpleParser.Package_Context) {
			name = ((UMLetSimpleParser.Package_Context)class_context.parent).name.ID().getText().trim() + "." + name;
		}
		classDefinition.setName(name);
		if(class_context.customBlock() != null)
			classDefinition.setCustom(class_context.customBlock().ExceptScope().getText().trim().replace("{%", "").replace("%}", ""));

		for(UMLetSimpleParser.ClassMemberContext member : class_context.classMember()) {
			ClassMemberDefinition definition = null;
			if(member.attribute() != null) {
				definition = new ClassMemberDefinition();
				definition.setName(member.attribute().name.ID().getText().trim());
				definition.setType(member.attribute().typename().getText().trim());
			} else if (member.method() != null) {
				definition = new ClassMethodDefinition();
				definition.setName(member.method().name.ID().getText().trim());
				definition.setType(member.method().typename().getText().trim());
				for(UMLetSimpleParser.ParameterContext param : member.method().parameter()) {
					MethodParameterDefinition def = new MethodParameterDefinition();
					def.setName(param.varname.ID().getText().trim());
					def.setType(param.typename().getText().trim());
					((ClassMethodDefinition)definition).getParameters().add(def);
				}
			}
			classDefinition.getClassMembers().add(definition);
		}

		result.add(classDefinition);
		return classDefinition;
	}

	private static void mapPackage(UMLetSimpleParser.Package_Context package_context, List<UMLetDefinition> result) {
		PackageDefinition packageDefinition = new PackageDefinition();
		packageDefinition.setName(package_context.name.ID().getText());
		List<UMLetSimpleParser.Class_Context> class_contexts = package_context.class_();
		for(UMLetSimpleParser.Class_Context classContext : class_contexts) {
			ClassDefinition classDefinition = mapClass(classContext, result);
			result.add(new RelationDefinition() {{
				setType("@");
				setName(UUID.randomUUID().toString());
				setDestination(packageDefinition);
				setSource(classDefinition);
			}});
		}
		result.add(packageDefinition);
	}
}
