SET @parentId = 2005449439377178624;
-- 人员复审信息表管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '人员复审信息表管理', 1000, 2, '/exam/personQualification', 'PersonQualification', 'exam/personQualification/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 人员复审信息表管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2005449439377178625, '列表', @parentId, 3, 'exam:personQualification:list', 1, 1, 1, NOW()),
    (2005449439377178626, '详情', @parentId, 3, 'exam:personQualification:detail', 2, 1, 1, NOW()),
    (2005449439377178627, '新增', @parentId, 3, 'exam:personQualification:add', 3, 1, 1, NOW()),
    (2005449439377178628, '修改', @parentId, 3, 'exam:personQualification:update', 4, 1, 1, NOW()),
    (2005449439377178629, '删除', @parentId, 3, 'exam:personQualification:delete', 5, 1, 1, NOW()),
    (2005449439377178630, '导出', @parentId, 3, 'exam:personQualification:export', 6, 1, 1, NOW());

