package cadabra.ansible

class Inventory {

    List<String> hosts
    List<Group> groups


    static class Group {
        String name
        List<String> hosts
    }

    String toString() {
        StringBuilder builder = new StringBuilder()
        hosts.each {
            builder.append(it).append(System.lineSeparator())
        }
        builder.append(System.lineSeparator())
        groups.each {
            builder.append("[${it.name}]").append(System.lineSeparator())
            it.hosts.each {
                builder.append(it).append(System.lineSeparator())
            }
            builder.append(System.lineSeparator())
        }
        return builder.toString()
    }
}
