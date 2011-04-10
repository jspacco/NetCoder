CREATE CACHED TABLE events (
  id bigint IDENTITY,
  user_id integer NOT NULL,
  problem_id integer NOT NULL,
  type integer NOT NULL,
  timestamp bigint NOT NULL,

  FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);

CREATE CACHED TABLE changes (
  id integer IDENTITY,
  event_id integer NOT NULL,
  type integer NOT NULL,   -- no enum datatypes in hsqldb
  start_row integer NOT NULL,
  end_row integer NOT NULL,
  start_col integer NOT NULL,
  end_col integer NOT NULL,
  text longvarchar NOT NULL,
  
  FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE CACHED TABLE problems (
  problem_id integer IDENTITY,
  testname varchar(255) NOT NULL,
  description longvarchar NOT NULL
);

INSERT INTO problems VALUES(NULL, 'sq', 'Square a number.');

CREATE CACHED TABLE test_cases (
  test_case_id integer IDENTITY,
  problem_id integer NOT NULL,
  test_case_name varchar(40),
  input longvarchar NOT NULL,
  output longvarchar NOT NULL,
  FOREIGN KEY (problem_id) REFERENCES problems(problem_id)
);

INSERT INTO test_cases VALUES(NULL, 0, 'Test1', '5', '25');
INSERT INTO test_cases VALUES(NULL, 0, 'Test2', '9', '81');
INSERT INTO test_cases VALUES(NULL, 0, 'Test3', '10', '100');
INSERT INTO test_cases VALUES(NULL, 0, 'Test4', '-1', '1');
