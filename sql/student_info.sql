-- 建表
CREATE TABLE department
	(dept_id INT NOT NULL COMMENT'院系代号',
    dept_name VARCHAR(20) NOT NULL COMMENT'院系名',
    dept_address VARCHAR(40) DEFAULT NULL COMMENT'地址',
    dept_phone CHAR(8) DEFAULT NULL COMMENT'联系电话',
    PRIMARY KEY(dept_id)) COMMENT'院系设置'; 
    
CREATE TABLE teacher
	(staff_id CHAR(8) NOT NULL COMMENT'教师工号',
    teacher_name VARCHAR(30) NOT NULL COMMENT'教师姓名',
    sex ENUM('男', '女', '未知') COMMENT'性别',
	date_of_birth DATE DEFAULT NULL COMMENT'出生日期',
    professional_rank VARCHAR(10) DEFAULT NULL COMMENT'职称',
    dept_id INT DEFAULT NULL COMMENT'院系号',
    PRIMARY KEY(staff_id),
    FOREIGN KEY(dept_id) REFERENCES department(dept_id)) COMMENT'教师信息';

CREATE TABLE student(
	student_id CHAR(8) NOT NULL COMMENT'学号',
    student_name VARCHAR(30) NOT NULL COMMENT'姓名',
    sex ENUM('男', '女', '未知') COMMENT'性别',
	date_of_birth DATE DEFAULT NULL COMMENT'出生日期',
    mobile_phone CHAR(11) DEFAULT NULL COMMENT'手机号',
    dept_id INT DEFAULT NULL COMMENT'院系号',
    PRIMARY KEY(student_id),
    FOREIGN KEY(dept_id) REFERENCES department(dept_id)) COMMENT'学生表';

CREATE TABLE course
	(course_id CHAR(8) NOT NULL COMMENT'课程号',
    course_name VARCHAR(40) COMMENT'课程名',
    credit INT DEFAULT 4 COMMENT'学分',
    study_hours INT DEFAULT 40 COMMENT'学时',
    dept_id INT COMMENT'开课院系号',
    PRIMARY KEY(course_id),
    CONSTRAINT chk_credit CHECK (credit>0),
    CONSTRAINT chk_credit CHECK (study_hours>0),
    FOREIGN KEY(dept_id) REFERENCES department(dept_id)) COMMENT'课程信息';
-- 2025.12.27 course表新增成绩占比
ALTER TABLE course
ADD COLUMN usual_weight DECIMAL(2, 1) DEFAULT 0.5 COMMENT '平时成绩占比',
ADD COLUMN final_weight DECIMAL(2, 1) DEFAULT 0.5 COMMENT '期末成绩占比',
ADD CONSTRAINT chk_usual_range CHECK (usual_weight >= 0 AND usual_weight <= 1),
ADD CONSTRAINT chk_final_range CHECK (final_weight >= 0 AND final_weight <= 1),
ADD CONSTRAINT chk_total_weight CHECK ((usual_weight + final_weight) = 1.0);
    
CREATE TABLE responsibility
	(staff_id CHAR(8) NOT NULL COMMENT'教师工号',
    course_id CHAR(8) NOT NULL COMMENT'课程号',
    PRIMARY KEY(staff_id,course_id),
    FOREIGN KEY(staff_id) REFERENCES teacher(staff_id) ON UPDATE RESTRICT,
    FOREIGN KEY(course_id) REFERENCES course(course_id) ON UPDATE RESTRICT) COMMENT'教师任课情况';
    
CREATE TABLE course_class (
    class_id CHAR(8) NOT NULL COMMENT'教学班号',
    course_id CHAR(8) NOT NULL COMMENT'课程号',
    staff_id CHAR(8) NOT NULL COMMENT'教师工号',
    semester VARCHAR(30) NOT NULL COMMENT'学期',
    classroom VARCHAR(40) COMMENT'上课地点',         
    capacity INT DEFAULT 50 COMMENT'容量',
    selected_num INT DEFAULT 0 COMMENT '已选人数',
    day_of_week INT NOT NULL COMMENT '上课星期',  
    start_period INT NOT NULL COMMENT '起始节次', 
    end_period INT NOT NULL COMMENT '结束节次',   
    PRIMARY KEY (class_id),
    UNIQUE INDEX uk_class_course_staff (class_id, course_id, staff_id),
    CONSTRAINT chk_period_order CHECK (end_period >= start_period),
    CONSTRAINT chk_day_range CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_start_range CHECK (start_period BETWEEN 1 AND 12),
    CONSTRAINT chk_end_range CHECK (end_period BETWEEN 1 AND 12),
    CONSTRAINT chk_capacity CHECK (capacity >= selected_num),
    FOREIGN KEY (staff_id, course_id) REFERENCES responsibility(staff_id, course_id) ON UPDATE RESTRICT) COMMENT'开课情况';
    
