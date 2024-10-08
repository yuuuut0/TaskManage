CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `username` varchar(30) NOT NULL,
  `handle` varchar(4) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `last_active_at` datetime NOT NULL,
  `project_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `project_id_idx` (`project_id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tasks` (
  `task_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `assigned_user_id` varchar(50) DEFAULT NULL,
  `sub_completed` int NOT NULL DEFAULT '0',
  `sub_total` int NOT NULL DEFAULT '0',
  `submit_flg` tinyint(1) NOT NULL DEFAULT '0',
  `completed_flg` tinyint(1) NOT NULL DEFAULT '0',
  `connect_flg` tinyint(1) NOT NULL DEFAULT '0',
  `priority` tinyint unsigned NOT NULL DEFAULT '1',
  `deadline` datetime DEFAULT NULL,
  `project_id` varchar(50) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `completed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  KEY `assigned_user_id_idx` (`assigned_user_id`),
  KEY `project_id_idx` (`project_id`) /*!80000 INVISIBLE */,
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `assigned_user_id` FOREIGN KEY (`assigned_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `projects` (
  `project_id` varchar(50) NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(1000) DEFAULT '',
  `first_task_id` int NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`project_id`),
  KEY `first_task_id` (`first_task_id`),
  CONSTRAINT `first_task_id` FOREIGN KEY (`first_task_id`) REFERENCES `tasks` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `users`
  ADD CONSTRAINT `users.project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `tasks`
  ADD CONSTRAINT `tasks.project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `user_projects` (
  `user_id` varchar(50) NOT NULL,
  `project_id` varchar(50) NOT NULL,
  `handle` varchar(4) NOT NULL,
  `unapproved_count` int NOT NULL DEFAULT '0',
  `requests_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`project_id`),
  KEY `idx_project_id` (`project_id`,`user_id`),
  CONSTRAINT `project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;