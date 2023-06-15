CREATE TABLE IF NOT EXISTS Course
(
    courseID VARCHAR(15) NOT NULL,
    courseName VARCHAR(100) NOT NULL,
    subject VARCHAR(10) NOT NULL,
    code VARCHAR(5) NOT NULL,
    description VARCHAR(500) NOT NULL,
    credit DECIMAL(10,2) NOT NULL,
    availability VARCHAR(15),
    onlineTerms VARCHAR(15),
    coreqs VARCHAR(500),
    antireqs VARCHAR(500),
    likedRating DECIMAL(21, 20),
    easyRating DECIMAL(21, 20),
    usefulRating DECIMAL(21, 20),
    PRIMARY KEY (courseID)
);

CREATE TABLE IF NOT EXISTS Communication
(
    courseID VARCHAR(15) NOT NULL,
    subject VARCHAR(10) NOT NULL,
    code VARCHAR(5) NOT NULL,
    listNumber INT(1) NOT NULL,
    year VARCHAR(9) NOT NULL,
    -- FOREIGN KEY (courseID) REFERENCES Course(courseID),
    PRIMARY KEY (courseID, year)
);

CREATE TABLE IF NOT EXISTS Breath
(
    courseID VARCHAR(15) NOT NULL,
    subject VARCHAR(10) NOT NULL,
    code VARCHAR(5) NOT NULL,
    category VARCHAR(15) NOT NULL,
    -- FOREIGN KEY (courseID) REFERENCES Course(courseID),
    PRIMARY KEY (courseID)
);

CREATE TABLE IF NOT EXISTS Prerequisite
(
    courseID VARCHAR(15) NOT NULL,
    consentRequired BOOLEAN NOT NULL,
    courses VARCHAR(500),
    minimumLevel VARCHAR(5),
    onlyOpenTo VARCHAR(100),
    notOpenTo VARCHAR(100),
    FOREIGN KEY (courseID) REFERENCES Course(courseID),
    PRIMARY KEY (courseID)
);

CREATE TABLE IF NOT EXISTS Requirement
(
    requirementID INT(11) NOT NULL,
    type VARCHAR(50) NOT NULL,
    year VARCHAR(10) NOT NULL,
    courses VARCHAR(1000) NOT NULL,
    additionalRequirements VARCHAR(1000),
    link VARCHAR(250),
    PRIMARY KEY (requirementID)
);

CREATE TABLE IF NOT EXISTS Major
(
    requirementID INT(11) NOT NULL,
    majorName VARCHAR(100) NOT NULL,
    isCoop BOOLEAN NOT NULL,
    isDoubleDegree BOOLEAN NOT NULL,
    FOREIGN KEY (requirementID) REFERENCES Requirement(requirementID),
    PRIMARY KEY (requirementID)
);

CREATE TABLE IF NOT EXISTS Minor
(
    requirementID INT(11) NOT NULL,
    minorName VARCHAR(100) NOT NULL,
    FOREIGN KEY (requirementID) REFERENCES Requirement(requirementID),
    PRIMARY KEY (requirementID)
);

CREATE TABLE IF NOT EXISTS Specialization
(
    requirementID INT(11) NOT NULL,
    specializationName VARCHAR(100) NOT NULL,
    FOREIGN KEY (requirementID) REFERENCES Requirement(requirementID),
    PRIMARY KEY (requirementID)
);

CREATE TABLE IF NOT EXISTS Joint
(
    requirementID INT(11) NOT NULL,
    jointName VARCHAR(100) NOT NULL,
    FOREIGN KEY (requirementID) REFERENCES Requirement(requirementID),
    PRIMARY KEY (requirementID)
);