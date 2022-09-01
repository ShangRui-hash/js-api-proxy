package burp;


import java.io.PrintWriter;


public class BurpExtender implements IBurpExtender, IProxyListener,IExtensionStateListener {

    private IBurpExtenderCallbacks callbacks;

    private PrintWriter stdout;

    private IExtensionHelpers helpers;


    //

    // implement IBurpExtender

    //
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        // set our extension name
        callbacks.setExtensionName("rickshang/js-api-proxy");
        // obtain our output stream
        stdout = new PrintWriter(callbacks.getStdout(), true);
        // register ourselves as a Proxy listener
        callbacks.registerProxyListener(this);
        callbacks.registerExtensionStateListener(this);
    }


    //
    // implement IProxyListener
    //
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        stdout.println(

                (messageIsRequest ? "Proxy request to " : "Proxy response from ") +

                        message.getMessageInfo().getHttpService());
        //如果是http响应
        if(!messageIsRequest){
            IHttpRequestResponse irr = message.getMessageInfo();
            byte[] response = irr.getResponse();
            IResponseInfo respInfo = helpers.analyzeResponse(response);
            if(200 == respInfo.getStatusCode() && "HTML".equals(respInfo.getStatedMimeType())){
                //定位 <head>
                int headIndex = helpers.indexOf(response,"<head>".getBytes(),false,0,response.length);
                if(headIndex !=-1){
                    stdout.println("找到了 <head>"+"index:"+headIndex);
                    //修改响应报文
                    byte[] payload = "<!--js api proxy start--> <script src='http://localhost:8484/proxy.js'></script><!--js api proxy end-->".getBytes();
                    int newResponseLen = response.length + payload.length;
                    byte[] temp = new byte[newResponseLen];
                    System.arraycopy(response,0,temp,0,headIndex+6);
                    System.arraycopy(payload,0,temp,headIndex+6,payload.length);
                    System.arraycopy(response,headIndex,temp,headIndex+6+payload.length,response.length-(headIndex+6));
                    String newResponse = new String(temp);
                    stdout.println("new Response:"+newResponse);
                    irr.setResponse(newResponse.getBytes());
                    stdout.println(respInfo.getStatedMimeType());
                }else{
                    stdout.println("没有找到 <head>");
                }

            }
        }

    }




    //

    // implement IExtensionStateListener

    //


    @Override

    public void extensionUnloaded() {

        stdout.println("Extension was unloaded");

    }

}
