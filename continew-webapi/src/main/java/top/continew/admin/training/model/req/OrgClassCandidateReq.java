package top.continew.admin.training.model.req;


import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 创建或修改机构班级与考生关联表参数
 *
 * @author ilhaha
 * @since 2025/10/21 16:48
 */
@Data
@Schema(description = "创建或修改机构班级与考生关联表参数")
public class OrgClassCandidateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}