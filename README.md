# spring-tiny-auth
![Maven Central](https://img.shields.io/maven-central/v/com.github.lkqm/spring-tiny-auth)
![Travis (.org) branch](https://img.shields.io/travis/lkqm/spring-tiny-auth/master)

一个简单权限管理框架(代码不到400行), 支持基于路径拦截、支持rest风格路径权限、注解@Auth支持多种权限认证(基于角色的权限), 不支持登录、登出、缓存等功能（与权限认证无关, 业务中可自定灵活实现）.

说明: 内部基于spring webmvc框架HandlerInterceptor拦截器实现

## Future
- 十分钟上手, 代码不到400行
- 支持基于路径的权限控制(rest风格)
- 注解@Auth控制访问权限
- 内置支持多种token管理方案
- 支持JDK1.7+, Spring Boot1.5.x, Spring Boot2.x

## Quick: spring-boot
1. 添加依赖
    ```xml
    <dependency>
        <groupId>com.github.lkqm</groupId>
        <artifactId>spring-tiny-auth</artifactId>
        <version>${version}</version>
    </dependency>
    ```
2. 提供自定义`AuthInfoProvider`
    ```java
    @Component
    public class TestAuthInfoProvider implements AuthInfoProvider {
        @Override
        public AuthInfo doGetAuthInfo() {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            // !!!: 解析请求得到相关的用户信息
            // AuthInfo: 提供了权限信息(路径权限、角色、权限), 是否是超级管理员, 登录是否过期
            return null;
        }
    }
    ```
    
3. 启动类添加注解: `@EnableTinyAuth`

4. 配置
    ```properties
       tiny-auth.anno-patterns=/login                                 # 允许匿名访问的地址
       tiny-auth.authen-patterns=/logout, /admin/menu, /admin/info    # 只需要认证(登录)就能访问的地址
       tiny-auth.author-patterns=/**                                  # 需要认证授权的地址
    ```

5. 相关异常

    当访问保护资源未登录、登录过期、或者无权限时，会抛出特定异常, 需要自定义处理异常, 异常结构如下:
    ```
    |-- AuthException                # Auth相关的基异常
    |---- AuthNotLoggedException     # 未登录
    |---- AuthExpiredException       # 登录过期
    |---- AuthPermissionException    # 无访问权限
    ```

## 路径权限
注意路径权限是取匹配Controller处理方法进行绝对相等比较, 当:`/user/{id}`, 不匹配例子: `/user/{idx}`

## @Auth注解
@Auth注解value值指定了spring el表达式, 例如: `@Auth("authen()")`, 内部预定义表达式如下:

- anno(): 匿名访问
- authen(): 只需要认证(登录)
- hasRole('admin', 'operator'): 拥有任意一个角色
- hasAllRole('admin', 'operator'): 必须拥有所有角色
- hasPermission('admin', 'operator'): 拥有任意一个权限
- hasAllPermission('complaint:add', 'complaint:delete'): 必须拥有所有权限

提示: 常量类`AuthConstants`定义了`anno(), authen()`表达式

## Token管理
TokenManager内置提供的token管理, 支持如下几种类型, 可通过配置`tiny-auth.token.type`:
- HttpSessionTokenManager(httpsession): 基于HttpSession存储
- JdbcTokenManager(jdbc): 基于关系型数据库, 使用需要手动创建表
- JedisTokenManager(jedis): 基于redis存储, 使用jedis客户端连接
- RedisTemplateTokenManager(redistemplate): 基于redis存储, 使用RedisTemplate连接

## 类似项目
- [light-security](https://github.com/eacdy/light-security): Light Security是一个基于jwt的权限控制框架，支持与Spring Boot配合使用，支持Spring MVC与WebFlux 
