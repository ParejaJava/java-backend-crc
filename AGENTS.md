# AGENTS.md

## 1. 项目定位

本项目是一个 **心肺耦合（CRC）生理信号解析平台后端系统**，面向医疗科研场景中的 ECG / 呼吸流等大体积生理波形文件，实现文件存储、异步计算调度、MATLAB 算法调用、计算状态推送与结果入库。

项目核心目标不是做一个普通 CRUD 系统，而是实现一条完整的后端计算链路：

```text
前端上传 GB 级波形文件
        ↓
MinIO 分片上传与文件管理
        ↓
MySQL 记录文件、任务、计算结果元数据
        ↓
Redis 做幂等控制、状态缓存、进度缓存
        ↓
RabbitMQ 投递异步计算任务
        ↓
Java Consumer 调用 MATLAB 算法
        ↓
结果解析与入库
        ↓
WebSocket 推送计算进度和报告状态
```

Codex 在开发时必须围绕这条主链路实现功能，不要把项目写成简单的文件上传 Demo 或普通后台管理系统。

---

## 2. 技术栈约束

### 后端主技术栈

- Java 17+
- Spring Boot 3.x
- Maven
- MyBatis-Plus
- MySQL 8.x
- Redis
- RabbitMQ
- MinIO
- WebSocket
- MATLAB Runtime / 本地 MATLAB 脚本调用
- Lombok
- Hutool / Apache Commons 可按需使用

### 推荐开发依赖

优先使用稳定、面试友好、生产常见的依赖，不要引入过于冷门或复杂的框架。

推荐依赖包括：

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-amqp`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-websocket`
- `mysql-connector-j`
- `mybatis-plus-spring-boot3-starter`
- `minio`
- `lombok`

---

## 3. 代码结构规范

请按如下结构组织代码：

```text
src/main/java/com/example/crc
├── CrcApplication.java
├── common
│   ├── api
│   │   ├── Result.java
│   │   └── ResultCode.java
│   ├── exception
│   │   ├── GlobalExceptionHandler.java
│   │   └── BizException.java
│   └── constant
├── config
│   ├── MinioConfig.java
│   ├── RedisConfig.java
│   ├── RabbitMqConfig.java
│   ├── WebSocketConfig.java
│   └── MyBatisPlusConfig.java
├── controller
│   ├── FileController.java
│   ├── TaskController.java
│   └── WebSocketController.java
├── service
│   ├── FileService.java
│   ├── TaskService.java
│   ├── MatlabComputeService.java
│   └── WebSocketPushService.java
├── service.impl
│   ├── FileServiceImpl.java
│   ├── TaskServiceImpl.java
│   ├── MatlabComputeServiceImpl.java
│   └── WebSocketPushServiceImpl.java
├── mq
│   ├── producer
│   │   └── ComputeTaskProducer.java
│   ├── consumer
│   │   └── ComputeTaskConsumer.java
│   ├── message
│   │   └── ComputeTaskMessage.java
│   └── retry
├── mapper
│   ├── WaveFileMapper.java
│   ├── ComputeTaskMapper.java
│   └── ComputeResultMapper.java
├── entity
│   ├── WaveFile.java
│   ├── ComputeTask.java
│   └── ComputeResult.java
├── dto
│   ├── upload
│   ├── task
│   └── websocket
├── vo
│   ├── FileUploadVO.java
│   ├── TaskStatusVO.java
│   └── ComputeResultVO.java
└── util
    ├── FileHashUtil.java
    ├── RedisLockUtil.java
    ├── MatlabCommandUtil.java
    └── JsonUtil.java
```

如果当前项目包名不是 `com.example.crc`，请使用项目已有包名，不要强行改包名。

---

## 4. 核心业务模块

### 4.1 MinIO 文件存储模块

目标：支持 GB 级 ECG / RSP 波形文件上传，强调断点续传、分片上传、MD5 校验和元数据落库。

必须实现的能力：

1. 初始化分片上传任务。
2. 上传单个分片。
3. 查询已上传分片。
4. 合并分片。
5. 计算或接收文件 MD5。
6. 将文件元信息写入 MySQL。
7. 上传完成后生成后续计算任务。

推荐接口：

