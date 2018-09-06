
package com.aizhixin.cloud.dd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.aizhixin.cloud.dd.dorms.domain.BedDomain;
import com.aizhixin.cloud.dd.dorms.domain.FloorDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomDomain;
import com.aizhixin.cloud.dd.dorms.repository.BedRepository;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.QuestionnaireExportJdbc;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.importJdbc;
import com.aizhixin.cloud.dd.rollcall.domain.ClassesAssessDomain;
import com.aizhixin.cloud.dd.rollcall.domain.ExportCollegeAssesDomain;
import com.aizhixin.cloud.dd.rollcall.domain.ExportCourseAssesTotalDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.ExportQuestionAssginDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.ExportTeacherQuestionnaireDomain;
import com.aizhixin.cloud.dd.questionnaire.domain.QuestionDomain;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName: myTest
 * @Description:
 * @author xiagen
 * @date 2017年5月31日 下午12:18:47
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class myTest {

	@Autowired
	private importJdbc importJdbc;
	@Autowired
	private QuestionnaireExportJdbc questionnaireExportJdbc;
	@Autowired
	private BedRepository bedRepository;

	@Test
	public void nt() {
		Map<String, Long> noAss = importJdbc.get(120l, 10);
		Map<String, Long> Ass = importJdbc.getClassTotal(120l);
		List<ClassesAssessDomain> cdl = new ArrayList<>();
		for (String key : Ass.keySet()) {
			ClassesAssessDomain cd = new ClassesAssessDomain();
			cd.setName(key);
			cd.setNeedAssessTotal(Ass.get(key));
			if (null != noAss.get(key)) {
				cd.setNotAssessTotal(noAss.get(key));
				cd.setAssessTotal(Ass.get(key).longValue() - noAss.get(key).longValue());
			} else {
				cd.setAssessTotal(Ass.get(key));
				cd.setNotAssessTotal(0l);
			}
			cdl.add(cd);
		}

		for (ClassesAssessDomain classesDomain : cdl) {
			System.out.println(classesDomain.getName() + "," + classesDomain.getNeedAssessTotal() + ","
					+ classesDomain.getAssessTotal() + "," + classesDomain.getNotAssessTotal());
		}
	}

	@Test
	public void findByExportQuestionAssgin() {
		Long qid = 116L;
		List<ExportQuestionAssginDomain> eqadl = importJdbc.findByQuestions(qid);
		int i = 0;
		// File f=new File("I://tt.txt");
		// FileOutputStream fo=null;
		// try {
		// fo = new FileOutputStream(f);
		// } catch (FileNotFoundException e1) {
		//
		// e1.printStackTrace();
		// }
		List<Long> qq = new ArrayList<>();
		for (ExportQuestionAssginDomain exportQuestionAssginDomain : eqadl) {
			Map<Long, String> map = questionnaireExportJdbc.countClassQuestionAvgScore(qid,
					exportQuestionAssginDomain.getQuestionAssginId());
			if (i == 0) {
				for (Long questionId : map.keySet()) {
					qq.add(questionId);
				}
				Collections.sort(qq);
				i++;
			}
			String qavg = ",";
			for (int j = 0; j < qq.size(); j++) {
				if (j != qq.size() - 1) {
					qavg += map.get(qq.get(j)) + ",";
				} else {
					qavg += map.get(qq.get(j));
				}
			}
			Integer t = questionnaireExportJdbc.countByClassTotalNum(exportQuestionAssginDomain.getQuestionAssginId());
			Double d = questionnaireExportJdbc.countByClassAvgScore(exportQuestionAssginDomain.getQuestionAssginId());
			if (d == null) {
				d = 0.00;
			}
			System.out.println(
					exportQuestionAssginDomain.getCourseName() + "," + exportQuestionAssginDomain.getTeacherName() + ","
							+ exportQuestionAssginDomain.getTeachingClassName() + "," + t + qavg + "," + d);
			// String
			// s=exportQuestionAssginDomain.getCourseName()+","+exportQuestionAssginDomain.getTeacherName()+","+exportQuestionAssginDomain.getTeachingClassName()+","+t+qavg+","+d+"\r\n";
			// try {
			// fo.write(s.getBytes());
			// } catch (FileNotFoundException e) {
			//
			// e.printStackTrace();
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			// }
		}
		// if(fo!=null){
		// try {
		// fo.close();
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }
		// }
	}

	@Test
	public void findByTeacher() {
		class PrComparator implements Comparator {
			public int compare(Object object1, Object object2) {// 实现接口中的方法
				ExportTeacherQuestionnaireDomain p1 = (ExportTeacherQuestionnaireDomain) object1; // 强制转换
				ExportTeacherQuestionnaireDomain p2 = (ExportTeacherQuestionnaireDomain) object2;
				return new Double(p1.getAvg()).compareTo(new Double(p2.getAvg()));
			}
		}
		Long qid = 118L;
		List<ExportTeacherQuestionnaireDomain> eqdl = importJdbc.findTeacherName(qid);
		for (ExportTeacherQuestionnaireDomain exportTeacherQuestionnaireDomain : eqdl) {
			List<QuestionDomain> qdl = importJdbc.countTeacherQuestion(exportTeacherQuestionnaireDomain, qid);
			exportTeacherQuestionnaireDomain.setQdl(qdl);
			Long pepleTotal = importJdbc.countTeacherAssess(exportTeacherQuestionnaireDomain, qid);
			exportTeacherQuestionnaireDomain.setPepleTotal(pepleTotal);
			Double d = importJdbc.countTeacherAvg(exportTeacherQuestionnaireDomain, qid);
			// System.out.println(d);
			if (d == null) {
				exportTeacherQuestionnaireDomain.setAvg(0.00);
			} else {
				exportTeacherQuestionnaireDomain.setAvg(d);
			}

		}
		PrComparator c = new PrComparator();
		Collections.sort(eqdl, c);
		int j = 1;
		for (int i = eqdl.size() - 1; i >= 0; i--) {
			String qavg = "";
			if (eqdl.get(i).getQdl().size() == 0) {
				for (int k = 0; k < 10; k++) {
					qavg = "," + 0.00;
				}
			}
			for (QuestionDomain qq : eqdl.get(i).getQdl()) {
				qavg += "," + qq.getAvgScore();
			}
			System.out.println(j + "," + eqdl.get(i).getJobNum() + "," + eqdl.get(i).getCollegeName() + ","
					+ eqdl.get(i).getTeacherName() + "," + eqdl.get(i).getPepleTotal() + qavg + ","
					+ eqdl.get(i).getAvg());
			j++;
		}
		// int i=0;
		// for (ExportTeacherQuestionnaireDomain
		// exportTeacherQuestionnaireDomain : eqdl) {
		// i++;
		//// System.out.println(exportTeacherQuestionnaireDomain.getQdl().toString());
		// String qavg="";
		// for (QuestionDomain qq : exportTeacherQuestionnaireDomain.getQdl()) {
		// qavg+=","+qq.getAvgScore();
		// }
		// System.out.println(i+","+exportTeacherQuestionnaireDomain.getCollegeName()+","+exportTeacherQuestionnaireDomain.getTeacherName()+","+exportTeacherQuestionnaireDomain.getPepleTotal()+qavg+","+exportTeacherQuestionnaireDomain.getAvg());
		// }
	}

	@Test
	public void countByCourseTeacherInfo() {
		Long qid = 118L;
		Map<String, ExportCourseAssesTotalDomain> map = importJdbc.countCourseTeacherTotal(qid);
		List<ExportCourseAssesTotalDomain> eel = new ArrayList<>();
		for (String courseName : map.keySet()) {
			ExportCourseAssesTotalDomain e = map.get(courseName);
			ExportCourseAssesTotalDomain ed = importJdbc.countByCourseScoreAvgzb(courseName, qid);
			if (null != ed) {
				e.setAvg(ed.getAvg());
				e.setMax(ed.getMax());
				e.setMix(ed.getMix());
				e.setEtTotal(ed.getEtTotal());
				e.setEtzb(ed.getEtzb());
				e.setNetTotal(ed.getNetTotal());
				e.setNetzb(ed.getNetzb());
				e.setNToal(ed.getNToal());
				e.setNzb(ed.getNzb());
			}
			eel.add(e);
		}
		for (ExportCourseAssesTotalDomain exportCourseAssesTotalDomain : eel) {
			System.out.println(exportCourseAssesTotalDomain.getCourseName() + ","
					+ exportCourseAssesTotalDomain.getTeacherTotal() + "," + exportCourseAssesTotalDomain.getAvg() + ","
					+ exportCourseAssesTotalDomain.getMax() + "," + exportCourseAssesTotalDomain.getMix() + ","
					+ exportCourseAssesTotalDomain.getEtTotal() + "," + exportCourseAssesTotalDomain.getEtzb() + ","
					+ exportCourseAssesTotalDomain.getNetTotal() + "," + exportCourseAssesTotalDomain.getNetzb() + ","
					+ exportCourseAssesTotalDomain.getNToal() + "," + exportCourseAssesTotalDomain.getNzb());
		}
	}

	@Test
	public void countByCollegeInfo() {
		Long qid = 120L;
		List<ExportCollegeAssesDomain> cdal = importJdbc.findByCollegeInfo(qid);
		for (ExportCollegeAssesDomain exportCollegeAssesDomain : cdal) {
			List<String> teacherN = importJdbc.findByCollegeAndTeacher(qid, exportCollegeAssesDomain.getCollegeName());
			ExportCollegeAssesDomain e = importJdbc.countByCollegeStuInfo(teacherN, qid);
			exportCollegeAssesDomain.setAvg(e.getAvg());
			exportCollegeAssesDomain.setMax(e.getMax());
			exportCollegeAssesDomain.setMix(e.getMix());
			exportCollegeAssesDomain.setEtTotal(e.getEtTotal());
			exportCollegeAssesDomain.setEtzb(e.getEtzb());
			exportCollegeAssesDomain.setNetTotal(e.getNetTotal());
			exportCollegeAssesDomain.setNetzb(e.getNetzb());
			exportCollegeAssesDomain.setNToal(e.getNToal());
			exportCollegeAssesDomain.setNzb(e.getNzb());
		}
		for (ExportCollegeAssesDomain exportCollegeAssesDomain : cdal) {
			System.out.println(exportCollegeAssesDomain.getCollegeName() + ","
					+ exportCollegeAssesDomain.getTeacherTotal() + "," + exportCollegeAssesDomain.getAvg() + ","
					+ exportCollegeAssesDomain.getMax() + "," + exportCollegeAssesDomain.getMix() + ","
					+ exportCollegeAssesDomain.getEtTotal() + "," + exportCollegeAssesDomain.getEtzb() + ","
					+ exportCollegeAssesDomain.getNetTotal() + "," + exportCollegeAssesDomain.getNetzb() + ","
					+ exportCollegeAssesDomain.getNToal() + "," + exportCollegeAssesDomain.getNzb());
		}
	}

	@Test
	public void importRoomInfo() {

		String floorUrl = "http://dd.aizhixintest.com:80/diandian_api/api/web/v1/floor/save";
		String roomUrl = "http://dd.aizhixintest.com:80/diandian_api/api/web/v1/room/save";
		RestTemplate rest = new RestTemplate();
		HttpHeaders head = new HttpHeaders();
		head.add("Authorization", "Bearer 7fcbe29f-1c59-4d90-beeb-a68f6576530c");

		for (int i = 1; i < 5; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}
			FloorDomain f = new FloorDomain();
			f.setFloorNum(6);
			f.setFloorType(10);
			f.setName("学生公寓" + i + "栋");
			HttpEntity<FloorDomain> ll = new HttpEntity<FloorDomain>(f, head);
			ResponseEntity<String> json = rest.exchange(floorUrl, HttpMethod.POST, ll, String.class);
			ObjectMapper o = new ObjectMapper();
			try {
				Map<String, Object> data = o.readValue(json.getBody(), Map.class);
				if (null != data && null != data.get("data")) {
					Long floorId = Long.valueOf(data.get("data").toString());
					int num = 48;
					if (i > 2) {
						num = 46;
					}
					int a = num;
					for (int k = 1; k < 7; k++) {
						if (k == 1) {
							num = num - 2;
						} else {
							num = a;
						}
						for (int j = 1; j <= num; j++) {
							if (i == 4 && k == 3 && j >= 23) {
								break;
							}
							int beds = 6;
							if ((i == 1 || i == 3) && k >= 2 && j == 10) {
								beds = 4;
							}
							if ((i == 2 || i == 4) && k >= 2 && j == 9) {
								beds = 4;
							}

							RoomDomain r = new RoomDomain();
							r.setFloorId(floorId);
							r.setFloorNo(k + "");
							r.setBeds(beds);
							r.setEmBeds(beds);
							if (i == 1) {
								if (j < 10) {
									r.setNo(k + "-" + "10" + j);
								} else {
									r.setNo(k + "-" + "1" + j);
								}
							} else {
								if (j < 10) {
									r.setNo(i + "-" + k + "0" + j);
								} else {
									r.setNo(i + "-" + k + j);
								}
							}

							List<BedDomain> bedList = new ArrayList<>();
							for (int l = 1; l < beds + 1; l++) {
								BedDomain bd = new BedDomain();
								if (l == 3&&beds==6) {
									bd.setBedType(10);
								} else {
									if (l % 2 == 0) {
										bd.setBedType(10);
									} else {
										bd.setBedType(20);
									}
								}
								bd.setName("床位" + l);
								bedList.add(bd);
							}
							r.setBedList(bedList);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {

								e1.printStackTrace();
							}
							HttpEntity<RoomDomain> rl = new HttpEntity<RoomDomain>(r, head);
							rest.exchange(roomUrl, HttpMethod.POST, rl, String.class);
						}
					}

				}
			} catch (JsonParseException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		for (int l = 6; l < 10; l++) {
			FloorDomain f = new FloorDomain();
			f.setFloorNum(1);
			f.setUnitNum(3);
			f.setFloorType(20);
			f.setName("学生公寓" + l + "栋");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}
			HttpEntity<FloorDomain> ll = new HttpEntity<FloorDomain>(f, head);
			ResponseEntity<String> json = rest.exchange(floorUrl, HttpMethod.POST, ll, String.class);
			ObjectMapper o = new ObjectMapper();
			try {
				Map<String, Object> data = o.readValue(json.getBody(), Map.class);
				if (null != data && null != data.get("data")) {
					Long floorId = Long.valueOf(data.get("data").toString());
					int[] a = new int[] { 4, 4, 4, 6 };
					int[] b = new int[] { 6, 3, 5 };
					int[] c = new int[] { 3, 3, 4 };
					for (int m = 1; m < 4; m++) {
						for (int i = 1; i <= 12; i++) {
							if (l == 6 && m == 1 && (i == 1 || i == 2)) {
								continue;
							}
							if (l == 9 && m == 1 && i == 2) {
								continue;
							}

							for (int j = 1; j <= 4; j++) {
								if (l == 9 && m == 3 && j == 4) {
									continue;
								}
								if (l == 9 && m == 3 && i == 1 && j == 3) {
									continue;
								}
								String n = "";
								if (j == 1) {
									n = "A";
								} else if (j == 2) {
									n = "B";
								} else if (j == 3) {
									n = "C";
								} else {
									n = "D";
								}
								if (i % 2 == 0 && j == 4) {
									break;
								}
								RoomDomain r = new RoomDomain();
								r.setFloorId(floorId);
								r.setFloorNo(1 + "");
								r.setUnitNo(m + "");
								if (i < 10) {
									r.setNo(l + "-" + m + "0" + i + n);
								} else {
									r.setNo(l + "-" + m + i + n);
								}

								int beds = 0;
								if (i % 2 == 0) {
									beds = b[j - 1];
								} else {
									if (l == 9 && m == 3) {
										beds = c[j - 1];
									} else {
										beds = a[j - 1];
									}
								}
								r.setBeds(beds);
								r.setEmBeds(beds);
								List<BedDomain> bedList = new ArrayList<>();
								for (int k = 1; k <= beds; k++) {
									BedDomain bd = new BedDomain();
									if (k % 2 == 0) {
										bd.setBedType(10);
									} else {
										bd.setBedType(20);
									}
									bd.setName("床位" + k);
									bedList.add(bd);
								}
								r.setBedList(bedList);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {

									e1.printStackTrace();
								}
								HttpEntity<RoomDomain> rl = new HttpEntity<RoomDomain>(r, head);
								rest.exchange(roomUrl, HttpMethod.POST, rl, String.class);
							}
						}
					}

				}
			} catch (JsonParseException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

}
