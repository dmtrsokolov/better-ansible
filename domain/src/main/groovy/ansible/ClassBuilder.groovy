package ansible

import groovy.text.SimpleTemplateEngine

class ClassBuilder {

    GroovyClassLoader loader
    String name
    Class cls
    def imports
    def fields
    def methods

    ClassBuilder() {
        imports = []
        fields = [:]
        methods = [:]
    }

    ClassBuilder(GroovyClassLoader loader) {
        this.loader = loader
        imports = []
        fields = [:]
        methods = [:]
    }

    def setName(String name) {
        this.name = name
    }

    def addImport(Class importClass) {
        imports << "${importClass.getPackage().getName()}" +
                ".${importClass.getSimpleName()}"
    }

    def addField(String name, Class type) {
        fields[name] = type.simpleName
    }

    def addField(String name, String type) {
        fields[name] = type
    }

    def addMethod(String name, Closure closure) {
        methods[name] = closure
    }

    String getCreatedClass() {

        def templateText = '''
<%imports.each {%>import $it\n <% } %> 
class $name {
<%fields.each {%>    $it.value $it.key \n<% } %>
}
'''
        def data = [name: name, imports: imports, fields: fields]

        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(templateText)
        def result = template.make(data)
        return result.toString()
//        cls = loader.parseClass(result.toString())
//        methods.each {
//            cls.metaClass."$it.key" = it.value
//        }
//        return cls
    }
}