```text
POST /api/files/init-multipart
POST /api/files/upload-part
GET  /api/files/uploaded-parts
POST /api/files/complete-multipart
GET  /api/files/{fileId}
```

文件表建议字段：

```text
id
user_id
original_filename
object_name
bucket_name
file_size
file_md5
file_type
upload_status
created_at
updated_at
```

注意事项：

- 不要直接把大文件存到本地磁盘。
- 不要把大文件以 byte[] 全量读入内存。
- 分片上传接口要考虑重复上传同一分片。
- 合并前应检查分片完整性。
- MinIO objectName 建议使用：
  `userId/yyyyMMdd/fileMd5/originalFilename`

---

### 4.2 Redis 幂等与状态缓存模块

Redis 在本项目中主要承担两个角色：

#### 角色一：上传幂等控制

使用 `SETNX` 设计用户级文件锁，避免同一用户重复上传同一文件导致重复任务。

推荐 key：

```text
crc:upload:lock:{userId}:{fileMd5}
```

推荐 value：

```text
requestId 或 UUID
```

推荐 TTL：

```text
10 到 30 分钟
```

要求：

- 加锁必须使用 `SET key value NX EX seconds` 的原子语义。
- 删除锁时必须校验 value，避免误删其他请求的锁。
- 不要只依赖 Java synchronized，因为多实例部署下无效。
- 不要只依赖数据库唯一索引，因为上传链路中存在大量中间状态。

#### 角色二：任务状态与进度缓存

推荐 key：

```text
crc:task:status:{taskId}
crc:task:progress:{taskId}
crc:task:ws:user:{userId}
```

用途：

- 缓存计算状态。
- 支持前端快速查询任务进度。
- 支持 WebSocket 断线重连后恢复状态。
- 减少频繁查询 MySQL。

任务状态枚举建议：

```text
CREATED
QUEUED
RUNNING
SUCCESS
FAILED
RETRYING
DEAD_LETTERED
```

---

### 4.3 RabbitMQ 异步计算模块

RabbitMQ 用于承接耗时 MATLAB 计算任务，实现削峰填谷、异步解耦和失败重试。

#### 上游

RabbitMQ 的上游是 Java 后端业务服务，通常是：

```text
TaskServiceImpl
```

当文件上传完成、任务记录创建成功后，由 Java 后端把计算任务消息发送到 RabbitMQ。

#### 下游

RabbitMQ 的直接消费者也是 Java 后端，不是 MATLAB。

具体链路是：

```text
RabbitMQ
  ↓
Java Consumer
  ↓
ProcessBuilder
  ↓
MATLAB 脚本 / MATLAB Runtime
```

因此：

- 消费者 ACK 的真正执行者是 Java Consumer。
- MATLAB 只是被 Java 调用的外部计算进程。
- Java Consumer 负责判断 MATLAB 是否执行成功。
- 只有当 MATLAB 计算成功、结果解析成功、MySQL 状态更新成功后，才允许 ACK。

#### 推荐 MQ 拓扑

```text
Exchange:
crc.compute.exchange

Queue:
crc.compute.queue
crc.compute.retry.queue
crc.compute.dlq

Routing Key:
crc.compute.submit
crc.compute.retry
crc.compute.dead
```

#### 推荐消息体

```json
{
  "taskId": 10001,
  "userId": 1,
  "fileId": 20001,
  "objectName": "1/20251101/xxx/ecg.csv",
  "bucketName": "crc-waveform",
  "algorithmType": "CRC_INDEX",
  "retryCount": 0,
  "createdAt": "2025-11-01T10:00:00"
}
```

#### ACK 规则

必须使用手动 ACK：

- 成功：`basicAck`
- 可重试失败：根据 retryCount 投递到 retry queue，然后 ack 原消息
- 不可重试失败：投递到死信队列或拒绝进入 DLQ
- Java 进程异常退出：不 ACK，让 RabbitMQ 重新投递

#### 什么情况下进入死信队列

以下情况应进入死信队列：

1. MATLAB 进程多次执行失败。
2. MATLAB 执行超时。
3. 输入文件不存在或 MinIO 下载失败。
4. 输出结果格式错误，无法解析。
5. MySQL 状态更新多次失败。
6. 消息体字段缺失，无法恢复。
7. 超过最大重试次数。

建议最大重试次数：

