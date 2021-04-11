package i.

������;

import java.util.Map;

public class Command {

    public String op;
    public String result;
    public Map<String, String> data;
    public Map<String, User> users;

    public Command(String op, String result) {
        this.op = op;
        this.result = result;
    }

}
