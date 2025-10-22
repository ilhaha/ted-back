package top.continew.admin.exam.model.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 考生准考证数据对象（CandidateTicketDO）
 * </p>
 * <p>
 * 本类整合了考生、班级、考试安排等多表数据，用于准考证生成。
 * </p>
 *
 * @since 2025-10-21
 */
@Data
public class CandidateTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 考生姓名 */
    private String name;

    /** 身份证号 */
    private String idCard;

    /** 准考证号 */
    private String ticketId;

    /** 班级代码 */
    private Long classCode;

    /** 班级名称 */
    private String className;

    /** 考核种类 */
    private String examType;

    /** 考核项目 */
    private String examItem;

    /** 考场名 */
    private String examRoom;

    /** 考试时间段（如 2025-05-20 09:00-11:00） */
    private String examTime;

    /** 考生照片 */
    private String photo;
}
