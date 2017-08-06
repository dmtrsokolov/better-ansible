package cadabra

import ansible.Dependency
import spock.lang.Specification
import spock.lang.Unroll

class DependencySpec extends Specification{

    @Unroll
    def test() {
        expect:
        def dependency = new Dependency(
                name: 'consul',
                src: 'git@github.com:infacloud/ansible-role-consul.git',
                scm: 'git',
                version: 'master'
        )
        String destPath = DependencySpec.class.getClassLoader().getResource('.').getFile() + 'generated'
        DependenciesResolver.resolveDependencies([dependency], destPath)
        RoleGenerator.generate('consul',"${destPath}/consul/", destPath)
    }}
