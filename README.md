## js-api-proxy 
一个用来对混淆的js代码进行逆向工程的工具

### usage
- step1. 将 js-api-proxy.jar 添加为burpsuite的插件
- step2. 启动一个静态资源服务器
```
cd js
python3 -m http.server 8484 
```

### 工作原理
js-api-proxy 将会在所有的Content-Type为HTML的响应报文的<head>标签之后添加一段代码:
```html 
<!--js api proxy start--> <script src="http://localhost:8484/proxy.js"></script><!--js api proxy end-->
```
该段代码将请求运行在本地8484 端口的静态资源服务器上的proxy.js ，proxy.js 代理了常用的js api 函数， 详细代码如下：
```javascript
handler = {
    apply: function(target,thisArg,argumentsList){
      //打日志
      console.log("[调用] "+ target+': '+argumentsList.join(','));
      console.log(argumentsList[0]);
      //调用原函数
      return target.apply(thisArg,argumentsList)
    }
  }
  // 给window.open 挂上代理 
  window.open  = new Proxy(window.open,handler);
  // 给createElement api 挂上代理
  document.createElement = new Proxy(document.createElement,handler);
  // 给 appendChild api 挂上代理
  Element.prototype.appendChild = new Proxy(Element.prototype.appendChild,handler);
  // 给 removeChild api 挂上代理
  Element.prototype.removeChild = new Proxy(Element.prototype.removeChild,handler);
  ...
```
这样一来当相关函数被调用时，将会在控制台输出调用日志。
