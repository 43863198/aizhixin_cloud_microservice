/*package myTest;

import com.aizhixin.cloud.data.common.manager.FileOperator;
import com.aizhixin.cloud.data.syn.dto.TeachingclassDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestTeachingclassService {
    private Logger LOG = LoggerFactory.getLogger(TestTeachingclassService.class);
    private FileOperator fileOperator = new FileOperator();

    private void readNowTeachingclassFromDatabase (List<String> allNowLines, List<TeachingclassDomain> allNowTeachingclass, Map<String, TeachingclassDomain> cacheNowTeachingclassMap) {
        for (TeachingclassDomain d : allNowTeachingclass) {
            if (!cacheNowTeachingclassMap.keySet().contains(d.getXkkh())) {
                cacheNowTeachingclassMap.put(d.getXkkh(), d);
            }
            allNowLines.add(d.toString());
        }
    }

    public void readAndCompactData() throws ParseException {
        List<String> allNowLines = new ArrayList<>();
        Map<String, TeachingclassDomain> cacheNowTeachingclassMap = new HashMap<>();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date day1 = format.parse("2017-08-22");
        Date day2 = format.parse("2017-08-23");

        List<TeachingclassDomain> allNowTeachingclass = fileOperator.readTeachingclassData(day2, "teachingclass-source.json");
        if (null == allNowTeachingclass || allNowTeachingclass.size() <= 0) {
            return;
        }

        readNowTeachingclassFromDatabase(allNowLines, allNowTeachingclass, cacheNowTeachingclassMap);

        List<TeachingclassDomain> addList = new ArrayList<>();
        List<TeachingclassDomain> updateList = new ArrayList<>();
        List<TeachingclassDomain> delList = new ArrayList<>();

        Set<String> yestodayTeachingclassCodeSet = new HashSet<>();
        List<TeachingclassDomain>  yestodayTeachingclass = fileOperator.readTeachingclassData(day1, "teachingclass-source.json");
        if (null != yestodayTeachingclass && yestodayTeachingclass.size() > 0) {
            for (TeachingclassDomain t : yestodayTeachingclass) {
                yestodayTeachingclassCodeSet.add(t.getXkkh());

                if (cacheNowTeachingclassMap.keySet().contains(t.getXkkh())) {
                    if (!t.toString().equals(cacheNowTeachingclassMap.get(t.getXkkh()).toString())) {//这次和上次不一样
                        TeachingclassDomain n = cacheNowTeachingclassMap.get(t.getXkkh());
                        if (null == n) {
                            LOG.warn("Now Teachingclass data is error[XKKH:" + t.getXkkh() + "]");
                        } else {
                            updateList.add(n);
                        }
                    }
                } else {//上次有，这次没有
                    delList.add(t);
                }
            }

            for (TeachingclassDomain t : allNowTeachingclass) {
                if (!yestodayTeachingclassCodeSet.contains(t.getXkkh())) {//上次没，这次有
                    addList.add(t);
                }
            }
        } else {
            addList = allNowTeachingclass;
        }

        if (addList.size() > 0) {
            LOG.info("+++++++++++++Teachingclass new add data(" + addList.size() + ")");
            fileOperator.writeTeachingclassDataToOutFile(addList, "teachingclass-add.json");
        } else {
            LOG.info("...........Teachingclass not new add data");
        }

        if (updateList.size() > 0) {
            LOG.info("************Teachingclass new update data(" + updateList.size() + ")");
            fileOperator.writeTeachingclassDataToOutFile(updateList, "teachingclass-update.json");
        } else {
            LOG.info("...........Teachingclass not new update data");
        }

        if (delList.size() > 0) {
            LOG.info("--------------Teachingclass new delete data(" + delList.size() + ")");
            fileOperator.writeTeachingclassDataToOutFile(delList, "teachingclass-delete.json");
        } else {
            LOG.info("...........Teachingclass not new delete data");
        }
    }

    public static void main(String[] args) throws ParseException {
        TestTeachingclassService s = new TestTeachingclassService();
        s.readAndCompactData();
    }
}
*/