package cadabra

import cadabra.ansible.Dependency
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
        Dependency.resolveDependencies([dependency], "/Users/dsokolov/git/better-ansible/generated")
//        Role.generate('consul','/Users/dsokolov/git/better-ansible/generated/consul/', '/Users/dsokolov/git/better-ansible/generated')
    }}
