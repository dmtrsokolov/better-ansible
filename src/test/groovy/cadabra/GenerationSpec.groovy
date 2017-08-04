package cadabra

import cadabra.ansible.Role
import spock.lang.Specification
import spock.lang.Unroll

class GenerationSpec extends Specification {

    @Unroll
    def test() {
        expect:
        String rolePath = GenerationSpec.class.getClassLoader().getResource('roles/fake-role').getFile()
        String destPath = GenerationSpec.class.getClassLoader().getResource('.').getFile() + 'generated'
        Role.generate('Fake',rolePath, destPath)
    }
}
