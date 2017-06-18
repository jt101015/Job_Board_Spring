package main.java.com.jobBoard;


public class Response {

    private String code = null;
    private String msg = null;
    
    public Response() { }
    
    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public Response(String code) { this.code = code; }
    
    public String getCode() { return this.code; }
    public void setCode(String code) { this.code = code; }
    
    public String getMsg() { return this.msg; }
    public void setMsg(String msg) {this.msg = msg;}

}