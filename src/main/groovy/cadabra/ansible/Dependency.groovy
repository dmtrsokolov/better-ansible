package cadabra.ansible

import cadabra.YamlRepresenter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files


class Dependency implements AnsibleEntity {
    String src
    String scm
    String version
    String name


    static void resolveDependencies(List<Dependency> dependencies, String rolesPath) {
        DumperOptions options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        YamlRepresenter representer = new YamlRepresenter()
        Yaml yaml = new Yaml(representer, options)

        File dependenciesFile = Files.createTempFile('dependencies', 'yml').toFile()
        dependenciesFile.write(yaml.dump(dependencies))
        dependenciesFile.deleteOnExit()

//        "ansible-galaxy  -vvvv".execute().text
    }
}
