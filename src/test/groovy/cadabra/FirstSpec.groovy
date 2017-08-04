package cadabra

import cadabra.ansible.Inventory
import cadabra.ansible.Playbook
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.*
import org.apache.commons.io.FileUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification
import spock.lang.Unroll

import static com.spotify.docker.client.DockerClient.ListImagesParam.byName

class FirstSpec extends Specification {

    final String imageName = 'centos/systemd'

    final static DockerClient docker = DefaultDockerClient.fromEnv().build()
    String id

    def setup() {
        if (docker.listImages(byName(imageName)).isEmpty()) {
            docker.pull(imageName)
        }
        final String[] ports = ["80", "22"]
        final Map<String, List<PortBinding>> portBindings = new HashMap<>()
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>()
            hostPorts.add(PortBinding.of("0.0.0.0", port))
            portBindings.put(port, hostPorts)
        }

        List<PortBinding> randomPort = [PortBinding.randomPort("0.0.0.0")]
        portBindings.put("443", randomPort)

        final HostConfig hostConfig = HostConfig.builder()
                .portBindings(portBindings)
                .appendBinds('/sys/fs/cgroup:/sys/fs/cgroup:ro')
                .privileged(true)
                .build()


        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(imageName)
                .exposedPorts(ports)
                .build()

        final ContainerCreation creation = docker.createContainer(containerConfig)
        id = creation.id()
        final ContainerInfo info = docker.inspectContainer(id)
        println(info)

        docker.startContainer(id)
    }

    def cleanup() {
        docker.killContainer(id)
        docker.removeContainer(id)
    }

    def cleanupSpec() {
        docker.close()
    }

    @Unroll
    def test() {
        expect:
        def playbook = Playbook.of {
            play {
                hosts = id
                connection = 'docker'
                vars {
                    variable('http_port', 80)
                    variable('clients', 200)
                }
                tasks{
                    task {
                        name = 'ensure apache is at the latest version'
                        yum {
                            packageName = 'httpd'
                            state = 'latest'
                        }
                    }
                    task {
                        name = 'write the apache config file'
                        template {
                            src = 'srv/httpd.j2'
                            dest = '/etc/httpd.conf'
                        }
                        notify = {
                            handlers.find {it.name == 'restart apache'}
                        }
                    }
                }
                handlers {
                    handler {
                        name = 'restart apache'
                        service {
                            serviceName = 'httpd'
                            state = 'restarted'
                        }
                    }
                }
            }
        }

        def workingDir = new File('test')

        if (workingDir.exists()) {
            workingDir.deleteDir()
        }
        workingDir.mkdirs()

        DumperOptions options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        YamlRepresenter representer = new YamlRepresenter()
        Yaml yaml = new Yaml(representer, options)
        Inventory inventory = new Inventory(hosts: [id])

        new File(workingDir, 'main.yml').write(yaml.dump(playbook))
        def inventoryFile = new File(workingDir,'inventory/main.yml')
        def inventoryDir = inventoryFile.getParentFile()
        if(!inventoryFile.exists()) {
            inventoryDir.mkdirs()
        }
        inventoryFile.write(inventory.toString())
        FileUtils.copyDirectory(new File('src/test/resources'), new File('test'))
        def result = "ansible-playbook test/main.yml -i test/inventory/main.yml -vvvv".execute().text
        println(result)
    }
}
