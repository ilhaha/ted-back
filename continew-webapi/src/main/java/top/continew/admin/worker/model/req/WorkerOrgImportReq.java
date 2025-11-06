package top.continew.admin.worker.model.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/11/6 15:42
 */
@Data
public class WorkerOrgImportReq implements Serializable {

    /**
     * aesPhone
     */
    private String encFieldA;

    /**
     * aesIdCard
     */
    private String encFieldB;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 作业人员姓名
     */
    private String candidateName;

    /**
     * 作业人员性别
     */
    private String gender;

    /**
     * 作业人员手机号
     */
    private String phone;

    /**
     * 报名资格申请表路径
     */
    private String qualificationPath;

    /**
     * 报名资格申请表名称
     */
    private String qualificationName;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 身份证正面存储地址
     */
    private String idCardPhotoFront;

    /**
     * 身份证反面存储地址
     */
    private String idCardPhotoBack;

    /**
     * 一寸免冠照存储地址
     */
    private String facePhoto;

    /**
     * 报名方式，0作业人员自报名，1机构批量导入
     */
    private Integer applyType;

    /**
     * 审核状态:0待审核,1已生效,2未通过
     */
    private Integer status;

    /**
     * 资料映射
     */
    private Map<String,String> docMap;

}