CREATE TABLE course_selection (
    student_id CHAR(8) NOT NULL COMMENT '学号',
    semester VARCHAR(30) NOT NULL COMMENT '学期',
    course_id CHAR(8) NOT NULL COMMENT '课程号',
    staff_id CHAR(8) NOT NULL COMMENT '教师工号',
    class_id CHAR(8) NOT NULL COMMENT '教学班号',
    normal_score INT CHECK (normal_score BETWEEN 0 AND 100),
    test_score INT CHECK (test_score BETWEEN 0 AND 100),
    total_score INT CHECK (total_score BETWEEN 0 AND 100),
    PRIMARY KEY (student_id, class_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id),
	FOREIGN KEY (class_id, course_id, staff_id) REFERENCES course_class (class_id, course_id, staff_id) ON UPDATE RESTRICT)COMMENT'选课记录';
-- 2025.12.29 在course_selection表中增加“绩点”字段
ALTER TABLE course_selection 
ADD COLUMN gpa DECIMAL(3, 1) COMMENT '绩点';

-- 2025.12.29 将三个学院加入部门管理中
UPDATE sys_dept 
SET dept_name = '大学', 
    create_by = 'admin', 
    update_time = NOW() 
WHERE dept_id = 100;

DELETE FROM sys_dept WHERE parent_id = 100;
UPDATE sys_user SET dept_id = 100 WHERE user_id = 1;

INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, status, del_flag, create_by, create_time)
VALUES 
(1, 100, '0,100', '计算机学院', 1, '负责人', '0', '0', 'admin', NOW()),
(2, 100, '0,100', '通信学院', 2, '负责人', '0', '0', 'admin', NOW()),
(3, 100, '0,100', '材料学院', 3, '负责人', '0', '0', 'admin', NOW());

-- 2025.12.29 将教师和学生导入用户
-- 教师：
-- 1. 迁移教师到系统用户表 (sys_user)
INSERT INTO sys_user (user_name, nick_name, password, dept_id, status, del_flag, create_by, create_time)
SELECT 
    t.staff_id,        -- 教师工号作为登录账号
    t.teacher_name,    -- 教师姓名
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dOni56nzB737.33nmG', -- 默认密码 123456
    t.dept_id,         -- 关联你刚创建的 1, 2, 3 学院ID
    '0', '0', 'admin', NOW()
FROM teacher t
WHERE t.staff_id NOT IN (SELECT user_name FROM sys_user);
-- 2. 关联教师角色 (101)
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 101 
FROM sys_user u
INNER JOIN teacher t ON u.user_name = t.staff_id
WHERE u.user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 101);

-- 学生：
-- 1. 迁移学生到系统用户表 (sys_user)
INSERT INTO sys_user (user_name, nick_name, password, dept_id, status, del_flag, create_by, create_time)
SELECT 
    s.student_id,      -- 学号作为登录账号
    s.student_name,    -- 学生姓名
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dOni56nzB737.33nmG', -- 默认密码 123456
    s.dept_id,         -- 关联对应的学院ID
    '0', '0', 'admin', NOW()
FROM student s
WHERE s.student_id NOT IN (SELECT user_name FROM sys_user);
-- 2. 关联学生角色 (102)
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, 102 
FROM sys_user u
INNER JOIN student s ON u.user_name = s.student_id
WHERE u.user_id NOT IN (SELECT user_id FROM sys_user_role WHERE role_id = 102);

-- 2025.12.30 course表新增触发器：在已有教师负责的情况下不可以更改课程名或者所属院系
DELIMITER //

