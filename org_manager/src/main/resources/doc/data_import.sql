INSERT INTO org_api_uat.t_organization (id, name, code, province, domain_name, logo, SQUARE_LOGO, RECTANGLE_LOGO)
SELECT do.id, do.name, do.code, do.province,do.domain_name, do.logo, do.pt_logo, do.lpt_logo FROM dledu_dd_uat.dd_organ do where do.status = 'created';

INSERT INTO org_api_uat.t_college (id, name, org_id) SELECT c.id, c.name, c.organ_id from dledu_diandian.dd_college c  where c.`status`="created";
INSERT INTO org_api_uat.t_professional (id, name, college_id, org_id) SELECT c.id, c.name, c.college_id,g.organ_id from dledu_diandian.dd_major c,dledu_diandian.dd_college g  where g.id=c.college_id and c.`status`="created";
INSERT INTO org_api_uat.t_classes (id, name, professional_id, college_id, org_id) SELECT c.id, c.name, c.major_id, g.id as college_id, g.organ_id from dledu_diandian.dd_class c, dledu_diandian.dd_major m,dledu_diandian.dd_college g where c.major_id=m.id and m.college_id=g.id

---学生---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, classes_id, professional_id, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email,c.id as classes_id, m.id as major_id, g.id as college_id, g.organ_id, 70
from dledu_diandian.dd_user u, dledu_diandian.dd_user_class uc, dledu_diandian.dd_class c, dledu_diandian.dd_major m, dledu_diandian.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.id = uc.user_id and uc.class_id=c.id and u.role_id=2 and u.id = ju.id and c.major_id = m.id and g.id=m.college_id and u.`status`="created" and c.`status`="created" and m.`status`="created" and g.`status`="created";


---老师---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, g.id as college_id, g.organ_id, 60
from dledu_diandian.dd_user u, dledu_diandian.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.role_id=3 and u.id = ju.id and u.college_id=g.id and u.`status`="created" and g.`status`="created";



---管理员---
INSERT INTO org_api_uat.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, u.college_id, u.organ_id, 10
from dledu_diandian.dd_user u,dledu_zhixin.jhi_user ju
WHERE u.role_id=1 and u.id = ju.id and u.`status`="created";

update org_api_uat.t_user u set u.sex='男' where u.sex='male';
update org_api_uat.t_user u set u.sex='女' where u.sex='female';

---角色---

INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_STUDENT', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=70;
INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_TEACHER', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=60;
INSERT INTO org_api_uat.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_ORG_ADMIN', 'B' FROM org_api_uat.t_user u where u.USER_TYPE=10;






----以下为测试环境导入语句---

INSERT INTO org_api_test.t_college (id, name, org_id) SELECT c.id, c.name, c.organ_id from dledu_dd_test.dd_college c  where c.`status`="created";

INSERT INTO org_api_test.t_professional (id, name, college_id, org_id) SELECT c.id, c.name, c.college_id,g.organ_id from dledu_dd_test.dd_major c,dledu_dd_test.dd_college g  where g.id=c.college_id and c.`status`="created";

INSERT INTO org_api_test.t_classes (id, name, professional_id, college_id, org_id) SELECT c.id, c.name, c.major_id, g.id as college_id, g.organ_id from dledu_dd_test.dd_class c


INSERT INTO org_api_test.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, classes_id, professional_id, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email,c.id as classes_id, m.id as major_id, g.id as college_id, g.organ_id, 70
from dledu_dd_test.dd_user u, dledu_dd_test.dd_user_class uc, dledu_dd_test.dd_class c, dledu_dd_test.dd_major m, dledu_dd_test.dd_college g,dledu_zhixin_test.jhi_user ju
WHERE u.id = uc.user_id and uc.class_id=c.id and u.role_id=2 and u.id = ju.id and c.major_id = m.id and g.id=m.college_id and u.`status`="created" and c.`status`="created" and m.`status`="created" and g.`status`="created";