```text
3
```

#### 消费端并发控制

不要无限制启动 MATLAB。

建议：

- RabbitMQ listener concurrency 设置为 1 到 3。
- 使用 prefetch 控制单个消费者未确认消息数量。
- 使用 Java 线程池或信号量限制 MATLAB 进程并发数。
- 开发环境默认只允许 1 个 MATLAB 进程同时运行。
- 生产环境根据 CPU 核数、内存、MATLAB Runtime 占用评估。

示例原则：

```text
MATLAB 计算很重，宁可队列堆积，也不要把服务器打爆。
```

---

### 4.4 ProcessBuilder 调用 MATLAB 模块

Java 后端通过 `ProcessBuilder` 调用 MATLAB 计算脚本。

推荐职责划分：

```text
MatlabComputeService
  ├── 从 MinIO 获取输入文件
  ├── 生成 MATLAB 参数
  ├── 启动 MATLAB 进程
  ├── 捕获 stdout / stderr
  ├── 处理超时
  ├── 读取输出文件
  ├── 解析 CRC 指标和情绪标签
  └── 返回结构化结果
```

推荐方法：

```java
MatlabComputeResult runCompute(ComputeTaskMessage message);
```

ProcessBuilder 要求：

- 不要拼接危险 shell 字符串。
- 优先使用参数列表形式构造命令。
- 必须设置超时时间。
- 必须捕获标准输出和错误输出。
- 必须记录 exitCode。
- 必须为每个 taskId 创建独立工作目录。
- 计算完成后清理临时文件。
- 失败时保留必要日志路径，便于排查。

示例命令思路：

```text
matlab -batch "run_crc_compute('inputPath','outputPath','taskId')"
```

或 MATLAB Runtime：

```text
run_crc_compute.exe inputPath outputPath taskId
```

不要在代码中硬编码本机绝对路径。路径应放在配置文件中。

---

### 4.5 MySQL 数据模型模块

至少需要三张核心表。

#### 文件表：wave_file

记录上传文件元数据。

#### 任务表：compute_task

记录每次计算任务。

建议字段：

```text
id
user_id
file_id
task_type
task_status
mq_message_id
retry_count
error_message
started_at
finished_at
created_at
updated_at
```

#### 结果表：compute_result

记录 MATLAB 输出结果。

建议字段：

```text
id
task_id
user_id
file_id
crc_index
emotion_label
result_json
report_object_name
created_at
updated_at
```

重要规则：

- taskId 必须在消息发送到 RabbitMQ 之前生成。
- taskId 来源于 MySQL 的 compute_task 主键。
- RabbitMQ 消息只携带 taskId 等必要信息。
- 消费者必须基于 taskId 做幂等判断。
- 结果表对 task_id 建议加唯一索引，避免重复入库。

---

### 4.6 消费端幂等设计

消费者可能因为 RabbitMQ 重投递、Java 重启、ACK 失败等原因重复消费同一条消息，因此必须保证消费幂等。

推荐策略：

#### 第一层：任务状态判断

消费前查询 compute_task：

```text
如果 task_status = SUCCESS，直接 ACK，不重复计算。
如果 task_status = RUNNING 且更新时间很近，说明其他消费者正在处理，可拒绝或稍后重试。
如果 task_status = FAILED / CREATED / QUEUED / RETRYING，可以继续处理。
```

#### 第二层：Redis 任务锁

推荐 key：

```text
crc:task:lock:{taskId}
```

只有拿到锁的消费者才能执行 MATLAB。

#### 第三层：数据库唯一约束

结果表添加唯一索引：

```text
unique(task_id)
```

避免重复插入结果。

#### 第四层：状态流转约束

状态更新要符合顺序：

```text
CREATED → QUEUED → RUNNING → SUCCESS
CREATED → QUEUED → RUNNING → FAILED
RUNNING → RETRYING → QUEUED
FAILED → DEAD_LETTERED
```

不要允许 SUCCESS 被 FAILED 覆盖。

---

### 4.7 WebSocket 实时推送模块

WebSocket 用于向前端主动推送任务状态、计算进度和报告生成结果，替代前端高频轮询。

推荐连接路径：

```text
/ws/task/{userId}
```

推荐推送消息：

