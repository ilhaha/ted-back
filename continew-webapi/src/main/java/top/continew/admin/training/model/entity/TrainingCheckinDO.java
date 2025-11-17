package top.continew.admin.training.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 培训签到记录实体
 *
 * @author ilhaha
 * @since 2025/11/17 11:31
 */
@Data
@TableName("ted_training_checkin")
public class TrainingCheckinDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 培训ID
     */
    private Long trainingId;

    /**
     * 考生ID（对应 sys_user.id）
     */
    private Long candidateId;

    /**
     * 机构ID（冗余）
     */
    private Long orgId;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 扫码时二维码的时间戳
     */
    private Long qrTimestamp;

    /**
     * 二维码签名（校验）
     */
    private String qrSign;

    /**
     * 1：正常签到 2: 过期二维码 3：伪造二维码
     */
    private Boolean status;
}