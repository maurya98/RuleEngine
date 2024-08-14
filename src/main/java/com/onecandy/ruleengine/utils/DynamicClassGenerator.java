package com.onecandy.ruleengine.utils;

import org.codehaus.janino.SimpleCompiler;
import java.util.Map;

public class DynamicClassGenerator {

    public static Class<?> generateClass(String className, Map<String, String> fields) throws Exception {
        StringBuilder classCode = new StringBuilder();
        classCode.append("import java.util.*;\n");  // Import statements for collections
        classCode.append("public class ").append(className).append(" {\n");

        // Add fields
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            classCode.append("    private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
        }

        // Add getters and setters
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldType = entry.getValue();
            String fieldName = entry.getKey();

            // Getter
            classCode.append("    public ").append(fieldType).append(" get")
                    .append(capitalize(fieldName)).append("() {\n")
                    .append("        return ").append(fieldName).append(";\n")
                    .append("    }\n");

            // Setter
            classCode.append("    public void set").append(capitalize(fieldName))
                    .append("(").append(fieldType).append(" ").append(fieldName).append(") {\n")
                    .append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n")
                    .append("    }\n");
        }

        classCode.append("}\n");

        // Compile the class
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.cook(classCode.toString());

        return compiler.getClassLoader().loadClass(className);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}