CREATE TRIGGER trg_course_info_lock
BEFORE UPDATE ON course
FOR EACH ROW
BEGIN
    -- 只有当 课程名 或 院系号 发生变动时才进行检查
    IF (OLD.course_name <> NEW.course_name OR OLD.dept_id <> NEW.dept_id) THEN
        -- 检查是否有教师负责
        IF EXISTS (SELECT * FROM responsibility WHERE course_id = OLD.course_id) THEN
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = '禁止修改：该课程已有教师负责，不可更改课程名或所属院系！';
        END IF;
    END IF;
END //

DELIMITER ;

-- 2025.12.27 responsibility表触发器：教师只能教学自己所属学院的课程
-- 新增操作
DELIMITER //

CREATE TRIGGER trg_check_dept_consistency_insert
BEFORE INSERT ON responsibility
FOR EACH ROW
BEGIN
    DECLARE teacher_dept_id VARCHAR(50);
    DECLARE course_dept_id VARCHAR(50);
    SELECT dept_id INTO teacher_dept_id 
    FROM teacher 
    WHERE staff_id = NEW.staff_id;
    SELECT dept_id INTO course_dept_id 
    FROM course 
    WHERE course_id = NEW.course_id;
    IF teacher_dept_id <> course_dept_id THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '教学任务分配失败：教师所属学院与课程所属学院不一致！';
    END IF;
END //

DELIMITER ;

-- 2025.12.28 增加触发器：开设教学班的时候避免课时冲突
-- 新增操作
DELIMITER //

CREATE TRIGGER trg_course_class_time_conflict
BEFORE INSERT ON course_class
FOR EACH ROW
BEGIN
    DECLARE conflict_count INT;
    SELECT COUNT(*) INTO conflict_count
    FROM course_class
    WHERE staff_id = NEW.staff_id
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND NEW.start_period <= end_period 
      AND NEW.end_period >= start_period;
    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '操作失败：该教师在该时段已有课程，请检查排课时间！';
    END IF;
END //

DELIMITER ;
-- 修改操作
DELIMITER //

CREATE TRIGGER trg_course_class_update_conflict
BEFORE UPDATE ON course_class
FOR EACH ROW
BEGIN
    DECLARE conflict_count INT;

    SELECT COUNT(*) INTO conflict_count
    FROM course_class
    WHERE staff_id = NEW.staff_id
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND class_id <> OLD.class_id
      AND NEW.start_period <= end_period 
      AND NEW.end_period >= start_period;

    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '修改失败：修改后的时段与该教师的其他课程冲突！';
    END IF;
END //

DELIMITER ;

-- 2025.12.28 增加触发器：开设教学班的时候避免教室冲突
DELIMITER //

-- 1. 新增时的教室冲突检查
CREATE TRIGGER trg_course_class_room_insert_conflict
BEFORE INSERT ON course_class
FOR EACH ROW
BEGIN
    DECLARE conflict_count INT;

    -- 检查同一个教室、同一个学期、同一个星期下，是否有时间重叠
    SELECT COUNT(*) INTO conflict_count
    FROM course_class
    WHERE classroom = NEW.classroom
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND NEW.start_period <= end_period 
      AND NEW.end_period >= start_period;

    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '操作失败：该教室在该时段已被占用，请更换教室！';
    END IF;
END //

-- 2. 更新时的教室冲突检查
CREATE TRIGGER trg_course_class_room_update_conflict
BEFORE UPDATE ON course_class
FOR EACH ROW
BEGIN
    DECLARE conflict_count INT;

    SELECT COUNT(*) INTO conflict_count
    FROM course_class
    WHERE classroom = NEW.classroom
      AND semester = NEW.semester
      AND day_of_week = NEW.day_of_week
      AND class_id <> OLD.class_id -- 排除自身
      AND NEW.start_period <= end_period 
      AND NEW.end_period >= start_period;

    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '修改失败：修改后的教室在该时段已被占用！';
    END IF;
END //

DELIMITER ;

-- 2025.12.28 增加触发器：分数大于100改成100，分数小于0改成0
-- 1. 为新增数据创建触发器
DELIMITER //
CREATE TRIGGER trg_course_selection_insert_check
BEFORE INSERT ON course_selection
FOR EACH ROW
BEGIN
    -- 处理平时成绩
    IF NEW.normal_score > 100 THEN
        SET NEW.normal_score = 100;
    ELSEIF NEW.normal_score < 0 THEN
        SET NEW.normal_score = 0;
    END IF;

    -- 处理期末成绩
    IF NEW.test_score > 100 THEN
        SET NEW.test_score = 100;
    ELSEIF NEW.test_score < 0 THEN
        SET NEW.test_score = 0;
    END IF;
