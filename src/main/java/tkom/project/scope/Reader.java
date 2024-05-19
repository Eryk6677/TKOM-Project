package tkom.project.scope;

public class Reader {
    private String content;

    public void write(String newContent) {
        this.content = newContent;
    }

    public String read() {
        return this.content;
    }
}
