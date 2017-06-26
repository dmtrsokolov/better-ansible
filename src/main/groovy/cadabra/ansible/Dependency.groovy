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


    static boolean resolveDependencies(List<Dependency> dependencies, String rolesPath) {
        DumperOptions options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        YamlRepresenter representer = new YamlRepresenter()
        Yaml yaml = new Yaml(representer, options)

        File dependenciesFile = Files.createTempFile('dependencies', '.yml').toFile()
        println(dependenciesFile.absolutePath)
        dependenciesFile.write(yaml.dump(dependencies))
        dependenciesFile.deleteOnExit()

        def result = "ansible-galaxy install -r ${dependenciesFile.absolutePath} --roles-path $rolesPath -vvvv".execute()
        println result.text
        return result.exitValue() == 0
    }
}
