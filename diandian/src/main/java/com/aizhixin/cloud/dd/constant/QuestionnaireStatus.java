package com.aizhixin.cloud.dd.constant;

/**
 * 
 * 类名称：QuestionStatus
 * 创建人：MEIHUA.LI
 * 创建时间：2016年11月1日 下午4:56:19
 * 修改人：MEIHUA.LI
 * 修改时间：2016年11月1日 下午4:56:19
 * 修改备注：
 */
public class QuestionnaireStatus implements BaseConstants {

    /**
     * 问卷状态：初始化
     */
    public final static int QUESTION_STATUS_INIT                    = 10;
    /**
     * 问卷状态：已分配
     */
    public final static int QUESTION_STATUS_ASSIGN                  = 20;

    /**
     * 问卷是否删除:正常
     */
    public final static int QUESTION_DELETEFLAG_NORMAL              = 0;
    /**
     * 问卷是否删除:已删除
     */
    public final static int QUESTION_DELETEFLAG_DELETE              = 1;
    
    public final static int DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT   = 10;
    public final static int DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH = 20;

}