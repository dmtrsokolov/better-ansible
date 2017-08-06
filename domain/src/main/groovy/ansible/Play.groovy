package ansible


class Play implements AnsibleEntity {

    String hosts
    String connection
    List<Var> vars = []
    List<Task> tasks = []
    List<Role> roles = []
    List<Handler> handlers = []

    void vars(@DelegatesTo(Variables) Closure closure) {
        Variables variables = new Variables(play: this)
        closure.delegate = variables
        closure()
    }

    void tasks(@DelegatesTo(Tasks) Closure closure) {
        Tasks tasks = new Tasks(play: this)
        closure.delegate = tasks
        closure()
    }

    void roles(@DelegatesTo(Roles) Closure closure) {
        Roles roles = new Roles(play: this)
        closure.delegate = roles
        closure()
    }

    void handlers(@DelegatesTo(Handlers) Closure closure) {
        Handlers handlers = new Handlers(play: this)
        closure.delegate = handlers
        closure()
    }

    static class Variables {
        Play play

        void variable(Var variable) {
            play.vars << variable
        }

        void variable(String name, Object value) {
            play.vars << new Var(name: name, value: value)
        }

    }

    static class Tasks {
        Play play

        void task(Task task) {
            play.tasks << task
        }

        void task(@DelegatesTo(Task) Closure closure) {
            Task task = new Task()
            play.tasks << task
            closure.delegate = task
            closure()
        }
    }

    static class Roles {
        Play play

        void role(Role role) {
            play.roles << role
        }

        void role(@DelegatesTo(Role) Closure closure) {
            Role role = new Role()
            play.roles << role
            closure.delegate = role
            closure()
        }
    }

    static class Handlers {
        Play play

        void handler(Handler handler) {
            play.handlers += handler
        }

        void handler(@DelegatesTo(Handler) Closure closure) {
            Handler handler = new Handler()
            play.handlers += handler
            closure.delegate = handler
            closure()
        }
    }
}
