###伪生产数据库导入  **执行时将dd_123改为实际数据了名称


INSERT INTO org_api_uat.t_college (id, name, org_id) SELECT c.id, c.name, c.organ_id from dd_123.dd_college c  where c.`status`="created";
INSERT INTO org_api_uat.t_professional (id, name, college_id, org_id) SELECT c.id, c.name, c.college_id,g.organ_id from dd_123.dd_major c,dd_123.dd_college g  where g.id=c.college_id and c.`status`="created";

INSERT INTO org_api_uat.t_classes (id, name, professional_id, college_id, org_id) SELECT c.id, c.name, c.major_id, g.id as college_id, g.organ_id from dd_123.dd_class c, dd_123.dd_major m,dd_123.dd_college g where c.major_id=m.id and m.college_id=g.id;

---学生---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, classes_id, professional_id, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email,c.id as classes_id, m.id as major_id, g.id as college_id, g.organ_id, 70
from dd_123.dd_user u, dd_123.dd_user_class uc, dd_123.dd_class c, dd_123.dd_major m, dd_123.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.id = uc.user_id and uc.class_id=c.id and u.role_id=2 and u.id = ju.id and c.major_id = m.id and g.id=m.college_id and u.`status`="created" and c.`status`="created" and m.`status`="created" and g.`status`="created";


---老师---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, g.id as college_id, g.organ_id, 60
from dd_123.dd_user u, dd_123.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.role_id=3 and u.id = ju.id and u.college_id=g.id and u.`status`="created" and g.`status`="created";



---管理员---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, u.college_id, u.organ_id, 10
from dd_123.dd_user u,dledu_zhixin.jhi_user ju
WHERE u.role_id=1 and u.id = ju.id and u.`status`="created";

update org_api_uat.t_user u set u.sex='男' where u.sex='male';
update org_api_uat.t_user u set u.sex='女' where u.sex='female';

---角色---

INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_STUDENT', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=70;
INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_TEACHER', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=60;
INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_ORG_ADMIN', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=10;


-----------------------------------------伪生产环环境导入一个学校 学期点点174 ，新平台16--------------------------------------------------------------

----学期学周导入,开发、测试需要处理学期时间重复的问题----
INSERT INTO org_api_uat.t_semester (id, name, start_date, end_date, org_id, num_week)
SELECT id,name,start_date,end_date,organ_id,num_week from dd_123.dd_semester s WHERE s.id in (
SELECT DISTINCT t.TERM_ID from dledu_enrichmind2.em_teach_class t
);


INSERT INTO org_api_uat.t_week (id, name, start_date, end_date, semester_id, org_id, no)
SELECT id,name,start_date,end_date,semester_id,organ_id,name from dd_123.dd_week s WHERE s.semester_id in (
SELECT DISTINCT t.TERM_ID from dledu_enrichmind2.em_teach_class t
);

----课程数据导入，开发、测试环境需要处理ID重复的问题----
INSERT INTO org_api_uat.t_course (id, name, org_id)
SELECT (c.ID ) as ID, c.`NAME`, s.SCHOOL_ID  from dledu_enrichmind2.em_school_course s, dledu_enrichmind2.em_course c where s.COURSE_ID=c.ID and s.DELETE_FLAG=0 and c.`OWNER`!= 10 and c.DELETE_FLAG=0 ORDER BY c.ID;

----教学班数据导入----
INSERT INTO org_api_uat.t_teaching_class (id, name, course_id, semester_id, org_id, class_or_students)
select t.id as id, t.name, t.COURSE_ID as COURSE_ID, s.id, s.organ_id, 20 
from dledu_enrichmind2.em_teach_class t LEFT JOIN dd_123.dd_semester s on t.TERM_ID=s.id 
where t.COURSE_ID is not null and t.DELETE_FLAG=0;


----教学班教师数据导入----
INSERT INTO org_api_uat.t_teaching_class_teacher (teaching_class_id, teacher_id,semester_id, org_id)
select t.id  as id, ttc.TEACHER_ID, s.id, s.organ_id from dledu_enrichmind2.em_teacher_teach_course ttc, dledu_enrichmind2.em_teach_class t, dd_123.dd_semester s where t.TERM_ID=s.id and ttc.COURSE_ID =t.COURSE_ID and  t.COURSE_ID is not null and t.DELETE_FLAG=0; 



----教学班学生数据导入----
INSERT INTO org_api_uat.t_teaching_class_students (teaching_class_id, student_id, semester_id, org_id)
select t.id, c.STUDENT_ID, t.SEMESTER_ID, t.ORG_ID from org_api_uat.t_teaching_class t, dledu_enrichmind2.em_student_study_course c
where t.id=c.TEACH_CLASS_ID;













