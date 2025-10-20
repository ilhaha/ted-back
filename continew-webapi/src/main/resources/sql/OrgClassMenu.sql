SET @parentId = 1979120838553944064;
-- 培训机构班级管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '培训机构班级管理', 1000, 2, '/training/orgClass', 'OrgClass', 'training/orgClass/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 培训机构班级管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1979120838553944065, '列表', @parentId, 3, 'training:orgClass:list', 1, 1, 1, NOW()),
    (1979120838553944066, '详情', @parentId, 3, 'training:orgClass:detail', 2, 1, 1, NOW()),
    (1979120838553944067, '新增', @parentId, 3, 'training:orgClass:add', 3, 1, 1, NOW()),
    (1979120838553944068, '修改', @parentId, 3, 'training:orgClass:update', 4, 1, 1, NOW()),
    (1979120838553944069, '删除', @parentId, 3, 'training:orgClass:delete', 5, 1, 1, NOW()),
    (1979120838553944070, '导出', @parentId, 3, 'training:orgClass:export', 6, 1, 1, NOW());

