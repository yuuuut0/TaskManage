CREATE TABLE `task_approvals` (
  `approval_id` int NOT NULL AUTO_INCREMENT,
  `task_id` int NOT NULL,
  `approver_id` varchar(50) DEFAULT NULL,
  `assignee_id` varchar(50) DEFAULT NULL,
  `comment` varchar(255) NOT NULL DEFAULT '',
  `reply` varchar(255) DEFAULT '',
  `submitted_at` datetime NOT NULL,
  `approved_at` datetime DEFAULT NULL,
  `approver_flg` tinyint(1) NOT NULL DEFAULT '0',
  `assignee_flg` tinyint(1) NOT NULL DEFAULT '0',
  `result` tinyint(1) NOT NULL DEFAULT '0',
  `project_id` varchar(50) NOT NULL,
  PRIMARY KEY (`approval_id`),
  KEY `task` (`task_id`),
  KEY `approver_idx` (`approver_id`),
  KEY `assignee_idx` (`assignee_id`),
  KEY `project_idx` (`project_id`),
  CONSTRAINT `approver` FOREIGN KEY (`approver_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `assignee` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `task` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci