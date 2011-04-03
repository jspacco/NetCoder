CREATE CACHED TABLE changes (
  id integer IDENTITY,
  user_id integer NOT NULL,
  problem_id integer NOT NULL,
  type varchar(20) DEFAULT NULL,   -- no enum datatypes in hsqldb
  start_row integer NOT NULL,
  end_row integer NOT NULL,
  start_col integer NOT NULL,
  end_col integer NOT NULL,
  timestamp bigint NOT NULL,
  text longvarchar NOT NULL
);

CREATE CACHED TABLE problems (
  problem_id integer IDENTITY,
  testname varchar(255) NOT NULL,
  description longvarchar NOT NULL
);

INSERT INTO problems VALUES(NULL, 'sq', 'Square a number.');

CREATE CACHED TABLE testcases (
  testcase_id integer IDENTITY,
  problem_id integer NOT NULL,
  input longvarchar NOT NULL,
  output longvarchar NOT NULL,
  FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);

-- creator.addTest(5, 25);
-- creator.addTest(9, 81);
-- creator.addTest(10, 100);
-- creator.addTest(-1, 1);

INSERT INTO testcases VALUES(NULL, 1, '5', '25');
INSERT INTO testcases VALUES(NULL, 1, '9', '81');
INSERT INTO testcases VALUES(NULL, 1, '10', '100');
INSERT INTO testcases VALUES(NULL, 1, '-1', '1');
