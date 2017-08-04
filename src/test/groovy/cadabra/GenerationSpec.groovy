package cadabra

import cadabra.ansible.Role
import spock.lang.Specification
import spock.lang.Unroll

class GenerationSpec extends Specification {

    @Unroll
    def test() {
        expect:
        Role.generate('Fake','/Users/dsokolov/git/better-ansible/src/test/resources/roles/fake-role', '/Users/dsokolov/git/better-ansible/generated')
    }
}
