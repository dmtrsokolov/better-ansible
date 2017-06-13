package cadabra.ansible


class Play implements AnsibleEntity {

    String hosts
    String connection
    List<Var> vars = []
    List<Task> tasks = []
    List<Handler> handlers = []

    void vars(@DelegatesTo(Play) Closure closure) {
        closure.delegate = this
        closure()
    }

    void variable(Var variable) {
        this.vars << variable
    }

    void variable(String name, Object value) {
        this.vars << new Var(name: name, value: value)
    }

    void tasks(@DelegatesTo(Play) Closure closure) {
        closure.delegate = this
        closure()
    }

    void task(Task task) {
        this.tasks << task
    }

    void task(@DelegatesTo(Task) Closure closure) {
        Task task = new Task()
        this.tasks << task
        closure.delegate = task
        closure()
    }

    void handlers(@DelegatesTo(Play) Closure closure) {
        closure.delegate = this
        closure()
    }

    void handler(Handler handler) {
        this.handlers += handler
    }

    void handler(@DelegatesTo(Handler) Closure closure) {
        Handler handler = new Handler()
        this.handlers += handler
        closure.delegate = handler
        closure()
    }
}
