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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci