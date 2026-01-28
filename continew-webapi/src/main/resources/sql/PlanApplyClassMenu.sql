SET @parentId = 2016319771243786240;
-- 考试计划报考班级管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试计划报考班级管理', 1000, 2, '/exam/planApplyClass', 'PlanApplyClass', 'exam/planApplyClass/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试计划报考班级管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2016319771243786241, '列表', @parentId, 3, 'exam:planApplyClass:list', 1, 1, 1, NOW()),
    (2016319771243786242, '详情', @parentId, 3, 'exam:planApplyClass:detail', 2, 1, 1, NOW()),
    (2016319771243786243, '新增', @parentId, 3, 'exam:planApplyClass:add', 3, 1, 1, NOW()),
    (2016319771243786244, '修改', @parentId, 3, 'exam:planApplyClass:update', 4, 1, 1, NOW()),
    (2016319771243786245, '删除', @parentId, 3, 'exam:planApplyClass:delete', 5, 1, 1, NOW()),
    (2016319771243786246, '导出', @parentId, 3, 'exam:planApplyClass:export', 6, 1, 1, NOW());

