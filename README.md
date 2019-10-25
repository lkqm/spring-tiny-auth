# spring-tiny-auth
一个简单权限管理框架(代码不到400行), 支持基于路径拦截、支持rest风格路径权限、注解@Auth支持多种权限认证(基于角色的权限), 不支持登录、登出、缓存等功能（与权限认证无关, 业务中可自定灵活实现）.

说明: 内部基于spring webmvc框架HandlerInterceptor拦截器实现

## 特性
- 十分钟上手, 代码不到400行
- 支持基于路径的权限控制(rest风格)
- 注解@Auth控制访问权限
- 支持JDK1.7+, Spring Boot1.5.x, Spring Boot2.x

## 快速开始
1. 添加依赖
    ```xml
    <dependency>
        <groupId>com.github.lkqm</groupId>
        <artifactId>spring-tiny-auth</artifactId>
        <version>1.0.0-SNAPSHOT</version>
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

## @Auth注解
@Auth注解value值指定了spring el表达式, 例如: `@Auth("authen()")`, 内部预定义表达式如下:

- anno(): 匿名访问
- authen(): 只需要认证(登录)
- hasRole('admin', 'operator'): 拥有任意一个角色
- hasAllRole('admin', 'operator'): 必须拥有所有角色
- hasPermission('admin', 'operator'): 拥有任意一个权限
- hasAllPermission('complaint:add', 'complaint:delete'): 必须拥有所有权限
