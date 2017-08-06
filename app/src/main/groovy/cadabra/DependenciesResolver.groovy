package cadabra

import ansible.Dependency
import groovy.transform.CompileStatic
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files

@CompileStatic
class DependenciesResolver {

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
