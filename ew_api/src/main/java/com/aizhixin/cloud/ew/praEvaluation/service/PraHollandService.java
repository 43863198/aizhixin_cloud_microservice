package com.aizhixin.cloud.ew.praEvaluation.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.praEvaluation.domain.AppDimensionScoreDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.AppRecordDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.CharacteristicDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.DimensionScoreDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.HollandReportDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.MBTIReportDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PerfessionalDimensionDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PerfessionalReportDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PraDimensionScoreDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PraHollandChoiceDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PraHollandDomain;
import com.aizhixin.cloud.ew.praEvaluation.domain.PraJobsDomain;
import com.aizhixin.cloud.ew.praEvaluation.entity.AppAdvantage;
import com.aizhixin.cloud.ew.praEvaluation.entity.AppCharacteristic;
import com.aizhixin.cloud.ew.praEvaluation.entity.AppJobs;
import com.aizhixin.cloud.ew.praEvaluation.entity.AppUserChoice;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraAnswerRecord;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraChoice;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraDimension;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraDimensionReport;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraJobsToEvaluation;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraQuestion;
import com.aizhixin.cloud.ew.praEvaluation.entity.PraUserReport;
import com.aizhixin.cloud.ew.praEvaluation.entity.Report;
import com.aizhixin.cloud.ew.praEvaluation.repository.AppAdvantageRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.AppCharacteristicRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.AppJobsRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.AppUserChoiceRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraAnswerRecordRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraChoiceRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraDimensionDescriptionRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraDimensionReportRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraDimensionRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraJobsToEvaluationRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraQuestionRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.PraUserReportRepository;
import com.aizhixin.cloud.ew.praEvaluation.repository.ReportRepository;

/**
 * 霍兰德测评操作后台API
 * 
 * @author Rigel.ma
 *
 */
@Component
@Transactional
public class PraHollandService {

	@Autowired
	private PraQuestionRepository praquestionRepository;

	@Autowired
	private PraChoiceRepository praChoiceRepository;

	@Autowired
	private PraAnswerRecordRepository praAnswerRecordRepository;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private PraDimensionReportRepository praDimensionReportRepository;

	@Autowired
	private PraDimensionRepository praDimensionRepository;

	@Autowired
	private AppAdvantageRepository appAdvantageRepository;

	@Autowired
	private AppCharacteristicRepository appCharacteristicRepository;

	@Autowired
	private AppJobsRepository appJobsRepository;

	@Autowired
	private PraUserReportRepository praUserReportRepository;

	@Autowired
	private AppUserChoiceRepository appUserChoiceRepository;

	@Autowired
	private PraDimensionDescriptionRepository praDimensionDescriptionRepository;

	@Autowired
	private PraJobsToEvaluationRepository praJobsToEvaluationRepository;

	// 出题
	public Map<String, Object> getHolland() {

		Map<String, Object> result = new HashMap<String, Object>();
		List<PraHollandDomain> praHollandDomains = new ArrayList<>();

		List<PraQuestion> praQuestions = praquestionRepository.findByEvaluationId(1l);

		for (int i = 0; i < praQuestions.size(); i++) {
			PraQuestion praQuestion = praQuestions.get(i);
			PraHollandDomain praHollanddomain = new PraHollandDomain();
			praHollanddomain.setQuestion(praQuestion.getQuestion());
			praHollanddomain.setNum(praQuestion.getNum());
			// praHollanddomain.setDimensionId(appquestion.getDimension().getId());
			List<PraHollandChoiceDomain> praChoiceDomains = new ArrayList<>();
			List<PraChoice> praChoices = praChoiceRepository.findByQuestionId(praQuestion.getId());
			for (int j = 0; j < praChoices.size(); j++) {
				PraChoice praChoice = praChoices.get(j);
				PraHollandChoiceDomain pradomain = new PraHollandChoiceDomain();
				pradomain.setChoiceId(praChoice.getId());
				pradomain.setChoiceCode(praChoice.getChoiceCode());
				pradomain.setChoice(praChoice.getChoiceContent());
				pradomain.setDimensionId(praChoice.getDimensionId());
				pradomain.setScore(praChoice.getScore());
				praChoiceDomains.add(pradomain);

			}
			praHollanddomain.setChoices(praChoiceDomains);
			praHollandDomains.add(praHollanddomain);

		}

		// 计算测评人数，打开题目后测评人数加1
		/*
		 * PraEvaluation appEvaluation= appEvaluationRepository.findOne(1l);
		 * Integer num1 = appEvaluation.getNum(); appEvaluation.setNum(1); }
		 * appEvaluationRepository.save(appEvaluation);
		 */
		result.put("praHollandDomains", praHollandDomains);
		return result;
	}

	// 保存答题记录

