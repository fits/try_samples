
package simple;

public class SimpleImplementation {

    private String name;
    private String src;

    public String getName() {
        System.out.println("getname : " + this.name);
        return this.name;
    }

    public void setName(String name) {
        System.out.println("name:" + name);
        
        this.name = name;
    }

    public String getSrc() {
        System.out.println("getsrc:" + this.src);
        
        return this.src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