END; //
DELIMITER ;

-- 2. 为修改数据创建触发器
DELIMITER //
CREATE TRIGGER trg_course_selection_update_check
BEFORE UPDATE ON course_selection
FOR EACH ROW
BEGIN
    -- 处理平时成绩
    IF NEW.normal_score > 100 THEN
        SET NEW.normal_score = 100;
    ELSEIF NEW.normal_score < 0 THEN
        SET NEW.normal_score = 0;
    END IF;

    -- 处理期末成绩
    IF NEW.test_score > 100 THEN
        SET NEW.test_score = 100;
    ELSEIF NEW.test_score < 0 THEN
        SET NEW.test_score = 0;
    END IF;
END; //
DELIMITER ;

-- 触发器：计算总分
DELIMITER //
-- 处理更新操作时的总分计算
CREATE TRIGGER trg_calc_total_score_update
BEFORE UPDATE ON course_selection
FOR EACH ROW
BEGIN
    DECLARE v_usual_w DECIMAL(2,1);
    DECLARE v_final_w DECIMAL(2,1);

    SELECT usual_weight, final_weight INTO v_usual_w, v_final_w
    FROM course
    WHERE course_id = NEW.course_id;

    IF NEW.normal_score IS NOT NULL AND NEW.test_score IS NOT NULL THEN
        SET NEW.total_score = ROUND(
            (NEW.normal_score * v_usual_w) + (NEW.test_score * v_final_w), 
            0
        );
    END IF;
END //
-- 处理插入操作时的总分计算
CREATE TRIGGER trg_calc_total_score_insert
BEFORE INSERT ON course_selection
FOR EACH ROW
BEGIN
    DECLARE v_usual_w DECIMAL(2,1);
    DECLARE v_final_w DECIMAL(2,1);

    SELECT usual_weight, final_weight INTO v_usual_w, v_final_w
    FROM course
    WHERE course_id = NEW.course_id;

    IF NEW.normal_score IS NOT NULL AND NEW.test_score IS NOT NULL THEN
        SET NEW.total_score = ROUND(
            (NEW.normal_score * v_usual_w) + (NEW.test_score * v_final_w), 
            0
        );
    END IF;
END //

DELIMITER ;

-- 2025.12.29 增加约束可选学期的条件
ALTER TABLE course_class 
ADD CONSTRAINT chk_semester_value 
CHECK (semester IN ('2025-2026秋季学期', '2025-2026春季学期'));

-- 2025.12.29 用存储过程计算绩点，并利用触发器实现自动化更新
DELIMITER //

CREATE PROCEDURE sp_update_student_gpa(IN p_student_id CHAR(8), IN p_class_id CHAR(8))
BEGIN
    UPDATE course_selection
    SET gpa = CASE 
        WHEN total_score BETWEEN 90 AND 100 THEN 4.0
        WHEN total_score BETWEEN 85 AND 89  THEN 3.7
        WHEN total_score BETWEEN 82 AND 84  THEN 3.3
        WHEN total_score BETWEEN 78 AND 81  THEN 3.0
        WHEN total_score BETWEEN 75 AND 77  THEN 2.7
        WHEN total_score BETWEEN 72 AND 74  THEN 2.3
        WHEN total_score BETWEEN 68 AND 71  THEN 2.0
        WHEN total_score BETWEEN 66 AND 67  THEN 1.7
        WHEN total_score BETWEEN 64 AND 65  THEN 1.5
        WHEN total_score BETWEEN 60 AND 63  THEN 1.0
        ELSE 0.0
    END
    WHERE student_id = p_student_id AND class_id = p_class_id;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER trg_auto_calc_gpa