	public Map<String, Object> saveRecord(List<AppRecordDomain> praHollandRecordDomain, AccountDTO account) {

		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		// String userName = account.getName();
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);
		// 保存答题记录
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 1l);
		int times = praUserReports.size() + 1;

		// List<AppRecordDomain> appRecordDomains=
		// hollandRecordDomain.getAppRecordDomains();

		for (int i = 0; i < praHollandRecordDomain.size(); i++) {
			PraAnswerRecord appRecordAnswer = new PraAnswerRecord();
			AppRecordDomain appRecordDomain = praHollandRecordDomain.get(i);
			appRecordAnswer.setUserId(account.getId());
			appRecordAnswer.setUserName(account.getName());
			appRecordAnswer.setQuestionId(appRecordDomain.getQuestionId());
			appRecordAnswer.setChoiceId(appRecordDomain.getChoiceId());
			// dimensionId=appRecordDomain.getDimensionId();
			appRecordAnswer.setDimensionId(appRecordDomain.getDimensionId());
			appRecordAnswer.setEvaluationId(1l);
			appRecordAnswer.setTimes(times);
			appRecordAnswer.setScore(appRecordDomain.getScore());
			// stepScote= stepScote +appRecordDomain.getScore();
			appRecordAnswer.setRecordDate(date);
			praAnswerRecordRepository.save(appRecordAnswer);
		}

		result.put("success", "ok");
		return result;
	}

	// 判断职业优势、特点分析、推荐工作

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public Map<String, Object> judgeHolland(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();

		Long userId = account.getId();
		String userName = account.getName();

		// 先算维度得分

		@SuppressWarnings("rawtypes")
		TreeSet treeset = new TreeSet();
		List<AppDimensionScoreDomain> dimensionScoreDomains = new ArrayList<>();
		List<Long> characteristicIds = new ArrayList<>();

		// 算测评的次数
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 1l);
		int times = praUserReports.size() + 1;

		// 取这套测评的维度所有维度
		List<PraDimension> appDimensions = praDimensionRepository.findByEvaluationId(1l);

		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);

		PraUserReport praUserReport = new PraUserReport();

		for (int j = 0; j < appDimensions.size(); j++) {
			PraDimension appDimension = appDimensions.get(j);
			Long dimensionId = appDimension.getId();

			// 算每個維度的分值
			List<PraAnswerRecord> appAnswerRecords = praAnswerRecordRepository
					.findByUserIdAndEvaluationIdAndDimensionIdAndTimes(userId, 1l, dimensionId, times);
			Double score = 0.0;
			PraDimensionReport praDimensionReport = new PraDimensionReport();
			for (int i = 0; i < appAnswerRecords.size(); i++) {
				PraAnswerRecord appStepRecord = appAnswerRecords.get(i);
				score = score + appStepRecord.getScore();
			}
			praDimensionReport.setDimensionId(dimensionId);
			praDimensionReport.setEvaluationId(1l);
			praDimensionReport.setUserId(userId);
			praDimensionReport.setUserName(userName);
			praDimensionReport.setScore(score);
			praDimensionReport.setTimes(times);
			praDimensionReport.setReportDate(date);
			praDimensionReportRepository.save(praDimensionReport);

			AppDimensionScoreDomain dimensionScoreDomain = new AppDimensionScoreDomain();
			dimensionScoreDomain.setDimensionId(dimensionId);
			String dimensionCode = praDimensionRepository.findOne(dimensionId).getName();
			dimensionScoreDomain.setDimensionSort(appDimension.getParentDimensionId());
			dimensionScoreDomain.setDimensionName(dimensionCode);
			dimensionScoreDomain.setScore(score);
			// dimensionScoreDomains.add(dimensionScoreDomain);
			treeset.add(dimensionScoreDomain);
		}
		Iterator<AppDimensionScoreDomain> iterator = treeset.iterator();
		while (iterator.hasNext()) {
			AppDimensionScoreDomain dimensionScoreDomain1 = iterator.next();
			dimensionScoreDomains.add(dimensionScoreDomain1);

			// 判断特点
			if (dimensionScoreDomain1.getScore() > 6) {
				AppCharacteristic appCharacteristic = appCharacteristicRepository
						.findByDimensionAndMinScore(dimensionScoreDomain1.getDimensionName(), 7.0);
				characteristicIds.add(appCharacteristic.getId());

			} else if (dimensionScoreDomain1.getScore() < 4) {
				AppCharacteristic appCharacteristic = appCharacteristicRepository
						.findByDimensionAndMaxScore(dimensionScoreDomain1.getDimensionName(), 3);
				characteristicIds.add(appCharacteristic.getId());

			} else {
				AppCharacteristic appCharacteristic = appCharacteristicRepository
						.findByDimensionAndMinScoreAndMaxScore(dimensionScoreDomain1.getDimensionName(), 4, 6);
				characteristicIds.add(appCharacteristic.getId());

			}
		}
		// 判断职业优势 职业优势最多取2位，如果第一的分值比第二的高3分，则为第一维度
		String code;
		AppAdvantage appAdvantage;

		//
		if (dimensionScoreDomains.get(0).getScore() - (dimensionScoreDomains.get(1).getScore()) >= 3) {
			code = dimensionScoreDomains.get(0).getDimensionName();
			appAdvantage = appAdvantageRepository.findByCode(code);
		} else {
			code = dimensionScoreDomains.get(0).getDimensionName() + dimensionScoreDomains.get(1).getDimensionName();
			appAdvantage = appAdvantageRepository.findByCode(code);
		}

		// 判断工作
		String jobCode = dimensionScoreDomains.get(0).getDimensionName()
				+ dimensionScoreDomains.get(1).getDimensionName() + dimensionScoreDomains.get(2).getDimensionName();
		AppJobs appJobs = appJobsRepository.findByCode(jobCode);

		AppUserChoice appUserChoice = appUserChoiceRepository.findByEvaluationIdAndUserIdAndTimes(1l, userId, times);
		if (appUserChoice != null) {
			if (jobCode.contains(appUserChoice.getCode())) {
				appUserChoice.setResult("Y");
			} else {
				appUserChoice.setResult("N");
			}
			appUserChoice.setResultCode(jobCode);
			appUserChoice.setResultDate(date);
			appUserChoiceRepository.save(appUserChoice);
		}

		praUserReport.setUserId(userId);
		praUserReport.setUserName(userName);
		praUserReport.setAdvantageId(appAdvantage.getId());
		praUserReport.setJobsId(appJobs.getId());
		praUserReport.setEvaluationId(1L);
		praUserReport.setCharacteristicId(characteristicIds.get(0));
		praUserReport.setCharacteristicId1(characteristicIds.get(1));
		praUserReport.setCharacteristicId2(characteristicIds.get(2));
		praUserReport.setCharacteristicId3(characteristicIds.get(3));
		praUserReport.setCharacteristicId4(characteristicIds.get(4));
		praUserReport.setCharacteristicId5(characteristicIds.get(5));
		praUserReport.setReportDate(date);
		praUserReport.setTimes(times);
		praUserReport.setMemo(jobCode);
		praUserReportRepository.save(praUserReport);

		result.put("success", "Ok");
		return result;
	}

	// 用户的测评结果
	public Map<String, Object> getHollandReport(AccountDTO account) {
		Long userId = account.getId();
		Map<String, Object> result = new HashMap<String, Object>();
		HollandReportDomain hollandReportDomain = new HollandReportDomain();
		List<PraUserReport> appUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 1l);
		if (appUserReports.size() > 0) {
			Integer times = appUserReports.size();

			PraUserReport appUserReport = praUserReportRepository.findByUserIdAndEvaluationIdAndTimes(userId, 1l,
					times);

			List<PraDimensionReport> appDimensionReports = praDimensionReportRepository
					.findByUserIdAndEvaluationIdAndTimes(userId, 1l, times);
			// List<AppDimensionReport> appDimensionReports = new
			// ArrayList<AppDimensionReport>();

			TreeSet<AppDimensionScoreDomain> treeset = new TreeSet<AppDimensionScoreDomain>();
			List<AppDimensionScoreDomain> dimensionScoreDomains = new ArrayList<>();
			for (int i = 0; i < appDimensionReports.size(); i++) {
				PraDimensionReport appDimensionReport = appDimensionReports.get(i);
				AppDimensionScoreDomain dimensionScoreDomain = new AppDimensionScoreDomain();
				dimensionScoreDomain.setDimensionId(appDimensionReport.getDimensionId());
				dimensionScoreDomain.setDimensionName(
						praDimensionRepository.findOne(appDimensionReport.getDimensionId()).getName());
				dimensionScoreDomain.setDimensionSort(
						praDimensionRepository.findOne(appDimensionReport.getDimensionId()).getParentDimensionId());
				dimensionScoreDomain.setScore(appDimensionReport.getScore());
				// dimensionScoreDomains.add(dimensionScoreDomain);
				treeset.add(dimensionScoreDomain);
			}
			String sort = "";
			// 六个维度的排序
			Iterator<AppDimensionScoreDomain> iterator = treeset.iterator();
			while (iterator.hasNext()) {
				AppDimensionScoreDomain dimensionScoreDomain1 = iterator.next();
				dimensionScoreDomains.add(dimensionScoreDomain1);

				// 六个维度的分值排序
				sort = sort + dimensionScoreDomain1.getDimensionName();
			}

			result.put("sort", sort);

			AppAdvantage appAdvantage = appAdvantageRepository.findOne(appUserReport.getAdvantageId());

			// 独立的推荐职位
			String jobStr = appUserReport.getMemo();
			List<PraJobsToEvaluation> praJobsToEvaluations = praJobsToEvaluationRepository.findByCode(jobStr);
			List<PraJobsDomain> praJobsDomains = new ArrayList<>();

			for (int j = 0; j < praJobsToEvaluations.size(); j++) {
				PraJobsToEvaluation praJobsToEvaluation = praJobsToEvaluations.get(j);
				PraJobsDomain praJobsDomain = new PraJobsDomain();
				praJobsDomain.setJobsId(praJobsToEvaluation.getPraJobs().getId());
				praJobsDomain.setName(praJobsToEvaluation.getPraJobs().getName());
				praJobsDomains.add(praJobsDomain);
			}

			result.put("praJobs", praJobsDomains);

			AppJobs appJobs = appJobsRepository.findOne(appUserReport.getJobsId());
			String code = appJobs.getCode();
			List<String> jobs = new ArrayList<>();
			String job1 = appJobs.getJob1();
			jobs.add(job1);
			String job2 = appJobs.getJob2();
			jobs.add(job2);
			String job3 = appJobs.getJob3();
			jobs.add(job3);
			result.put("code", code);
			List<CharacteristicDomain> characteristicDomains = new ArrayList<>();
			List<AppCharacteristic> appCharacteristics = new ArrayList<>();
			AppCharacteristic appCharacteristic0 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId());
			appCharacteristics.add(appCharacteristic0);
			AppCharacteristic appCharacteristic1 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId1());
			appCharacteristics.add(appCharacteristic1);
			AppCharacteristic appCharacteristic2 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId2());
			appCharacteristics.add(appCharacteristic2);
			AppCharacteristic appCharacteristic3 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId3());
			appCharacteristics.add(appCharacteristic3);
			AppCharacteristic appCharacteristic4 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId4());
			appCharacteristics.add(appCharacteristic4);
			AppCharacteristic appCharacteristic5 = appCharacteristicRepository
					.findOne(appUserReport.getCharacteristicId5());
			appCharacteristics.add(appCharacteristic5);

			for (int i = 0; i < appCharacteristics.size(); i++) {
				CharacteristicDomain characteristicDomain = new CharacteristicDomain();
				AppCharacteristic appCharacteristic = appCharacteristics.get(i);

				characteristicDomain.setCharacteristicId(appCharacteristic.getId());
				characteristicDomain.setContent(appCharacteristic.getContent());
				characteristicDomain.setDimensionName(appCharacteristic.getDimension());
				characteristicDomains.add(characteristicDomain);
			}
			hollandReportDomain.setUserId(userId);
			hollandReportDomain.setUserName(account.getName());
			hollandReportDomain.setEvaluationId(1L);
			hollandReportDomain.setDimensionScoreDomains(dimensionScoreDomains);
			hollandReportDomain.setAdvantageContent(appAdvantage.getContent());
			hollandReportDomain.setCharacteristicDomains(characteristicDomains);
			hollandReportDomain.setJobs(jobs);
			result.put("hollandReportDomain", hollandReportDomain);
		}

		return result;
	}

	// MBTI出题
	public Map<String, Object> getMBTI() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<PraHollandDomain> praHollandDomains = new ArrayList<>();

		List<PraQuestion> praQuestions = praquestionRepository.findByEvaluationId(2l);

		for (int i = 0; i < praQuestions.size(); i++) {
			PraQuestion praQuestion = praQuestions.get(i);
			PraHollandDomain praHollanddomain = new PraHollandDomain();
			praHollanddomain.setQuestion(praQuestion.getQuestion());
			praHollanddomain.setNum(praQuestion.getNum());
			praHollanddomain.setQuestionId(praQuestion.getId());
			// praHollanddomain.setDimensionId(appquestion.getDimension().getId());
			List<PraHollandChoiceDomain> praChoiceDomains = new ArrayList<>();
			List<PraChoice> praChoices = praChoiceRepository.findByQuestionId(praQuestion.getId());
			for (int j = 0; j < praChoices.size(); j++) {
				PraChoice praChoice = praChoices.get(j);
				PraHollandChoiceDomain pradomain = new PraHollandChoiceDomain();
				pradomain.setChoiceId(praChoice.getId());
				pradomain.setChoiceCode(praChoice.getChoiceCode());
				pradomain.setChoice(praChoice.getChoiceContent());
				pradomain.setDimensionId(praChoice.getDimensionId());
				pradomain.setScore(praChoice.getScore());
				praChoiceDomains.add(pradomain);

			}
			praHollanddomain.setChoices(praChoiceDomains);
			praHollandDomains.add(praHollanddomain);

		}

		// 计算测评人数，打开题目后测评人数加1
		/*
		 * PraEvaluation appEvaluation= appEvaluationRepository.findOne(1l);
		 * Integer num1 = appEvaluation.getNum(); appEvaluation.setNum(1); }
		 * appEvaluationRepository.save(appEvaluation);
		 */
		result.put("praHollandDomains", praHollandDomains);
		return result;
	}

	public Map<String, Object> saveMBTIRecord(List<AppRecordDomain> praHollandRecordDomain, AccountDTO account) {

		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		// String userName = account.getName();
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);
		// 保存答题记录
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 2l);
		int times = praUserReports.size() + 1;

		// List<AppRecordDomain> appRecordDomains=
		// hollandRecordDomain.getAppRecordDomains();

		for (int i = 0; i < praHollandRecordDomain.size(); i++) {
			PraAnswerRecord appRecordAnswer = new PraAnswerRecord();
			AppRecordDomain appRecordDomain = praHollandRecordDomain.get(i);
			appRecordAnswer.setUserId(account.getId());
			appRecordAnswer.setUserName(account.getName());
			appRecordAnswer.setQuestionId(appRecordDomain.getQuestionId());
			appRecordAnswer.setChoiceId(appRecordDomain.getChoiceId());
			// dimensionId=appRecordDomain.getDimensionId();
			appRecordAnswer.setDimensionId(appRecordDomain.getDimensionId());
			appRecordAnswer.setEvaluationId(2l);
			appRecordAnswer.setTimes(times);
			appRecordAnswer.setScore(appRecordDomain.getScore());
			// stepScote= stepScote +appRecordDomain.getScore();
			appRecordAnswer.setRecordDate(date);
			praAnswerRecordRepository.save(appRecordAnswer);
		}
		result.put("success", "ok");
		return result;

	}

	public Map<String, Object> judgeMBTI(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		String userName = account.getName();
		Long evaluationId = 2l;
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, evaluationId);
		int times = praUserReports.size() + 1;

		// 维护Dimension_REport数据
		List<PraDimension> dimensions = praDimensionRepository.findByEvaluationId(evaluationId);
		for (int j = 0; j < dimensions.size(); j++) {
			PraDimension dimension = dimensions.get(j);
			double dimensionScore = 0.0;
			List<PraAnswerRecord> answerRecords = praAnswerRecordRepository
					.findByUserIdAndEvaluationIdAndDimensionIdAndTimes(userId, evaluationId, dimension.getId(), times);
			// 算维度的分值
			for (int m = 0; m < answerRecords.size(); m++) {
				PraAnswerRecord answerRecord = answerRecords.get(m);
				dimensionScore = dimensionScore + answerRecord.getScore();

			}

			PraDimensionReport dimensionReport = new PraDimensionReport();
			dimensionReport.setScore(dimensionScore);
			dimensionReport.setUserId(userId);
			dimensionReport.setUserName(userName);
			dimensionReport.setDimensionId(dimension.getId());
			dimensionReport.setName(dimension.getName());
			// dimensionReport.setCost(cost);
			dimensionReport.setEvaluationId(2l);
			// 算次数
			// List<DimensionReport> d =
			// dimensionReportRepository.findByUserIdAndDimensionId(userId,dimension.getId());
			// times = d.size();
			dimensionReport.setTimes(times);
			dimensionReport.setReportDate(date);
			praDimensionReportRepository.save(dimensionReport);
		}

		StringBuffer resultStr = new StringBuffer();
		PraDimensionReport E = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "E", times);
		PraDimensionReport I = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "I", times);
		if (E.getScore() > I.getScore())
			resultStr.append("E");
		else {
			resultStr.append("I");
		}

		PraDimensionReport S = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "S", times);
		PraDimensionReport N = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "N", times);
		if (S.getScore() > N.getScore())
			resultStr.append("S");
		else {
			resultStr.append("N");
		}

		PraDimensionReport T = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "T", times);
		PraDimensionReport F = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "F", times);
		if (T.getScore() > F.getScore())
			resultStr.append("T");
		else {
			resultStr.append("F");
		}
		PraDimensionReport J = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "J", times);
		PraDimensionReport P = praDimensionReportRepository.findByUserIdAndEvaluationIdAndNameAndTimes(userId,
				evaluationId, "P", times);
		if (J.getScore() > P.getScore())
			resultStr.append("J");
		else {
			resultStr.append("P");
		}
		String str = resultStr.toString();
		result.put("str", str);
		Report report = reportRepository.findByCode(str);

		// 维护user_REport数据

		PraUserReport userReport = new PraUserReport();
		userReport.setUserId(userId);
		userReport.setUserName(userName);
		userReport.setEvaluationId(evaluationId);
		userReport.setAdvantageId(report.getId());
		userReport.setMemo(str);
		userReport.setTimes(times);
		userReport.setReportDate(date);
		praUserReportRepository.save(userReport);
		result.put("userReport", "userReport save ok");
		return result;
	}

	// 有无测试报告接口
	public Map<String, Object> getHollandReports(AccountDTO account, Long evaluationId) {

		Map<String, Object> result = new HashMap<String, Object>();
		// HollandReportDomain hollandReportDomain = new HollandReportDomain();
		List<PraUserReport> appUserReports = praUserReportRepository.findByUserIdAndEvaluationId(account.getId(),
				evaluationId);
		Integer times = appUserReports.size();
		result.put("times", times);
		return result;
	}

	public Map<String, Object> getMBTIReport(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<PraUserReport> userReports = praUserReportRepository.findByUserIdAndEvaluationId(account.getId(), 2l);
		PraUserReport userReport = new PraUserReport();
		if (userReports.size() > 0) {
			for (int i = 0; i < userReports.size(); i++) {
				userReport = userReports.get(i);
			}
			MBTIReportDomain mBTIReportDomain = new MBTIReportDomain();
			mBTIReportDomain.setUserId(account.getId());
			mBTIReportDomain.setUserName(account.getName());
			mBTIReportDomain.setReportCode(reportRepository.findOne(userReport.getAdvantageId()).getCode());
			mBTIReportDomain.setReportContent(reportRepository.findOne(userReport.getAdvantageId()).getBriefContent());
			// mBTIReportDomain.setBriefContent(userReport.getReport().getBriefContent());
			List<PraDimensionReport> dimensionReports = praDimensionReportRepository
					.findByUserIdAndEvaluationIdAndTimes(account.getId(), 2l, userReports.size());
			List<DimensionScoreDomain> dimensionScoreDomains = new ArrayList<>();
			for (int i = 0; i < dimensionReports.size(); i++) {
				PraDimensionReport dimensionReport = dimensionReports.get(i);
				DimensionScoreDomain dimensionScoreDomain = new DimensionScoreDomain();
				dimensionScoreDomain.setDimensionId(dimensionReport.getDimensionId());
				dimensionScoreDomain.setDimensionName(dimensionReport.getName());
				dimensionScoreDomain.setScore(dimensionReport.getScore());
				dimensionScoreDomains.add(dimensionScoreDomain);

			}
			mBTIReportDomain.setDimensionScoreDomains(dimensionScoreDomains);
			result.put("mBTIReportDomain", mBTIReportDomain);

			// 独立的推荐职位
			String jobStr = userReport.getMemo();
			List<PraJobsToEvaluation> praJobsToEvaluations = praJobsToEvaluationRepository.findByCode(jobStr);
			List<PraJobsDomain> praJobsDomains = new ArrayList<>();

			for (int j = 0; j < praJobsToEvaluations.size(); j++) {
				PraJobsToEvaluation praJobsToEvaluation = praJobsToEvaluations.get(j);
				PraJobsDomain praJobsDomain = new PraJobsDomain();
				praJobsDomain.setJobsId(praJobsToEvaluation.getPraJobs().getId());
				praJobsDomain.setName(praJobsToEvaluation.getPraJobs().getName());
				praJobsDomains.add(praJobsDomain);
			}

			result.put("praJobs", praJobsDomains);

			return result;
		} else {
			result.put("userReport", "no report!");
			return result;
		}
	}

	public Map<String, Object> getPerfessional() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<PraHollandDomain> praHollandDomains = new ArrayList<>();

		List<PraQuestion> praQuestions = praquestionRepository.findByEvaluationId(4l);

		for (int i = 0; i < praQuestions.size(); i++) {
			PraQuestion praQuestion = praQuestions.get(i);
			PraHollandDomain praHollanddomain = new PraHollandDomain();
			praHollanddomain.setQuestion(praQuestion.getQuestion());

			praHollanddomain.setNum(praQuestion.getNum());
			praHollanddomain.setQuestionId(praQuestion.getId());
			praHollanddomain.setMemo(praQuestion.getMemo());
			// praHollanddomain.setDimensionId(appquestion.getDimension().getId());
			List<PraHollandChoiceDomain> praChoiceDomains = new ArrayList<>();
			List<PraChoice> praChoices = praChoiceRepository.findByEvaluationId(4l);
			for (int j = 0; j < praChoices.size(); j++) {
				PraChoice praChoice = praChoices.get(j);
				PraHollandChoiceDomain pradomain = new PraHollandChoiceDomain();
				pradomain.setChoiceId(praChoice.getId());
				pradomain.setChoiceCode(praChoice.getChoiceCode());
				pradomain.setChoice(praChoice.getChoiceContent());
				pradomain.setDimensionId(praChoice.getDimensionId());
				pradomain.setScore(praChoice.getScore());
				praChoiceDomains.add(pradomain);

			}
			praHollanddomain.setChoices(praChoiceDomains);
			praHollandDomains.add(praHollanddomain);

		}

		// 计算测评人数，打开题目后测评人数加1
		/*
		 * PraEvaluation appEvaluation= appEvaluationRepository.findOne(1l);
		 * Integer num1 = appEvaluation.getNum(); appEvaluation.setNum(1); }
		 * appEvaluationRepository.save(appEvaluation);
		 */
		result.put("praHollandDomains", praHollandDomains);
		return result;
	}

	public Map<String, Object> savePerfessionalRecord(List<AppRecordDomain> praHollandRecordDomain,
			AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		String userName = account.getName();
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);
		// 保存答题记录
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 4l);
		int times = praUserReports.size() + 1;
		// List<AppRecordDomain> appRecordDomains=
		// hollandRecordDomain.getAppRecordDomains();

		for (int i = 0; i < praHollandRecordDomain.size(); i++) {
			PraAnswerRecord appRecordAnswer = new PraAnswerRecord();
			AppRecordDomain appRecordDomain = praHollandRecordDomain.get(i);
			appRecordAnswer.setUserId(account.getId());
			appRecordAnswer.setUserName(account.getName());
			appRecordAnswer.setQuestionId(appRecordDomain.getQuestionId());
			appRecordAnswer.setChoiceId(appRecordDomain.getChoiceId());
			// dimensionId=appRecordDomain.getDimensionId();
			appRecordAnswer.setDimensionId(appRecordDomain.getDimensionId());
			appRecordAnswer.setEvaluationId(4l);
			appRecordAnswer.setTimes(times);
			appRecordAnswer.setRecordDate(date);
			praAnswerRecordRepository.save(appRecordAnswer);
		}

		// 维护user_REport数据

		PraUserReport userReport = new PraUserReport();
		userReport.setUserId(userId);
		userReport.setUserName(userName);
		userReport.setEvaluationId(4l);
		userReport.setTimes(times + 1);
		// userReport.setReportDate(date);
		praUserReportRepository.save(userReport);

		result.put("success", "ok");
		return result;

	}

	public Map<String, Object> getValues() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<PraHollandDomain> praHollandDomains = new ArrayList<>();

		List<PraQuestion> praQuestions = praquestionRepository.findByEvaluationId(3l);

		for (int i = 0; i < praQuestions.size(); i++) {
			PraQuestion praQuestion = praQuestions.get(i);
			PraHollandDomain praHollanddomain = new PraHollandDomain();
			praHollanddomain.setQuestion(praQuestion.getQuestion());

			praHollanddomain.setNum(praQuestion.getNum());
			praHollanddomain.setQuestionId(praQuestion.getId());
			praHollanddomain.setMemo(praQuestion.getMemo());
			// praHollanddomain.setDimensionId(praQuestion.getDimension().getId());
			List<PraHollandChoiceDomain> praChoiceDomains = new ArrayList<>();
			List<PraChoice> praChoices = praChoiceRepository.findByEvaluationId(3l);
			for (int j = 0; j < praChoices.size(); j++) {
				PraChoice praChoice = praChoices.get(j);
				PraHollandChoiceDomain pradomain = new PraHollandChoiceDomain();
				pradomain.setChoiceId(praChoice.getId());
				pradomain.setChoiceCode(praChoice.getChoiceCode());
				pradomain.setChoice(praChoice.getChoiceContent());
				pradomain.setDimensionId(praQuestion.getDimension().getId());
				pradomain.setScore(praChoice.getScore());
				praChoiceDomains.add(pradomain);

			}
			praHollanddomain.setChoices(praChoiceDomains);
			praHollandDomains.add(praHollanddomain);

		}

		// 计算测评人数，打开题目后测评人数加1
		/*
		 * PraEvaluation appEvaluation= appEvaluationRepository.findOne(1l);
		 * Integer num1 = appEvaluation.getNum(); appEvaluation.setNum(1); }
		 * appEvaluationRepository.save(appEvaluation);
		 */
		result.put("praHollandDomains", praHollandDomains);
		return result;
	}

	public Map<String, Object> saveValuesRecord(List<AppRecordDomain> praHollandRecordDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		String userName = account.getName();
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date1);
		// 保存答题记录
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, 3l);
		int times = praUserReports.size() + 1;
		// List<AppRecordDomain> appRecordDomains=
		// hollandRecordDomain.getAppRecordDomains();

		for (int i = 0; i < praHollandRecordDomain.size(); i++) {
			PraAnswerRecord appRecordAnswer = new PraAnswerRecord();
			AppRecordDomain appRecordDomain = praHollandRecordDomain.get(i);
			appRecordAnswer.setUserId(account.getId());
			appRecordAnswer.setUserName(account.getName());
			appRecordAnswer.setQuestionId(appRecordDomain.getQuestionId());
			appRecordAnswer.setChoiceId(appRecordDomain.getChoiceId());
			appRecordAnswer.setDimensionId(appRecordDomain.getDimensionId());
			appRecordAnswer.setEvaluationId(3l);
			appRecordAnswer.setTimes(times);
			appRecordAnswer.setScore(appRecordDomain.getScore());
			appRecordAnswer.setRecordDate(date);
			praAnswerRecordRepository.save(appRecordAnswer);
		}

		PraUserReport userReport = new PraUserReport();
		userReport.setUserId(userId);
		userReport.setUserName(userName);
		userReport.setEvaluationId(3l);
		userReport.setTimes(times);
		// userReport.setReportDate(date);
		praUserReportRepository.save(userReport);

		result.put("success", "ok");
		return result;

	}

	public Map<String, Object> judgePerfessional(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		String userName = account.getName();
		Long evaluationId = 4l;
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, evaluationId);
		int times = praUserReports.size();

		// Dimension_REport数据
		List<PraDimension> dimensions = praDimensionRepository.findByEvaluationId(evaluationId);
		PerfessionalReportDomain perfessionalReportDomain = new PerfessionalReportDomain();

		List<PerfessionalDimensionDomain> PerfessionalDimensionDomains = new ArrayList<>();

		for (int j = 0; j < dimensions.size(); j++) {
			PraDimension dimension = dimensions.get(j);
			// double dimensionScore=0.0;
			List<PraAnswerRecord> answerRecords = praAnswerRecordRepository
					.findByUserIdAndEvaluationIdAndDimensionIdAndTimes(userId, evaluationId, dimension.getId(), times);

			PerfessionalDimensionDomain perfessionalDimensionDomain = new PerfessionalDimensionDomain();
			List<String> contents = new ArrayList<>();

			// 算每个维度的名词，维度的名词表示能力，要把这些能力展示出来
			for (int m = 0; m < answerRecords.size(); m++) {
				PraAnswerRecord answerRecord = answerRecords.get(m);
				contents.add(praquestionRepository.findOne(answerRecord.getQuestionId()).getQuestion());
			}

			perfessionalDimensionDomain.setDimensionId(dimension.getId());
			perfessionalDimensionDomain
					.setDimensionName(praDimensionDescriptionRepository.findByDimensionId(dimension.getId()).getName());
			perfessionalDimensionDomain.setDimensionDescription(
					praDimensionDescriptionRepository.findByDimensionId(dimension.getId()).getDescription());
			perfessionalDimensionDomain.setContent(contents);
			PerfessionalDimensionDomains.add(perfessionalDimensionDomain);
		}
		perfessionalReportDomain.setUserId(userId);
		perfessionalReportDomain.setUserName(userName);
		perfessionalReportDomain.setPerfessionalDimensionDomains(PerfessionalDimensionDomains);
		result.put("perfessionalReportDomain", perfessionalReportDomain);
		return result;
	}

	public Map<String, Object> judgeValues(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Long userId = account.getId();
		String userName = account.getName();
		Long evaluationId = 3l;
		List<PraUserReport> praUserReports = praUserReportRepository.findByUserIdAndEvaluationId(userId, evaluationId);
		int times = praUserReports.size();

		// 维护Dimension_REport数据
		TreeSet<PraDimensionScoreDomain> treeset = new TreeSet<PraDimensionScoreDomain>();

		List<PraDimension> dimensions = praDimensionRepository.findByEvaluationId(evaluationId);
		for (int j = 0; j < dimensions.size(); j++) {
			PraDimension dimension = dimensions.get(j);
			double dimensionScore = 0.0;
			List<PraAnswerRecord> answerRecords = praAnswerRecordRepository
					.findByUserIdAndEvaluationIdAndDimensionIdAndTimes(userId, evaluationId, dimension.getId(), times);
			// 算维度的分值
			for (int m = 0; m < answerRecords.size(); m++) {
				PraAnswerRecord answerRecord = answerRecords.get(m);
				dimensionScore = dimensionScore + answerRecord.getScore();

			}
			PraDimensionReport dimensionReport = new PraDimensionReport();
			dimensionReport.setScore(dimensionScore);
			dimensionReport.setUserId(userId);
			dimensionReport.setUserName(userName);
			dimensionReport.setDimensionId(dimension.getId());
			dimensionReport.setName(dimension.getName());
			// dimensionReport.setCost(cost);
			dimensionReport.setEvaluationId(evaluationId);
			dimensionReport.setTimes(times);
			// dimensionReport.setReportDate(new Date());
			praDimensionReportRepository.save(dimensionReport);
			PraDimensionScoreDomain dimensionScoreDomain = new PraDimensionScoreDomain();
			dimensionScoreDomain.setDimensionId(dimension.getId());
			dimensionScoreDomain.setDimensionName(dimension.getDescription());
			dimensionScoreDomain.setDimensionDescription(
					praDimensionDescriptionRepository.findByDimensionId(dimension.getId()).getDescription()); // dimensionScoreDomain.setDimensionSort(praDimensionRepository.findOne(appDimensionReport.getDimensionId()).getParentDimensionId());
			dimensionScoreDomain.setScore(dimensionScore);
			// dimensionScoreDomains.add(dimensionScoreDomain);
			treeset.add(dimensionScoreDomain);
		}

		result.put("valuesDomain", treeset);

		return result;
	}

	public Map<String, Object> getHollandJoinMBTI(AccountDTO account) {
		Map<String, Object> result = new HashMap<String, Object>();
		Set<PraJobsDomain> jobSet = new HashSet<PraJobsDomain>();

		List<PraUserReport> userReportHs = praUserReportRepository.findByUserIdAndEvaluationId(account.getId(), 1l);
		List<PraJobsDomain> hpraJobsDomains = new ArrayList<>();
		List<PraJobsDomain> praJobsDomains = new ArrayList<>();

		PraUserReport hUserReport = new PraUserReport();
		if (userReportHs.size() > 0) {
			for (int i = 0; i < userReportHs.size(); i++) {
				hUserReport = userReportHs.get(i);
			}

			// holland的推荐职位
			String jobStr = hUserReport.getMemo();
			List<PraJobsToEvaluation> praJobsToEvaluations = praJobsToEvaluationRepository.findByCode(jobStr);

			for (int j = 0; j < praJobsToEvaluations.size(); j++) {
				PraJobsToEvaluation praJobsToEvaluation = praJobsToEvaluations.get(j);
				PraJobsDomain praJobsDomain = new PraJobsDomain();
				praJobsDomain.setJobsId(praJobsToEvaluation.getPraJobs().getId());
				praJobsDomain.setName(praJobsToEvaluation.getPraJobs().getName());
				hpraJobsDomains.add(praJobsDomain);
			}

			/// result.put("HpraJobs", hpraJobsDomains);
		}

		// 取MBTI的报告
		List<PraUserReport> userReports = praUserReportRepository.findByUserIdAndEvaluationId(account.getId(), 2l);
		PraUserReport userReport = new PraUserReport();
		// String jobs =new String();
		if (userReports.size() > 0) {
			for (int i = 0; i < userReports.size(); i++) {
				userReport = userReports.get(i);

			}
			// 独立的推荐职位
			String jobStr = userReport.getMemo();
			List<PraJobsToEvaluation> praJobsToEvaluations = praJobsToEvaluationRepository.findByCode(jobStr);

			for (int j = 0; j < praJobsToEvaluations.size(); j++) {
				PraJobsToEvaluation praJobsToEvaluation = praJobsToEvaluations.get(j);
				PraJobsDomain praJobsDomain = new PraJobsDomain();
				praJobsDomain.setJobsId(praJobsToEvaluation.getPraJobs().getId());
				praJobsDomain.setName(praJobsToEvaluation.getPraJobs().getName());
				praJobsDomains.add(praJobsDomain);
			}

			// result.put("MpraJobs", praJobsDomains);

		}

		if (hpraJobsDomains.size() >= 1) {
			for (int k = 0; k < hpraJobsDomains.size(); k++) {
				PraJobsDomain praJobsDomain1 = hpraJobsDomains.get(k);
				jobSet.add(praJobsDomain1);
			}
		}
		if (praJobsDomains.size() >= 1) {
			for (int l = 0; l < praJobsDomains.size(); l++) {
				PraJobsDomain praJobsDomain2 = praJobsDomains.get(l);
				jobSet.add(praJobsDomain2);
			}
		}

		result.put("jobSet", jobSet);
		return result;
	}

	/*
	 * public Map<String, Object> getHollandStatistic() { Map<String,Object>
	 * result = new HashMap<String, Object>(); List<HollandStatisticsDomain>
	 * hollandStatisticsDomains = new ArrayList<>(); List<AppUserReport>
	 * appUserReports = (List<AppUserReport>) appUserReportRepository.findAll();
	 * for( int i=0;i<appUserReports.size();i++){ HollandStatisticsDomain
	 * hollandStatisticsDomain = new HollandStatisticsDomain(); AppUserReport
	 * appUserReport = appUserReports.get(i);
	 * hollandStatisticsDomain.setUser(appUserReport.getUserName());
	 * 
	 * User user = userRepository.findByUserId(appUserReport.getUserId());
	 * if(user!=null){ hollandStatisticsDomain.setLogin(user.getLogin());
	 * hollandStatisticsDomain.setAge(user.getAge());
	 * hollandStatisticsDomain.setGender(user.getGender());
	 * hollandStatisticsDomain.setClassName(user.getClassName());
	 * hollandStatisticsDomain.setMajor(user.getMajor());
	 * hollandStatisticsDomain.setShool(user.getOrgan()); }
	 * 
	 * AppUserChoice appUserChoice
	 * =appUserChoiceRepository.findByEvaluationIdAndUserIdAndTimes(1l,
	 * appUserReport.getUserId(), appUserReport.getTimes());
	 * hollandStatisticsDomain.setTimes(appUserReport.getTimes());
	 * hollandStatisticsDomain.setIslandCode(appUserChoice.getCode());
	 * hollandStatisticsDomain.setCode(appUserChoice.getResultCode());
	 * 
	 * List<HollandDimensionsDomain> hollandDimensionsDomains = new
	 * ArrayList<>(); List<AppDimensionReport> appDimensionReports =
	 * appDimensionReportRepository.findByEvaluationIdAndUserIdAndTimes(1l,
	 * appUserReport.getUserId(),appUserReport.getTimes()); for(int
	 * j=0;j<appDimensionReports.size();j++){ AppDimensionReport
	 * appDimensionReport = appDimensionReports.get(j); HollandDimensionsDomain
	 * hollandDimensionsDomain = new HollandDimensionsDomain();
	 * hollandDimensionsDomain.setDimensionId(appDimensionReport.getDimensionId(
	 * ));
	 * hollandDimensionsDomain.setDimensionName(appDimensionRepository.findOne(
	 * appDimensionReport.getDimensionId()).getName());
	 * hollandDimensionsDomain.setNum(appDimensionReport.getScore());
	 * hollandDimensionsDomains.add(hollandDimensionsDomain); }
	 * hollandStatisticsDomain.setHollandDimensionsDomains(
	 * hollandDimensionsDomains);
	 * hollandStatisticsDomains.add(hollandStatisticsDomain); }
	 * result.put("hollandStatisticsDomains", hollandStatisticsDomains); return
	 * result; }
	 * 
	 * 
	 */

}
