package top.continew.admin.worker.model.resp;

import akka.dispatch.sysmsg.Failed;
import lombok.Data;
import top.continew.admin.training.model.vo.ExcelRowErrorVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ilhaha
 * @Create 2025/12/3 10:40
 */
@Data
public class UploadResulResp {

    /**
     * 上传成功的身份证列表
     */
    private List<String> successIdCards = new ArrayList<>();

    /**
     * 失败列表
     */
    private List<FailedUploadResp> failedList = new ArrayList<>();
}