BEFORE UPDATE ON course_selection
FOR EACH ROW
BEGIN
    -- 仅当总成绩发生变化时计算
    IF NEW.total_score IS NOT NULL THEN
        SET NEW.gpa = CASE 
            WHEN NEW.total_score >= 90 THEN 4.0
            WHEN NEW.total_score >= 85 THEN 3.7
            WHEN NEW.total_score >= 82 THEN 3.3
            WHEN NEW.total_score >= 78 THEN 3.0
            WHEN NEW.total_score >= 75 THEN 2.7
            WHEN NEW.total_score >= 72 THEN 2.3
            WHEN NEW.total_score >= 68 THEN 2.0
            WHEN NEW.total_score >= 66 THEN 1.7
            WHEN NEW.total_score >= 64 THEN 1.5
            WHEN NEW.total_score >= 60 THEN 1.0
            ELSE 0.0
        END;
    END IF;
END //

DELIMITER ;

-- 2025.12.29 对选课和退课增加触发器，在选课表变化的时候修改已选人数
-- 增加：
DELIMITER //

CREATE TRIGGER trg_courseselection_insert
AFTER INSERT ON course_selection
FOR EACH ROW
BEGIN
    -- 1. 检查人数是否已满
    -- 使用声明变量来获取当前容量和已选人数
    DECLARE curr_capacity INT;
    DECLARE curr_selected INT;

    SELECT capacity, selected_num INTO curr_capacity, curr_selected 
    FROM course_class 
    WHERE class_id = NEW.class_id;

    -- 2. 如果已满，抛出错误拦截插入
    IF curr_selected >= curr_capacity THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '选课失败：该教学班人数已满！';
    END IF;

    -- 3. 更新开课表人数
    UPDATE course_class 
    SET selected_num = selected_num + 1 
    WHERE class_id = NEW.class_id;
END //

DELIMITER ;

-- 减少：
DELIMITER //

CREATE TRIGGER trg_courseselection_delete
AFTER DELETE ON course_selection
FOR EACH ROW
BEGIN
    UPDATE course_class 
    SET selected_num = GREATEST(0, selected_num - 1) 
    WHERE class_id = OLD.class_id;
END //

DELIMITER ;

-- 2025.12.29 新增触发器，禁止学生选择同一课程的多个教学班或者选择时间重合的课程
DROP TRIGGER IF EXISTS trg_courseselection_insert;

DELIMITER //

