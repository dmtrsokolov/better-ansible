package cadabra;

import ansible.AnsibleEntity;
import ansible.Dependency;
import ansible.Handler;
import ansible.Play;
import ansible.Playbook;
import ansible.Task;
import ansible.Var;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlRepresenter extends Representer {

    public YamlRepresenter() {
        this.representers.put(Playbook.class, new RepresentPlaybook());
        this.representers.put(Play.class, new RepresentPlay());
        this.representers.put(Var.class, new RepresentVar());
        this.representers.put(Task.class, new RepresentTask());
        this.representers.put(Dependency.class, new RepresentDependency());
        this.addClassTag(Task.class, Tag.MAP);
        this.addClassTag(Handler.class, Tag.MAP);
    }

    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (javaBean instanceof AnsibleEntity && "metaClass".equals(property.getName())) {
            return null;
        } else {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }


    private class RepresentPlaybook implements Represent {
        public Node representData(Object data) {
            Playbook playbook = (Playbook) data;
            return representSequence(Tag.SEQ, playbook.getPlays(), false);
        }
    }

    private class RepresentPlay implements Represent {
        public Node representData(Object data) {
            Play play = (Play) data;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("hosts", play.getHosts());
            if (play.getConnection() != null) {
                map.put("connection", play.getConnection());
            }
            if (!play.getVars().isEmpty()) {
                map.put("vars", play.getVars());
            }
            map.put("tasks", play.getTasks());
            if (!play.getHandlers().isEmpty()) {
                map.put("handlers", play.getHandlers());
            }
            return representMapping(Tag.MAP, map, false);
        }
    }

    private class RepresentVar implements Represent {
        public Node representData(Object data) {
            Var variable = (Var) data;
            return representMapping(Tag.MAP, Collections.singletonMap(variable.getName(), variable.getValue()), false);
        }
    }

    private class RepresentTask implements Represent {
        public Node representData(Object data) {
            Task task = (Task) data;
            Map<Object, Object> map = new LinkedHashMap<>();
            map.put("name", task.getName());
            map.put(task.getCode(), task.getArgs());
            if (task.getNotify() != null) {
                map.put("notify", new String[] {task.getNotify().call().getName()});
            }
            return representMapping(Tag.MAP, map, false);
        }
    }

    private class RepresentDependency implements Represent {

        @Override
        public Node representData(Object data) {
            Dependency dependency = (Dependency) data;
            Map<String, String> map = new LinkedHashMap<>();
            if (dependency.getSrc() != null) {
                map.put("src", dependency.getSrc());
            }
            if (dependency.getSrc() != null) {
                map.put("scm", dependency.getScm());
            }
            if (dependency.getVersion() != null) {
                map.put("version", dependency.getVersion());
            }
            if (dependency.getName() != null) {
                map.put("name", dependency.getName());
            }
            return representMapping(Tag.MAP, map, false);
        }
    }
}
