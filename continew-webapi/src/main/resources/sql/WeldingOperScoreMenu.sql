SET @parentId = 2013867244485173248;
-- 焊接项目实操成绩管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '焊接项目实操成绩管理', 1000, 2, '/exam/weldingOperScore', 'WeldingOperScore', 'exam/weldingOperScore/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 焊接项目实操成绩管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2013867244485173249, '列表', @parentId, 3, 'exam:weldingOperScore:list', 1, 1, 1, NOW()),
    (2013867244485173250, '详情', @parentId, 3, 'exam:weldingOperScore:detail', 2, 1, 1, NOW()),
    (2013867244485173251, '新增', @parentId, 3, 'exam:weldingOperScore:add', 3, 1, 1, NOW()),
    (2013867244485173252, '修改', @parentId, 3, 'exam:weldingOperScore:update', 4, 1, 1, NOW()),
    (2013867244485173253, '删除', @parentId, 3, 'exam:weldingOperScore:delete', 5, 1, 1, NOW()),
    (2013867244485173254, '导出', @parentId, 3, 'exam:weldingOperScore:export', 6, 1, 1, NOW());