CREATE TRIGGER trg_courseselection_insert
BEFORE INSERT ON course_selection
FOR EACH ROW
BEGIN
    -- 定义变量存储当前要选的班级信息
    DECLARE cur_course_id VARCHAR(50);
    DECLARE cur_semester VARCHAR(50);
    DECLARE cur_day INT;
    DECLARE cur_start INT;
    DECLARE cur_end INT;
    DECLARE cur_selected INT;
    DECLARE cur_capacity INT;
    DECLARE conflict_count INT;

    -- 取出当前准备插入的班级的详细信息
    SELECT course_id, semester, day_of_week, start_period, end_period, selected_num, capacity 
    INTO cur_course_id, cur_semester, cur_day, cur_start, cur_end, cur_selected, cur_capacity
    FROM course_class 
    WHERE class_id = NEW.class_id;

    -- 校验：重复选课 (同一课程号)
    IF EXISTS (
        SELECT 1 FROM course_selection cs
        JOIN course_class cc ON cs.class_id = cc.class_id
        WHERE cs.student_id = NEW.student_id 
          AND cc.course_id = cur_course_id
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '选课失败：你已在历史学期修读过该课程，不可重复选课！';
    END IF;

    -- 校验：时间冲突
    IF EXISTS (
        SELECT 1 FROM course_selection cs
        JOIN course_class cc ON cs.class_id = cc.class_id
        WHERE cs.student_id = NEW.student_id
          AND cc.semester = cur_semester
          AND cc.day_of_week = cur_day
          AND (cur_start <= cc.end_period AND cur_end >= cc.start_period)
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '选课失败：与已选课程时间冲突！';
    END IF;
END //

DELIMITER ;



-- 2025.12.30 administer的统计内容：教师总数、学生总数、课程总数、学院总数、各学院学生分布、各学院教师分布、教师职称分布
-- 教师总数
SELECT COUNT(*) AS teacherCount FROM teacher;
-- 学生总数
SELECT COUNT(*) AS studentCount FROM student;
-- 课程总数
SELECT COUNT(*) AS courseCount FROM course;
-- 学院总数
SELECT COUNT(*) AS deptCount FROM department;
-- 各学院学生人数
SELECT d.dept_name AS name,
    (SELECT COUNT(*) FROM student s WHERE s.dept_id = d.dept_id) AS value
FROM department d;
-- 各学院教师人数
SELECT d.dept_name AS name,
    (SELECT COUNT(*) FROM teacher t WHERE t.dept_id = d.dept_id) AS value
FROM department d;
-- 教师职称分布
SELECT professional_rank AS name, COUNT(*) AS value
FROM teacher
WHERE professional_rank IS NOT NULL AND professional_rank != ''
GROUP BY professional_rank;

-- 2025.12.30 teacher的统计内容：统计任课教学班的平时成绩、期末成绩、绩点分布
SELECT
    c.class_id AS classId,
    -- 1. 平时成绩分布 (3个区间)
    SUM(CASE WHEN cs.normal_score < 60 THEN 1 ELSE 0 END) AS normal_low,
    SUM(CASE WHEN cs.normal_score >= 60 AND cs.normal_score < 85 THEN 1 ELSE 0 END) AS normal_mid,
    SUM(CASE WHEN cs.normal_score >= 85 THEN 1 ELSE 0 END) AS normal_high,

    -- 2. 期末成绩分布 (4个区间)
    SUM(CASE WHEN cs.test_score < 60 THEN 1 ELSE 0 END) AS test_fail,
    SUM(CASE WHEN cs.test_score >= 60 AND cs.test_score < 70 THEN 1 ELSE 0 END) AS test_pass,
    SUM(CASE WHEN cs.test_score >= 70 AND cs.test_score < 85 THEN 1 ELSE 0 END) AS test_good,
    SUM(CASE WHEN cs.test_score >= 85 THEN 1 ELSE 0 END) AS test_excellent,

    -- 3. GPA分布 (4个区间)
    SUM(CASE WHEN cs.gpa = 0.0 THEN 1 ELSE 0 END) AS gpa_0,
    SUM(CASE WHEN cs.gpa >= 1.0 AND cs.gpa <= 2.3 THEN 1 ELSE 0 END) AS gpa_1_2,
    SUM(CASE WHEN cs.gpa >= 2.7 AND cs.gpa <= 3.3 THEN 1 ELSE 0 END) AS gpa_2_3,
    SUM(CASE WHEN cs.gpa >= 3.7 AND cs.gpa <= 4.0 THEN 1 ELSE 0 END) AS gpa_3_4
FROM course_selection cs, course_class c
WHERE cs.class_id = c.class_id
  AND c.staff_id = '11010101' -- 测试用
GROUP BY c.class_id;

-- 2025.12.30 student统计内容：统计各学期所选学分、获得学分、各学期平均绩点和总平均绩点（加权平均）
-- 1. 统计该学生各学期的数据
(SELECT 
    semester AS semester_name,
    -- 已选学分：无论及格与否全部累加
    SUM((SELECT credit FROM course WHERE course_id = cs.course_id)) AS total_credits,
    -- 已获得学分：只有 gpa > 0 才计算
    SUM(CASE WHEN gpa > 0 THEN (SELECT credit FROM course WHERE course_id = cs.course_id) ELSE 0 END) AS earned_credits,
    ROUND(
        SUM(gpa * (SELECT credit FROM course WHERE course_id = cs.course_id)) 
        / 
        SUM((SELECT credit FROM course WHERE course_id = cs.course_id)), 
    2) AS avg_gpa,
    1 AS sort_order
FROM course_selection cs
WHERE student_id = '23120001' 
GROUP BY semester)

UNION ALL

-- 2. 统计该学生个人的生涯总计数据
(SELECT 
    '个人总计' AS semester_name, 
    SUM((SELECT credit FROM course WHERE course_id = cs.course_id)) AS total_credits,
    -- 总计已获得学分
    SUM(CASE WHEN gpa > 0 THEN (SELECT credit FROM course WHERE course_id = cs.course_id) ELSE 0 END) AS earned_credits,
    ROUND(
        SUM(gpa * (SELECT credit FROM course WHERE course_id = cs.course_id)) 
        / 
        SUM((SELECT credit FROM course WHERE course_id = cs.course_id)), 
    2) AS avg_gpa,
    2 AS sort_order
FROM course_selection cs
WHERE student_id = '23120001')

ORDER BY sort_order ASC, semester_name DESC;