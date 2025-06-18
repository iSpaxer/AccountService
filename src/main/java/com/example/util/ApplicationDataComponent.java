package com.example.util;

import lombok.Getter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class ApplicationDataComponent {

    private final List<Map<String, String>> technologies;

    private final BuildProperties buildProperties;

    public ApplicationDataComponent(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
        this.technologies = initTechnologies();
    }

    public String glueEndpoints(String api) {
        return "/api/v" + this.getBuildProperties().getVersion() + api;
    }

    private List<Map<String, String>> initTechnologies() {
        List<Map<String, String>> techList = new ArrayList<>();

        try {
            File pomFile = new File("pom.xml");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(pomFile);
            Element root = document.getRootElement();
            Namespace ns = root.getNamespace();

            Map<String, String> propertiesMap = new HashMap<>();
            Element propertiesElement = root.getChild("properties", ns);
            if (propertiesElement != null) {
                for (Element prop : propertiesElement.getChildren()) {
                    propertiesMap.put(prop.getName(), prop.getTextTrim());
                }
            }

            Element dependencies = root.getChild("dependencies", ns);
            if (dependencies != null) {
                for (Element dependency : dependencies.getChildren("dependency", ns)) {

                    String groupId = dependency.getChildText("groupId", ns);
                    String artifactId = dependency.getChildText("artifactId", ns);
                    String version = dependency.getChildText("version", ns);

                    Map<String, String> tech = new HashMap<>();
                    tech.put("groupId", groupId);
                    tech.put("artifactId", artifactId);

                    if (version != null && version.startsWith("${") && version.endsWith("}")) {
                        tech.put("version",
                                propertiesMap.getOrDefault(
                                        version.substring(2, version.length() - 1), "unknown")
                        );
                    } else {
                        tech.put("version", propertiesMap.get("spring-boot.version"));
                    }
                    techList.add(tech);
                }
            }

        } catch (IOException | JDOMException e) {
            throw new RuntimeException("Error with pom.xml");
        }

        return techList;
    }
}
