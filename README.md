# 分布式ID

> 基于数据库的分布式取号器.

基于[https://github.com/Meituan-Dianping/Leaf](https://github.com/Meituan-Dianping/Leaf) 二次开发.

# 配置项

| 字段 | 说明 | 默认值 |
| ---- | ---- | ---- | 
|id.server.enable-auth | 是否开启鉴权 | false|
|id.server.init-get | 启动后获取一次，提前缓存 | false|

# 使用说明

## 1、初始化DB

执行`seezoon-distributed-id-server/build/sql/seezoon-distributed-id.sql`

关键表字段说明：

| 字段 | 说明 |
| ---- | ---- | 
|biz_tag | 业务标识，一个分布式取号器可以满足多个业务场景，根据该字段区分|
|token | 该业务标识认证码，如果server开器认证，则在请求时候需要携带|
|max_id | 当前值|
|step | 步长，每次缓存多少，缓存越大性能越高，在服务重启时候丢失的号段越大，按需设置|

## 2、程序安装

```shell
git clone https://github.com/seezoon/seezoon-distributed-id.git
mvn clean package
```

产出物目录`seezoon-distributed-id/target/seezoon-distributed-id-server`

添加配置对应环境application.yml配置到产出物conf目录

```yaml
# 环境标识，开发环境有意义，测试及生产无意义,作为打印标识可以自己定义如:test,prod,uat
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/seezoon-distributed-id?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&connectTimeout=1000&socketTimeout=60000&rewriteBatchedStatements=true
    username: root
    password:
```

## 3、验证

添加初始数据

```sql
INSERT INTO `seezoon-distributed-id`.`distributed_id` (`biz_tag`, `token`, `max_id`, `step`,
                                                       `description`, `create_time`, `update_time`)
VALUES ('test', '', 10000, 1, NULL, now(), now());
```

- grpc 工具测试： 使用方法参考https://github.com/fullstorydev/grpcurl

```shell
grpcurl -plaintext -d '{"bizTag":"test"}' localhost:9000 com.seezoon.DistributedId/Get 
```

- http 验证

```shell
curl http://127.0.0.1:8080/get/test
```

## 4、镜像制作

```shell
./seezoon-distributed-id/build.sh
```




