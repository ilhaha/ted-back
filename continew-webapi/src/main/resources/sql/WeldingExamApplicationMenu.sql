SET @parentId = 2011996409570873344;
-- 机构申请焊接考试项目管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '机构申请焊接考试项目管理', 1000, 2, '/exam/weldingExamApplication', 'WeldingExamApplication', 'exam/weldingExamApplication/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构申请焊接考试项目管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2011996409570873345, '列表', @parentId, 3, 'exam:weldingExamApplication:list', 1, 1, 1, NOW()),
    (2011996409570873346, '详情', @parentId, 3, 'exam:weldingExamApplication:detail', 2, 1, 1, NOW()),
    (2011996409570873347, '新增', @parentId, 3, 'exam:weldingExamApplication:add', 3, 1, 1, NOW()),
    (2011996409570873348, '修改', @parentId, 3, 'exam:weldingExamApplication:update', 4, 1, 1, NOW()),
    (2011996409570873349, '删除', @parentId, 3, 'exam:weldingExamApplication:delete', 5, 1, 1, NOW()),
    (2011996409570873350, '导出', @parentId, 3, 'exam:weldingExamApplication:export', 6, 1, 1, NOW());

