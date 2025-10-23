package top.continew.admin.document.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;

/**
 * 机构报考-考生上传资料详情信息
 *
 * @author ilhaha
 * @since 2025/10/23 10:03
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "机构报考-考生上传资料详情信息")
public class DocumentPreDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构报考-考生扫码上传文件表id
     */
    @Schema(description = "机构报考-考生扫码上传文件表id")
    @ExcelProperty(value = "机构报考-考生扫码上传文件表id")
    private Long enrollPreUploadId;

    /**
     * 存储路径
     */
    @Schema(description = "存储路径")
    @ExcelProperty(value = "存储路径")
    private String docPath;

    /**
     * 关联资料类型ID
     */
    @Schema(description = "关联资料类型ID")
    @ExcelProperty(value = "关联资料类型ID")
    private Long typeId;

    /**
     * 删除标记(0未删,1已删)
     */
    @Schema(description = "删除标记(0未删,1已删)")
    @ExcelProperty(value = "删除标记(0未删,1已删)")
    private Boolean isDeleted;
}