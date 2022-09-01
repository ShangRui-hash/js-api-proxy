handler = {
    apply: function(target,thisArg,argumentsList){
      //打日志
      console.log("[调用] "+ target+': '+argumentsList.join(','));
      console.log(argumentsList[0]);
      //调用原函数
      return target.apply(thisArg,argumentsList)
    }
  }
  // 重写 window.open 函数为一个Proxy
  window.open  = new Proxy(window.open,handler);
  // 给createElement api 挂上代理
  document.createElement = new Proxy(document.createElement,handler);
  // 给 appendChild api 挂上代理
  Element.prototype.appendChild = new Proxy(Element.prototype.appendChild,handler);
  // 给 removeChild api 挂上代理
  Element.prototype.removeChild = new Proxy(Element.prototype.removeChild,handler);
  // // 中断 removeChild
  // Element.prototype.removeChild = function(arg1){
  //   console.log("removeChild: "+arg1);
  //   console.log()
  // }