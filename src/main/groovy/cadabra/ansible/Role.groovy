package cadabra.ansible

import groovy.transform.CompileStatic
import org.yaml.snakeyaml.Yaml

@CompileStatic
class Role implements AnsibleEntity {
    String name
    List<Task> tasks

    static String toCamelCase( String text, boolean capitalized = false ) {
        text = text.replaceAll( "(_)([A-Za-z0-9])", { List<String> it -> it[2].toUpperCase() } )
        return capitalized ? text.capitalize() : text
    }

    static String toSnakeCase( String text ) {
        text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
    }

    static void generate(String name, String path, String dest) {
        Yaml yaml = new Yaml()
        new File(path + '/defaults/main.yml').withReader { reader ->
            Map<String, Object> vars = yaml.loadAs(reader, Map.class)
            ClassBuilder builder = mapToClass(name.capitalize() + 'Role', vars, dest)
        }
    }

    private static ClassBuilder mapToClass(String name, Map<String, Object> vars, String dest) {
        ClassBuilder builder = new ClassBuilder()
        builder.setName(name)
        vars.forEach { String k, Object v ->
            String fieldName = toCamelCase(k)
            switch (v) {
                case String:
                    builder.addField(fieldName, String.class)
                    break
                case Integer:
                    builder.addField(fieldName, Integer.class)
                    break
                case Double:
                    builder.addField(fieldName, Double.class)
                    break
                case Map:
                    Map map = v as Map
                    if (map.isEmpty()) {
                        builder.addField(fieldName, 'Map<String, Object>')
                    } else {
                        String className = fieldName.capitalize()
                        mapToClass(className, map, dest)
                        builder.addField(fieldName, className)
                    }
                    break
                case List:
                    List list = v as List
                    if (list.isEmpty()) {
                        builder.addField(fieldName, 'List<Object>')
                    } else {
                        Map<Class, List> groups = list.groupBy { it.class}
                        if (groups.size() == 1) {
                            Class aClass = list.first().class
                            if (aClass instanceof Map) {
                                String className = fieldName.capitalize()
                                mapToClass(className, list.first() as Map, dest)
                                builder.addField(fieldName, className)
                            } else {
                                builder.addField(fieldName, "List<${aClass.name.replaceFirst('java.lang.', '')}>")
                            }
                        } else {
                            builder.addField(fieldName, 'List<Object>')
                        }
                    }
                    break
            }
        }
        def destinationFolder = new File(dest)
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir()
        }
        File file = new File(destinationFolder, builder.name + '.groovy')
        file.withWriter { Writer writer ->
            writer.write(builder.getCreatedClass())
        }
        builder
    }

    static void generateTaskSources(String path, String dest) {
        new File(path).withReader { reader ->
            Yaml yaml = new Yaml()
            List<Object> tasks = yaml.loadAs(reader, List.class)
            tasks.forEach { task ->
                ClassBuilder builder = new ClassBuilder()
                def name = (task['name'] as String).split().collect{String it -> it.capitalize()}.join('')
                builder.setName(name)

                (task as Map<String, Object>).forEach { k, v ->
                    if ("name" != k) {
                        builder.addField(k as String, String)
                    }
                }
                def destinationFolder = new File(dest)
                if (!destinationFolder.exists()) {
                    destinationFolder.mkdir()
                }
                File file = new File(destinationFolder, builder.name + '.groovy')
                file.withWriter { writer ->
                    writer.write(builder.getCreatedClass())
                }
            }
        }
    }
}
