package top.continew.admin.exam.model.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassExamTableResp implements Serializable {

    /** 班级名称 */
    private String className;

    /** 及格人数 */
    private long passCount;

    /** 不及格人数 */
    private long failCount;

    /** 本班级下的成绩列表（表格数据） */
    private List<ExamRecordsResp> records;
}
