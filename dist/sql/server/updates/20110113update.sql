ALTER TABLE `accounts`
  ADD `lastIP2` CHAR(15) NULL DEFAULT NULL
  AFTER `lastIP`;
ALTER TABLE `accounts`
  ADD `lastIP3` CHAR(15) NULL DEFAULT NULL
  AFTER `lastIP2`;