INSERT INTO org_api_test.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, g.id as college_id, g.organ_id, 60
from dledu_dd_test.dd_user u, dledu_dd_test.dd_college g,dledu_zhixin_test.jhi_user ju
WHERE u.role_id=3 and u.id = ju.id and u.college_id=g.id and u.`status`="created" and g.`status`="created";


update org_api_test.t_user u set u.sex='男' where u.sex='male';
update org_api_test.t_user u set u.sex='女' where u.sex='female';

INSERT INTO org_api_test.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_STUDENT', 'B' FROM org_api_test.t_user u where u.USER_TYPE=70;
INSERT INTO org_api_test.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_TEACHER', 'B' FROM org_api_test.t_user u where u.USER_TYPE=60;









------------------生产数据库导入------------------------------------


INSERT INTO org_api.t_college (id, name, org_id) SELECT c.id, c.name, c.organ_id from  dledu_diandian.dd_college c  where c.`status`="created";
INSERT INTO org_api.t_professional (id, name, college_id, org_id) SELECT c.id, c.name, c.college_id,g.organ_id from  dledu_diandian.dd_major c, dledu_diandian.dd_college g  where g.id=c.college_id and c.`status`="created";
alter table org_api.t_professional drop foreign key  FK_PROFESSIONAL_COLLEGE ;

INSERT INTO org_api.t_classes (id, name, professional_id, college_id, org_id) SELECT c.id, c.name, c.major_id, g.id as college_id, g.organ_id from dledu_diandian.dd_class c, dledu_diandian.dd_major m,dledu_diandian.dd_college g where c.major_id=m.id and m.college_id=g.id;

---学生---
INSERT INTO org_api.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, classes_id, professional_id, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email,c.id as classes_id, m.id as major_id, g.id as college_id, g.organ_id, 70
from  dledu_diandian.dd_user u,  dledu_diandian.dd_user_class uc,  dledu_diandian.dd_class c,  dledu_diandian.dd_major m,  dledu_diandian.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.id = uc.user_id and uc.class_id=c.id and u.role_id=2 and u.id = ju.id and c.major_id = m.id and g.id=m.college_id and u.`status`="created" and c.`status`="created" and m.`status`="created" and g.`status`="created";

---老师---
INSERT INTO org_api.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, g.id as college_id, g.organ_id, 60
from  dledu_diandian.dd_user u,  dledu_diandian.dd_college g,dledu_zhixin.jhi_user ju
WHERE u.role_id=3 and u.id = ju.id and u.college_id=g.id and u.`status`="created" and g.`status`="created";

---管理员---
INSERT INTO org_api.t_user (ACCOUNT_ID, id, name, JOB_NUMBER, sex, phone, email, college_id, org_id, user_type)
SELECT u.id, u.id, u.name, u.person_id, u.gender, ju.phone_number, ju.email, u.college_id, u.organ_id, 10
from  dledu_diandian.dd_user u,dledu_zhixin.jhi_user ju
WHERE u.role_id=1 and u.id = ju.id and u.`status`="created";

update org_api.t_user u set u.sex='男' where u.sex='male';
update org_api.t_user u set u.sex='女' where u.sex='female';

---角色---

INSERT INTO org_api.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_STUDENT', 'B' FROM org_api.t_user u where u.USER_TYPE=70;
INSERT INTO org_api.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_TEACHER', 'B' FROM org_api.t_user u where u.USER_TYPE=60;
INSERT INTO org_api.t_user_role (user_id, role_name, role_group) select u.id, 'ROLE_ORG_ADMIN', 'B' FROM org_api.t_user u where u.USER_TYPE=10;

INSERT INTO org_api.t_organization (id, name, code, province, domain_name, logo, SQUARE_LOGO, RECTANGLE_LOGO)
SELECT do.id, do.name, do.code, do.province,do.domain_name, do.logo, do.pt_logo, do.lpt_logo FROM dledu_diandian.dd_organ do where do.status = 'created';

