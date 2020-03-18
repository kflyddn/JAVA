### 慕课网秒杀商城项目

系统访问地址： http://seckill.anythy.cn/

#### 项目架构图 && 业务流程图
![](src/main/docs/seckill_architecture.jpg)

#### 学习笔记

> 技术介绍
* 前端：Thymeleaf, Bootstrap, JQuery
* 后端：SpringBoot, JSR303(服务端验证框架，参数校验)，MyBatis
* 中间件：RabbitMQ(异步操作)，Redis, Druid(连接池，监控)

> 关键技术点
* 分布式会话
* 商品列表页：展示商品
* 商品详情页：秒杀的入口
* 订单详情页：秒杀成功进入商品详情页
* 压力测试：测试并发量
* 缓存优化：缓存静态页面，CDN，Nginx做网关
* 消息队列：异步下单
* 接口安全：防刷，限流，验证码，隐藏地址

> 学到了什么-目标
* 如何利用缓存
* 如何使用异步
* 如何编写优雅的代码

> 开发计划
* SpringBoot环境搭建
* 集成Thymeleaf，Result结果封装
* 集成MyBatis + Druid
* 集成Jedis + Redis安装 + 通用缓存Key封装
* 实现登录功能
* 数据库设计
* 明文密码两次MD5处理
* JSR303参数校验 + 全局异常处理器
* 分布式Session
* 功能实现： 数据库设计，商品列表页，商品详情页，订单详情页
* JMeter压测： 自定义变量模拟多用户，JMeter命令行使用
* 页面优化技术：页面缓存+URL缓存+对象缓存，页面静态化，前后端分离，静态资源优化，CDN优化
* 接口优化：Redis预减库存减少数据库访问，内存标记减少Redis访问，RabbitMQ队列缓冲，异步下单，增强用户体验
* Nginx水平扩展
* 再次压测
* 安全的优化：秒杀接口地址隐藏，数学公式验证码，接口防刷

> 零星笔记
* Result设计
{
    "code": 500100,
    "msg": 库存不足,
    "data": {}, []
}
* Redis集成和安装，bind的意义
* 如何避免Redis中的key冲突，加前缀
* 通用缓存Key封装：接口<-抽象类<-实现类
* incr, decr方法
* 登陆数据库设计
* 两次MD5：双重保险
  * 用户端：PASS=MD5(明文+固定salt) 防止明文密码在网络传输
  * 服务端：PASS=MD5(用户输入+随机salt) 防止数据库被盗，破解出密码
* JSR303参数校验
  * Field error in object 'loginVo' on field 'mobile': rejected value [21345678909]; codes [IsMobile.loginVo.mobile,IsMobile.mobile,IsMobile.java.lang.String,IsMobile]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loginVo.mobile,mobile]; arguments []; default message [mobile],true]; default message [手机号码格式错误]]
  * 全局异常处理
* 分布式session：将token写到cookie中，后续访问将携带这个token，服务端通过这个token在redis中获取用户信息，同时延长过期时间
  public String list(Model model, HttpServletResponse response,
                       @CookieValue(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String coolieToken,
                       @RequestParam(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String paramToken)
* 数据库设计
  * 商品表 订单表
  * 秒杀商品表（单独开出来一张表，不在商品表扩展，方便后期其他活动） 秒杀订单表
* 实际项目中价格不会是小数，存的都是分
* 商品详情页，秒杀页
* JMeter压测
* Redis压测工具redis-benchmark
  * redis-benchmark -h 127.0.0.1 -p 6379 -c 100 -n 100000 模拟100个并发发10万个请求
  * redis-benchmark -h 127.0.0.1 -p 63379 -q -d 100 模拟100个字节，默认是3个字节，返回的是1秒的并发量
  * redis-benchmark -t set, lpush -q -n 100000 只测试set和lpush，模拟一万个请求
  * redis-benchmark -n 100000 -q script load "redis.call('set', 'foo', 'bar')" 只测试redis的这个命令
* SpringBoot打war包
* 添加spring-boot-starter-tomcat的provided依赖
* 添加maven-war-plugin插件
* maven打包指定时间
* 页面优化技术
  * 页面缓存+URL缓存+对象缓存
      * 取缓存，手动渲染模板，输出结果
  * 页面静态化，前后端分离
  * 静态资源优化
  * CDN优化
* 超卖问题
  * 数据库级别解决：SQL加库存数量判断防止库存变成负数，数据库加唯一索引防止用户重复购买
  * 代码级别：判断是否已下单
  * 业务逻辑级别：加验证码
* 接口优化
  * Redis预减库存减少数据库访问
  * 内存标记减少Redids的访问
  * 请求先入队缓冲，异步下单，增强用户体验
  * RabbitMQ与SpringBoot集成
  * Nginx水平扩展
  * 扩展：阿里mycat分库分表原理
* 思路：减少数据库访问
  1. 系统初始化，把商品库存数量加载到Redis
  2. 收到请求，Redis预减库存，库存不足，直接返回，否则进入3
  3. 请求入队，立即返回派对中
  4. 请求出队，生成订单，减少库存
  5. 客户端轮询，判断是否秒杀成功
* SpringBoot集成MQ
  1. spring-boot-starter-amqp依赖
  2. 创建消息接收者
  3. 创建消息发送者
* RabbitMQ配置
  *  /etc/rabbitmq/rabbitmq.config
  [
  {rabbit, [{tcp_listeners, [5672]}, {loopback_users, ["guestuser", "guest"]}]}
  ].
  配置guest为management角色，注意是小写，guestuser为administrator角色，上面的配置可以远程通过web 15672端口访问管理界面
* Nginx横向扩展，配置tomcat集群
* LVS
* 安全优化
  * 秒杀接口地址隐藏
  * 数学公式验证码：防止机器人攻击，削峰（瞬间并发量分散到10秒甚至10几秒之内），分散用户请求
  * 接口限流防刷