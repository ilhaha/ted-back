SET @parentId = 1987687676254683136;
-- 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理', 1000, 2, '/training/orgTrainingPaymentAudit', 'OrgTrainingPaymentAudit', 'training/orgTrainingPaymentAudit/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构培训缴费审核（记录考生参与机构培训的缴费及审核流程）管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1987687676254683137, '列表', @parentId, 3, 'training:orgTrainingPaymentAudit:list', 1, 1, 1, NOW()),
    (1987687676254683138, '详情', @parentId, 3, 'training:orgTrainingPaymentAudit:detail', 2, 1, 1, NOW()),
    (1987687676254683139, '新增', @parentId, 3, 'training:orgTrainingPaymentAudit:add', 3, 1, 1, NOW()),
    (1987687676254683140, '修改', @parentId, 3, 'training:orgTrainingPaymentAudit:update', 4, 1, 1, NOW()),
    (1987687676254683141, '删除', @parentId, 3, 'training:orgTrainingPaymentAudit:delete', 5, 1, 1, NOW()),
    (1987687676254683142, '导出', @parentId, 3, 'training:orgTrainingPaymentAudit:export', 6, 1, 1, NOW());

