package cadabra.ansible

import groovy.transform.Canonical

@Canonical
class Playbook implements AnsibleEntity {

    List<Play> plays = []

    static Playbook of(@DelegatesTo(Playbook) Closure closure) {
        Playbook dsl = new Playbook()
        closure.delegate = dsl
        closure()
        return dsl
    }

    void play(Play play) {
        this.plays += play
    }

    void play(@DelegatesTo(Play) Closure closure) {
        Play play = new Play()
        this.plays += play
        closure.delegate = play
        closure()
    }
}
