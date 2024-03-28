# simple-tracing-usage

> 链路追踪的简单应用
> 
> ~~我逐渐理解了一切~~🤡

---

## 配置获取

### SpringBoot 获取 pom 配置

1、先在 pom.xml 中配置

```xml
<build>
    <plugins>
        <!-- application.yml 获取 maven 配置 -->
        <!-- 比如 @project.artifactId@ -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <configuration>
                <delimiters>
                    <delimiter>@</delimiter>
                </delimiters>
                <useDefaultDelimiters>false</useDefaultDelimiters>
            </configuration>
        </plugin>
    </plugins>
</build>
```

2、在 application.yml 中获取

```yml
spring:
  application:
    name: @project.artifactId@
```

3、代码中如何使用？

直接使用 `@Value` 获取

## 日志门面与日志们

日志门面也就是 SLF4J，早期日志工具神仙打架场面混乱，SLF4J 就站了出来，作为一个接口层统一了日志接口风格。 
除了 SLF4J 还有 JUL 这样的日志接口层。

而 log4j、logback、log4j2 这些日志们则是不同的日志具体实现。

## logback 配置唯一化

只需要一个 logback 配置，放到 core 中，其他项目拉取即可。

具体实现在 [tracing-core](tracing-core)#com.stu.listener.ApplicationStartedEventListener

---

## TraceID

> [TraceId & SpanId](https://zhuanlan.zhihu.com/p/374885660)

进行链路追踪我们一般都需要借助到 TraceID，根据 TraceID 来追踪请求链路。

1、普通请求

使用 Interceptor 来进行处理。
在请求进来的时候先判断 RequestHeader 是否存在 traceId，如果存在则复用；不存在表示这是一次请求的开始，生成新的 traceId。

> 本来是想使用 Aspect 来添加 traceId， 但是请求进来后先经过 Servlet 层，在切面添加的话就稍微有点迟了。

具体实现在 [tracing-core](tracing-core)#com.stu.interceptor.TraceInterceptor

启动服务 [tracing-service-a](tracing-service-a) 发送请求 `/api/a/once`：

```shell
2024-03-28 13:46:02.025 [http-nio-8001-exec-1] INFO  -[acc888d8-d616-46f2-b1c1-31b1b691bd13]-  c.s.i.TraceInterceptor#[preHandle,24] - request url: /api/a/once
2024-03-28 13:46:02.033 [http-nio-8001-exec-1] INFO  -[acc888d8-d616-46f2-b1c1-31b1b691bd13]-  c.s.service.ServiceA#[srvOnce,13] - trace id in serviceA: acc888d8-d616-46f2-b1c1-31b1b691bd13
```

可以看到 traceId 被带上了，并且能在业务层成功获取到。

2、普通请求 + 多线程

在业务层方法中添加部分代码

```java
public void srvOnce() {
    String traceId = MDC.get(TraceConstants.TRANCE_ID);
    log.info("trace id in serviceA: {}", traceId);
    
    // 添加部分代码
    Thread threadA = new Thread(() -> {
        String traceIdInner = MDC.get(TraceConstants.TRANCE_ID);
        log.info("trace id inner in serviceA#innerThread: {}", traceIdInner);
    });
    threadA.start();
}
```

再次请求，发现 threadA 内无法获取到 traceId

```shell
2024-03-28 13:46:02.034 [Thread-2] INFO  -[]-  c.s.service.ServiceA#[lambda$srvOnce$0,17] - trace id inner in serviceA#innerThread: null
```

多线程场景下无法获取到 traceId 如何解决呢？

为什么获取不到？`MDC#get` 内部实现原理是什么？

以 `ch.qos.logback.classic.util.LogbackMDCAdapter` 为例：
```java
final ThreadLocal<Map<String, String>> readWriteThreadLocalMap = new ThreadLocal<Map<String, String>>();

@Override
public void put(String key, String val) throws IllegalArgumentException {
    if (key == null) {
        throw new IllegalArgumentException("key cannot be null");
    }
    Map<String, String> current = readWriteThreadLocalMap.get(); // *
    if (current == null) {
        current = new HashMap<String, String>();
        readWriteThreadLocalMap.set(current);
    }

    current.put(key, val);
    nullifyReadOnlyThreadLocalMap();
}

@Override
public String get(String key) {
    Map<String, String> hashMap = readWriteThreadLocalMap.get(); // *

    if ((hashMap != null) && (key != null)) {
        return hashMap.get(key);
    } else {
        return null;
    }
}
```

一目了然，核心是 ThreadLocal。

因为 ThreadLocal 变量只能被当前线程访问，所以在多线程环境下我们自然也就获取不到 traceId。

如何处理呢？或许可以使用 InheritableThreadLocal，让子线程可以继承 ThreadLocal 变量。

测试了一下，<span style="color: orange">使用 InheritableThreadLocal 确实可以解决问题，但同时它也存在问题。</span>

如果使用了线程池

```java
public void srvPool() {
    log.info("trace id outer: {}", MDC.get(TraceConstants.TRACE_ID));

    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    for (int i = 0; i < threadCount; i++) {
        final int finalI = i;
        Thread thread = new Thread(() -> {
            log.info("thread-{}, trace-id: {}", finalI, MDC.get(TraceConstants.TRACE_ID));
        });
        executorService.execute(thread);
    }
}
```

使用 InheritableThreadLocal 时，<span style="color: red">由于线程池的复用机制，复用的线程获取到的可能是上一次或者上上次的 traceId。</span>

最后使用阿里巴巴开源的 transmittable-thread-local，历程基本和[这篇文章](https://blog.csdn.net/xiaolong7713/article/details/127274003)一致。

还有一个解决方案是使用 [logback-mdc-ttl](https://github.com/ofpay/logback-mdc-ttl)

3、跨服务传递

跨服务传递就比较简单了，思路很清晰，拦截请求，在 RequestHeader 中添加 traceId，下游服务从 traceId 中获取即可。

这里以 RestTemplate 为例，具体实现在 [tracing-core](tracing-core)com.stu.interceptor.RestTemplateInterceptor

不同的 Rest 客户端可能还是需要实现不同的拦截器，具体情况具体分析。

...

**总结**
* 单体应用：MDC + transmittable-thread-local
* 分布式：将 TraceId 设置到 RequestHeader 中向下传递

---

## 参考

* [SpringBoot+MDC 实现全链路调用日志跟踪](https://juejin.cn/post/6844904101483020295)
* [SpringBoot 获取 Maven 配置](https://qinguan.github.io/2018/03/11/spring-boot-internal-properties-read/)
* [TransmittableThreadLocal (TTL) 解决异步执行时上下文传递的问题](https://houbb.github.io/2023/07/19/ttl)
* [Slf4j MDC使用transmittable-thread-local解决多线程日志跟踪](https://blog.csdn.net/xiaolong7713/article/details/127274003)