SET @parentId = 1909074282459525120;
-- 步骤，存储题目的不同回答步骤管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '步骤，存储题目的不同回答步骤管理', 1000, 2, '/examconnect/step', 'Step', 'examconnect/step/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 步骤，存储题目的不同回答步骤管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909074282459525121, '列表', @parentId, 3, 'examconnect:step:list', 1, 1, 1, NOW()),
    (1909074282459525122, '详情', @parentId, 3, 'examconnect:step:detail', 2, 1, 1, NOW()),
    (1909074282459525123, '新增', @parentId, 3, 'examconnect:step:add', 3, 1, 1, NOW()),
    (1909074282459525124, '修改', @parentId, 3, 'examconnect:step:update', 4, 1, 1, NOW()),
    (1909074282459525125, '删除', @parentId, 3, 'examconnect:step:delete', 5, 1, 1, NOW()),
    (1909074282459525126, '导出', @parentId, 3, 'examconnect:step:export', 6, 1, 1, NOW());

