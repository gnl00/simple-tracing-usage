# simple-tracing-usage

> 链路追踪的简单应用
> 
> ~~我逐渐理解了一切🤡~~

## 日志门面与日志们

日志门面也就是 SLF4J，早期日志工具神仙打架场面混乱，SLF4J 就站了出来，作为一个接口层统一了日志接口风格。 
除了 SLF4J 还有 JUL 这样的日志接口层。

而 log4j、logback、log4j2 这些日志们则是不同的日志具体实现。

## logback 配置唯一化

只需要一个 logback 配置，放到 core 中，其他项目拉取即可。

具体实现在 `com.stu.listener.ApplicationStartedEventListener`

---

## TraceID

进行链路追踪我们一般都需要借助到 TraceID，根据 TraceID 来追踪请求链路。

* 单体应用：MDC + ThreadLocal 传递 or 切面，单线程 & 多线程 TODO
* 分布式：将 TraceId 设置到 RequestHeader 中向下传递 TODO

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

---

## 参考

* [SpringBoot+MDC 实现全链路调用日志跟踪](https://juejin.cn/post/6844904101483020295)
* [SpringBoot 获取 Maven 配置](https://qinguan.github.io/2018/03/11/spring-boot-internal-properties-read/)