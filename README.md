# 教学事务管理系统

基于 **RuoYi-Vue3** 二次开发的 B/S 教务管理系统，覆盖管理员 / 教师 / 学生三种角色的差异化业务。
后端 Spring Boot + MyBatis，前端 Vue 3，数据库 MySQL 8。

> 上海大学《数据库基础与应用》课程项目（2025.11–2025.12）。
> **本项目由本人完成的重点在数据库与数据层**：表结构设计、触发器、存储过程、统计查询；
> 系统基础框架与通用增删改查页面基于 RuoYi-Vue3 脚手架搭建。

## 功能

| 角色 | 功能 |
|---|---|
| 管理员 | 院系 / 教师 / 学生 / 课程 / 教学班管理；统计数据（教师数、学生数、课程数、各院系师生分布、教师职称分布） |
| 教师 | 查看本人教学班与学生名单、录入成绩 |
| 学生 | 选课 / 退课、查看课表与成绩、GPA 查询 |

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java / Spring Boot / MyBatis / Druid，端口 8080 |
| 前端 | Vue 3 / Element Plus / Vite |
| 数据库 | MySQL 8，库名 `ry_vue` |

## 目录结构

```
.
├── backend/                        # RuoYi-Vue 后端（Spring Boot）
│   └── ruoyi-admin/src/main/java/com/ruoyi/
│       ├── student/                # 学生管理
│       ├── teacher/                # 教师管理
│       ├── department/             # 院系管理
│       ├── course/                 # 课程管理
│       ├── courseclass/            # 教学班 / 排课
│       └── courseselection/        # 选课
├── frontend/                       # RuoYi-Vue3 前端（Vue 3 + Vite）
│   └── src/views/
│       ├── student/ teacher/ course/ courseclass/ courseselection/ department/
│       └── dashboard/              # Admin / Teacher / Student 三个角色首页
└── sql/
    └── student_info.sql            # ★ 建表 + 触发器 + 存储过程 + 演示账号，可一次性导入
```

## 数据库设计要点

7 张业务表：`department`、`teacher`、`student`、`course`、`responsibility`、`course_class`、`course_selection`。

业务约束不只写在应用层，而是尽量下沉到数据库，保证无论从哪个入口写入口径都一致：

| 对象 | 作用 |
|---|---|
| `trg_course_class_time_conflict` | 教学班上课时间冲突检测 |
| `trg_course_class_room_insert_conflict` / `_update_conflict` | 教室占用冲突检测 |
| `trg_check_dept_consistency_insert` | 跨表院系一致性校验 |
| `trg_course_selection_insert_check` / `_update_check` | 选课记录插入 / 更新时的业务校验 |
| `trg_courseselection_insert` | 重复选课、同学期上课时间冲突拦截 |
| `trg_courseselection_delete` | 退课后同步已选人数 |
| `trg_calc_total_score_insert` / `_update` | 按平时 / 期末占比自动计算总评 |
| `trg_auto_calc_gpa` + `sp_update_student_gpa` | 成绩变更后自动换算绩点 |
| `trg_course_info_lock` | 已有选课记录的课程禁止修改关键字段 |

管理员首页的多维统计用条件聚合一次查出，避免多次往返查询。

## 快速开始

1. 创建数据库

   ```sql
   CREATE DATABASE ry_vue DEFAULT CHARACTER SET utf8mb4;
   ```

2. 导入 `sql/student_info.sql`（含建表、触发器、存储过程与演示账号）

3. 配置数据库密码：在 `backend/ruoyi-admin/src/main/resources/application-druid.yml` 中
   已改为 `${DB_PASSWORD:}`，启动前设置环境变量：

   ```powershell
   $env:DB_PASSWORD = "你的MySQL密码"
   ```

4. 启动后端

   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. 启动前端

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

默认管理员账号见 RuoYi 脚手架文档。

## 已知问题

- **选课人数同步**：`trg_courseselection_insert` 由 `AFTER INSERT` 改为 `BEFORE INSERT` 做冲突校验后，
  原先人数自增的逻辑随之失效，需补一个 `AFTER INSERT` 触发器或在应用层处理。
- 排课冲突目前完全依赖触发器，高并发场景下建议在应用层加乐观锁或唯一索引兜底。
- 仓库不含真实学生数据，`student_info.sql` 中的账号仅为 RuoYi 脚手架演示数据。
