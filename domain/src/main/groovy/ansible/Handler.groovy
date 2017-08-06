package ansible

class Handler implements AnsibleEntity {
    String name
    Map<String, String> service

    void service(@DelegatesTo(Service) Closure closure) {
        Service srvc = new Service()
        closure.delegate = srvc
        closure()
        this.setService(['name': srvc.getServiceName(), 'state': srvc.getState()])
    }

    class Service {
        String serviceName
        String state
    }
}
