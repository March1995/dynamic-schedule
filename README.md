# dynamic-schedule

## 概述

基于Spring Boot的动态定时任务管理系统，支持动态创建、修改、删除定时任务，查询任务执行记录，以及分布式部署环境下的任务调度。

### 后端架构

#### 设计工具和要求

- Java版本: JDK 21
- 定时任务框架: Quartz
- 依赖管理工具: Maven (尽可能使用BOM方式导入依赖)
- 核心框架: Spring Boot 3.5.5
- 数据库: H2 (默认，支持MySQL等关系型数据库)

#### 实现功能

- [x] 支持动态创建/修改/删除Spring定时任务
- [x] 支持查询定时任务执行记录
- [x] 支持立即执行特定任务
- [x] 支持多实例部署，确保任务只在一台服务器上执行
- [x] 设计相应的数据库表结构，并给出SQL脚本
- [x] 提供Web管理页面和RESTful API接口
- [ ] 负载均衡：系统可根据窗口忙闲状态智能分配客户到最优(此功能未实现)

### 项目结构

```
dynamic-schedule/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/dynamicschedule/
│   │   │       ├── DynamicScheduleApplication.java  # Spring Boot启动类
│   │   │       ├── config/                          # 配置类
│   │   │       ├── controller/                      # REST API控制器
│   │   │       ├── entity/                          # 实体类
│   │   │       ├── jobhandler/                      # 任务处理器示例
│   │   │       ├── quartz/                          # Quartz相关类
│   │   │       ├── repository/                      # 数据访问层
│   │   │       ├── scheduler/                       # 调度器相关
│   │   │       └── service/                         # 业务逻辑层
│   │   └── resources/
│   │       ├── static/                              # 静态资源(前端页面)
│   │       ├── application.yml                      # 应用配置文件
│   │       ├── data.sql                             # 初始数据脚本
│   │       ├── schema.sql                           # 数据库表结构
│   │       └── quartz_tables.sql                    # Quartz表结构
├── pom.xml                                          # Maven配置文件
└── README.md
```

### 数据库设计

系统使用以下数据表:

1. `scheduled_jobs` - 存储定时任务信息
2. `job_execution_logs` - 存储任务执行日志
3. `server_nodes` - 存储服务节点信息(用于分布式锁)
4. `QRTZ_*` 系列表 - Quartz框架所需表

### 快速开始

1. 确保已安装JDK 21和Maven

2. 克隆项目到本地
   ```
   git clone <项目地址>
   ```

3. 进入项目目录并编译
   ```
   cd dynamic-schedule
   mvn clean package
   ```

4. 运行应用
   ```
   mvn spring-boot:run
   ```
   或
   ```
   java -jar target/dynamic-schedule-1.0.0.jar
   ```

5. 访问应用
   - Web管理页面: http://localhost:8080
   - H2数据库控制台: http://localhost:8080/h2-console
   - REST API接口: http://localhost:8080/api/

### REST API 接口说明

#### 定时任务管理接口

- `POST /api/jobs` - 创建定时任务
- `PUT /api/jobs/{id}` - 更新定时任务
- `DELETE /api/jobs/{id}` - 删除定时任务
- `GET /api/jobs` - 获取所有任务
- `GET /api/jobs/{id}` - 根据ID获取任务
- `GET /api/jobs/page` - 分页获取任务
- `POST /api/jobs/{id}/pause` - 暂停任务
- `POST /api/jobs/{id}/resume` - 恢复任务
- `POST /api/jobs/{id}/run` - 立即执行任务

#### 任务执行日志接口

- `GET /api/job-logs` - 分页获取执行日志
- `GET /api/job-logs/job/{jobName}` - 根据任务名称获取执行日志

### 分布式部署说明

系统支持多实例部署，通过以下机制确保同一任务不会在多个节点同时执行:

1. 使用数据库锁机制选举master节点
2. 只有master节点才会真正执行定时任务
3. 当master节点失效时，其他节点会自动选举新的master

部署步骤:
1. 修改[application.yml](src/main/resources/application.yml)配置文件，使用实际的数据库
2. 在每个节点上部署应用
3. 系统会自动处理节点间的协调工作

### 自定义任务处理器

要创建自定义任务处理器:

1. 创建一个新的Spring Bean类
2. 添加@Component注解并指定bean名称
3. 编写需要执行的方法
4. 在管理页面或通过API创建任务时，指定对应的bean名称和方法名

示例:
```java
@Component("myJobHandler")
public class MyJobHandler {
    public void execute() {
        // 任务逻辑
    }
}
```