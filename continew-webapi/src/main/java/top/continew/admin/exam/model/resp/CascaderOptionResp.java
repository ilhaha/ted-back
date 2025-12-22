package top.continew.admin.exam.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serializable;
import java.util.List;

/**
 * 级联选择器节点
 * 父节点：考试计划
 * 子节点：准考证号
 *
 * @author ilhaha
 * @Create 2025/12/22 10:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CascaderOptionResp implements Serializable {

    /**
     * 节点值
     */
    private Object value;

    /**
     * 节点显示名称
     */
    private String label;

    /**
     * 子节点列表
     */
    private List<CascaderOptionResp> children;

    public CascaderOptionResp(Object value, String label) {
        this.value = value;
        this.label = label;
    }


}