```json
{
  "type": "TASK_PROGRESS",
  "taskId": 10001,
  "status": "RUNNING",
  "progress": 60,
  "message": "MATLAB computing CRC index..."
}
```

推荐消息类型：

```text
TASK_CREATED
TASK_QUEUED
TASK_RUNNING
TASK_PROGRESS
TASK_SUCCESS
TASK_FAILED
REPORT_READY
```

要求：

- 一个 userId 可以对应多个 WebSocket session。
- 用户断开后要清理 session。
- 推送失败不能影响主计算流程。
- 任务状态必须先落库或写 Redis，再推送。
- 前端重连后可以通过 REST 接口查询最新状态。

---

## 5. 接口设计建议

### 文件上传

```text
POST /api/files/init-multipart
POST /api/files/upload-part
POST /api/files/complete-multipart
GET  /api/files/{fileId}
```

### 任务计算

```text
POST /api/tasks
GET  /api/tasks/{taskId}
GET  /api/tasks/{taskId}/result
POST /api/tasks/{taskId}/retry
```

### 系统健康检查

```text
GET /api/health
GET /api/health/mysql
GET /api/health/redis
GET /api/health/rabbitmq
GET /api/health/minio
```

健康检查接口用于后续扩展运维 Agent，不要删除。

---

## 6. 配置文件规范

使用 `application.yml`，不要把密码、AK/SK、数据库账号硬编码到 Java 代码里。

推荐结构：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crc_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: 1
        concurrency: 1
        max-concurrency: 2

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: crc-waveform

matlab:
  mode: local
  executable: matlab
  script-dir: ./matlab
  workspace-dir: ./workspace/matlab
  timeout-seconds: 1800

crc:
  upload:
    lock-ttl-seconds: 1800
  task:
    lock-ttl-seconds: 3600
    max-retry-count: 3