----学期学周导入,开发、测试需要处理学期时间重复的问题----
INSERT INTO org_api.t_semester (id, name, start_date, end_date, org_id, num_week)
SELECT id,name,start_date,end_date,organ_id,num_week from dledu_diandian.dd_semester s WHERE s.id in (
SELECT DISTINCT t.TERM_ID from dledu_enrichmind2.em_teach_class t
);

INSERT INTO org_api.t_week (id, name, start_date, end_date, semester_id, org_id, no)
SELECT id,name,start_date,end_date,semester_id,organ_id,name from dledu_diandian.dd_week s WHERE s.semester_id in (
SELECT DISTINCT t.TERM_ID from dledu_enrichmind2.em_teach_class t
);

----课程数据导入，开发、测试环境需要处理ID重复的问题----
INSERT INTO org_api.t_course (id, name, org_id)
SELECT (c.ID ) as ID, c.`NAME`, s.SCHOOL_ID  from dledu_enrichmind2.em_school_course s, dledu_enrichmind2.em_course c where s.COURSE_ID=c.ID and s.DELETE_FLAG=0 and c.`OWNER`!= 10 and c.DELETE_FLAG=0 ORDER BY c.ID;

----教学班数据导入----
INSERT INTO org_api.t_teaching_class (id, name, course_id, semester_id, org_id, class_or_students)
select t.id as id, t.name, t.COURSE_ID as COURSE_ID, s.id, s.organ_id, 20
from dledu_enrichmind2.em_teach_class t LEFT JOIN dledu_diandian.dd_semester s on t.TERM_ID=s.id
where t.COURSE_ID is not null and t.DELETE_FLAG=0;

----教学班教师数据导入----
INSERT INTO org_api.t_teaching_class_teacher (teaching_class_id, teacher_id,semester_id, org_id)
select t.id  as id, ttc.TEACHER_ID, s.id, s.organ_id from dledu_enrichmind2.em_teacher_teach_course ttc, dledu_enrichmind2.em_teach_class t, dledu_diandian.dd_semester s where t.TERM_ID=s.id and ttc.COURSE_ID =t.COURSE_ID and  t.COURSE_ID is not null and t.DELETE_FLAG=0;

----教学班学生数据导入----
INSERT INTO org_api.t_teaching_class_students (teaching_class_id, student_id, semester_id, org_id)
select t.id, c.STUDENT_ID, t.SEMESTER_ID, t.ORG_ID from org_api.t_teaching_class t, dledu_enrichmind2.em_student_study_course c
where t.id=c.TEACH_CLASS_ID;


INSERT INTO t_year (id, name, org_id) VALUES ("51201520161","2015~2016学年",51);
INSERT INTO t_year (id, name, org_id) VALUES ("51201620171","2016~2017学年",51);
INSERT INTO t_year (id, name, org_id) VALUES ("74201620171","2016~2017学年",74);
INSERT INTO t_year (id, name, org_id) VALUES ("79201620171","2016~2017学年",79);
INSERT INTO t_year (id, name, org_id) VALUES ("81201620171","2016~2017学年",81);
INSERT INTO t_year (id, name, org_id) VALUES ("83201620171","2016~2017学年",83);
INSERT INTO t_year (id, name, org_id) VALUES ("85201620171","2016~2017学年",85);
INSERT INTO t_year (id, name, org_id) VALUES ("87201620171","2016~2017学年",87);
INSERT INTO t_year (id, name, org_id) VALUES ("95201620171","2016~2017学年",95);

UPDATE t_semester SET YEAR_ID="51201520161" where id=45;
UPDATE t_semester SET YEAR_ID="51201620171" where id=53;
UPDATE t_semester SET YEAR_ID="74201620171" where id=60;
UPDATE t_semester SET YEAR_ID="79201620171" where id=63;
UPDATE t_semester SET YEAR_ID="81201620171" where id=65;
UPDATE t_semester SET YEAR_ID="83201620171" where id=67;
UPDATE t_semester SET YEAR_ID="85201620171" where id=69;
UPDATE t_semester SET YEAR_ID="87201620171" where id=73;
UPDATE t_semester SET YEAR_ID="95201620171" where id=79;