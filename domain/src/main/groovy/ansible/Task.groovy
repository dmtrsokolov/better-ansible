package ansible


class Task implements AnsibleEntity{

    String name
    String code
    Map<String, Object> args
    Closure<Handler> notify

    void yum(@DelegatesTo(Yum) Closure closure) {
        Yum yum = new Yum()
        this.code = 'yum'
        closure.delegate = yum
        closure()
        this.setArgs(['name': yum.packageName, 'state': yum.state])
    }

    void template(@DelegatesTo(Template) Closure closure) {
        Template template = new Template()
        this.code = 'template'
        closure.delegate = template
        closure()
        this.setArgs(['src' : template.getSrc(), 'dest' : template.getDest()])
    }

    class Yum {
        String packageName
        String state
    }

    class Template {
        String src
        String dest
    }
}