```

---

## 7. 开发顺序

Codex 应按以下顺序开发，不要一上来就写复杂 Agent 或前端页面。

### 第一阶段：项目基础骨架

1. 创建统一返回体 `Result<T>`。
2. 创建全局异常处理。
3. 配置 MyBatis-Plus。
4. 创建 MySQL 表结构 SQL。
5. 实现基础健康检查接口。

### 第二阶段：文件上传与 MinIO

1. 接入 MinIO。
2. 实现普通小文件上传，用于验证链路。
3. 扩展为分片上传。
4. 实现分片合并。
5. 文件元数据写入 MySQL。
6. Redis 防重复上传。

### 第三阶段：任务系统

1. 创建 compute_task 表。
2. 文件上传完成后创建任务。
3. 任务状态查询接口。
4. 任务状态流转逻辑。

### 第四阶段：RabbitMQ 异步计算

1. 配置 exchange / queue / routingKey。
2. 实现生产者。
3. 实现消费者。
4. 开启手动 ACK。
5. 实现失败重试。
6. 实现死信队列。

### 第五阶段：MATLAB 调用

1. 先用 mock 脚本模拟 MATLAB 输出。
2. 使用 ProcessBuilder 调用 mock 脚本。
3. 捕获输出与 exitCode。
4. 接入真实 MATLAB 脚本。
5. 解析结果并写入 compute_result。

### 第六阶段：WebSocket 推送

1. 建立 WebSocket 连接管理。
2. 任务状态变化时推送。
3. 支持多个 session。
4. 支持断线后 REST 查询恢复状态。

### 第七阶段：面试展示增强

1. 补充 README。
2. 补充接口文档。
3. 补充架构图。
4. 补充典型异常场景说明。
5. 补充压测或模拟并发说明。

---

## 8. 编码要求

### 通用要求

- 每个类职责单一。
- Controller 不写业务细节。
- Service 负责业务编排。
- MQ Consumer 只负责消费控制，不堆积复杂业务。
- MATLAB 调用逻辑集中在 `MatlabComputeService`。
- Redis key 统一放在常量类或工具类中。
- 所有外部系统调用都必须有异常处理。
- 关键状态变更必须写日志。
- 所有入参必须做基础校验。

### 日志要求

关键日志必须包含：

```text
userId
fileId
taskId
messageId
objectName
status
retryCount
elapsedTime
```

建议日志示例：

```text
[CRC_TASK_START] taskId=10001, fileId=20001, userId=1
[CRC_MATLAB_EXIT] taskId=10001, exitCode=0, elapsed=126000ms
[CRC_TASK_SUCCESS] taskId=10001, resultId=30001
[CRC_TASK_FAILED] taskId=10001, reason=MATLAB_TIMEOUT
```

---

## 9. 不要做的事情

Codex 开发时请避免以下问题：

1. 不要把 MATLAB 当成 RabbitMQ 消费者。
2. 不要在 Controller 里直接调用 ProcessBuilder。
3. 不要自动 ACK RabbitMQ 消息。
4. 不要在 MATLAB 未成功时 ACK。
5. 不要把大文件完整读入内存。
6. 不要把 Redis 锁写成普通 set 后再 expire 的非原子操作。
7. 不要让多个消费者无限制启动 MATLAB。
8. 不要让 WebSocket 推送失败影响计算任务。
9. 不要只写 Demo，要保留真实工程链路。
10. 不要引入复杂微服务架构，本项目先保持单体 Spring Boot 应用即可。
11. 不要在代码中硬编码数据库密码、MinIO 密钥和 MATLAB 路径。
12. 不要省略任务状态机。
13. 不要省略消费者幂等逻辑。
14. 不要省略死信队列和重试策略。
15. 不要把异常简单吞掉，必须记录日志并更新任务状态。

---

## 10. Codex 每次开发前的检查清单

每次生成代码或修改代码前，先检查：

1. 当前修改属于哪个模块？
2. 是否影响主链路？
3. 是否需要新增数据库表或字段？
4. 是否需要更新 `application.yml`？
5. 是否需要补充异常处理？
6. 是否需要补充日志？
7. 是否需要考虑幂等？
8. 是否需要考虑 RabbitMQ ACK？
9. 是否需要考虑 Redis TTL？
10. 是否需要更新 README 或接口说明？

---

## 11. 推荐提交粒度

Git commit 建议按功能模块提交：

```text
feat: init spring boot backend skeleton
feat: add minio multipart upload support
feat: add redis upload idempotency lock
feat: add compute task domain model
feat: add rabbitmq async compute pipeline
feat: add matlab process builder integration
feat: add websocket task progress push
feat: add task retry and dead letter handling
docs: add architecture and api usage
```

不要一次性提交巨大改动。

---

## 12. 当前项目优先级

当前阶段优先实现真实后端工程能力，优先级如下：

```text
1. Spring Boot 基础骨架
2. MySQL 表结构与 MyBatis-Plus
3. MinIO 文件上传
4. Redis 幂等锁
5. RabbitMQ 异步任务
6. ProcessBuilder 调用 MATLAB mock
7. 真实 MATLAB 调用
8. WebSocket 状态推送
9. 运维健康检查接口
10. 后续 Agent 工具调用
```

现阶段不要优先实现复杂多 Agent。后续如果要做运维 Agent，应基于已经实现的健康检查接口和真实后端状态查询能力扩展。

---

## 13. 面试讲述口径

本项目的亮点应围绕下面五点展开：

1. **GB 级生理波形文件处理**  
   使用 MinIO 分片上传、断点续传和 MD5 校验，解决大文件上传失败率高和重复上传问题。

2. **异步计算调度**  
   使用 RabbitMQ 将耗时 MATLAB 计算从 HTTP 请求链路中解耦，避免请求阻塞，并通过队列实现削峰填谷。

3. **跨进程算法调用**  
   使用 Java ProcessBuilder 调用 MATLAB 算法脚本，实现 Java Web 服务与科研算法之间的工程化集成。

4. **幂等与可靠性设计**  
   使用 Redis SETNX、防重复任务、消费者手动 ACK、死信队列和数据库唯一约束，降低重复计算和结果重复入库风险。

5. **实时状态反馈**  
   使用 WebSocket 主动推送计算进度和报告生成结果，避免前端频繁轮询，提高用户体验。

---

## 14. 给 Codex 的最终提醒

这是一个用于展示后端工程能力的科研计算平台，不是单纯的算法项目。

写代码时请始终围绕这几个关键词：

```text
大文件上传
异步任务
可靠消息
跨进程计算
幂等控制
状态流转
实时推送
可观测性
```

任何新增功能都必须服务于这条主线。
