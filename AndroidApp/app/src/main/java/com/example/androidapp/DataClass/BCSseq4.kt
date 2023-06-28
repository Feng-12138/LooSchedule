package com.example.androidapp.DataClass

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.viewModels.ScheduleViewModel
import com.google.gson.Gson

class BCSseq4 {
    val courseList1A = listOf(
        Course("CS 145", "Designing Functional Programs (Advanced Level)",
            0.7316017316017316f, "CS 145 is an advanced-level version of CS 135. [Note: See Note 2 above. This course may be substituted for CS 135 in any degree plan or for prerequisite purposes. Offered: F]"),
        Course("MATH 145", "Algebra (Advanced Level)", 0.953757225433526f,
            "MATH 145 is an advanced-level version of MATH 135. [Offered: F]"),
        Course("MATH 147", "Calculus 1 (Advanced Level)", 0.9235668789808917f,
            "MATH 147 is an advanced-level version of MATH 137. [Offered: F]"),
        Course("EMLS 129R", "Written Academic English", 0.48333333333333334f,
        "Designed specifically for students for whom English is not the first language, this writing skills course provides instruction in grammar, sentence and paragraph structure, elements of composition, and academic essay writing, including a focus on theme, development of central ideas, exposition, and argumentation. [Note: Not open to fluent writers of English.]"),
        Course("PHYS 111", "Physics 1", 0.4128205128205128f,
    "An introduction to physics for students intending to concentrate their further studies in biology, dentistry, medicine and paramedicine; includes particle kinematics and dynamics, energy and momentum conservation, and rotational mechanics. [Offered: F, W; also offered online: W]")
    )

    val courseList1B = listOf(
        Course("CS 146", "Elementary Algorithm Design and Data Abstraction (Advanced Level)", 0.9015151f,
            "CS 146 is an advanced-level version of CS 136. [Note: See Note 2 above. This course may be substituted for CS 136 in any degree plan or for prerequisite purposes. Students who receive a good grade in CS 135 may contact the instructor of CS 146 to seek admission without the formal prerequisites. Offered: W]"),
        Course("MATH 146", "Linear Algebra 1 (Advanced Level)", 0.89719623f,
            "MATH 146 is an advanced-level version of MATH 136. [Note: Students who receive a minimum grade of 90% in MATH 135 may contact the instructor of MATH 146 to seek admission without the formal prerequisites. Offered: W]"),
        Course("MATH 148", "Calculus 2 (Advanced Level)", 0.85294116f,
            "MATH 148 is an advanced-level version of MATH 138. [Note: Students who receive a minimum grade of 90% in MATH 137 may contact the instructor of MATH 148 to seek admission without the formal prerequisites. Offered: W]"),
        Course("STAT 230", "Probability", 0.5981183f,
            "This course provides an introduction to probability models including sample spaces, mutually exclusive and independent events, conditional probability and Bayes' Theorem. The named distributions (Discrete Uniform, Hypergeometric, Binomial, Negative Binomial, Geometric, Poisson, Continuous Uniform, Exponential, Normal (Gaussian), and Multinomial) are used to model real phenomena. Discrete and continuous univariate random variables and their distributions are discussed. Joint probability functions, marginal probability functions, and conditional probability functions of two or more discrete random variables and functions of random variables are also discussed. Students learn how to calculate and interpret means, variances and covariances particularly for the named distributions. The Central Limit Theorem is used to approximate probabilities. [Note: Many upper-year Statistics courses require a grade of at least 60% in STAT 230. Offered: F,W,S]"),
        Course("ECON 101", "Introduction to Microeconomics", 0.4533228f,
            "This course provides an introduction to microeconomic analysis relevant for understanding the Canadian economy. The behaviour of individual consumers and producers, the determination of market prices for commodities and resources, and the role of government policy in the functioning of the market system are the main topics covered.")
    )
    val courseList2A = listOf(
        Course("CS 245", "Logic and Computation", 0.27333334f,
            "Logic as a tool for representation, reasoning, and computation. Propositional and predicate logic. Formalizing the notions of correct and incorrect reasoning, defining what is computable, and exploring the limits of computation. Godel's Incompleteness Theorem. Applications of logic to computer science."),
        Course("CS 246", "Object-Oriented Software Development", 0.6248175f,
            "Introduction to object-oriented programming and to tools and techniques for software development. Designing, coding, debugging, testing, and documenting medium-sized programs: reading specifications and designing software to implement them; selecting appropriate data structures and control structures; writing reusable code; reusing existing code; basic performance issues; debuggers; test suites. [Note: Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("MATH 239", "Introduction to Combinatorics", 0.7636656f,
            "Introduction to graph theory: colourings, matchings, connectivity, planarity. Introduction to combinatorial analysis: generating series, recurrence relations, binary strings, plane trees. [Offered: F,W,S]"),
        Course("STAT 231", "Statistics", 0.3740876f,
            "This course provides a systematic approach to empirical problem solving which will enable students to critically assess the sampling protocol and conclusions of an empirical study including the possible sources of error in the study and whether evidence of a causal relationship can be reasonably concluded. The connection between the attributes of a population and the parameters in the named distributions covered in STAT 230 will be emphasized. Numerical and graphical techniques for summarizing data and checking the fit of a statistical model will be discussed. The method of maximum likelihood will be used to obtain point and interval estimates for the parameters of interest as well as testing hypotheses. The interpretation of confidence intervals and p-values will be emphasized. The Chi-squared and t distributions will be introduced and used to construct confidence intervals and tests of hypotheses including likelihood ratio tests. Contingency tables and Gaussian response models including the two sample Gaussian and simple linear regression will be used as examples. [Note: Many upper-year Statistics courses require a grade of at least 60% in STAT 231. Offered: F,W,S]"),
        Course("ECON 102", "Introduction to Macroeconomics", 0.5045181f,
            "This course introduces students to the measurement and behaviour of key macroeconomic variables both in Canada and around the world. Topics include national accounts, inflation, interest rates, wages, international balance of payments, business cycles, growth, employment, unemployment, poverty, and inequality.")
    )
    val WT1 = listOf<Course>()
    val courseList2B = listOf(
        Course("CS 240", "Data Structures and Data Management", 0.56458634f,
            "Introduction to widely used and effective methods of data organization, focusing on data structures, their algorithms, and the performance of these algorithms. Specific topics include priority queues, sorting, dictionaries, data structures for text processing. [Note: Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CS 241", "Foundations of Sequential Programs", 0.8856589f,
            "The relationship between high-level languages and the computer architecture that underlies their implementation, including basic machine architecture, assemblers, specification and translation of programming languages, linkers and loaders, block-structured languages, parameter passing mechanisms, and comparison of programming languages. [Note: Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. CS 251 is a recommended corequisite. Offered: F,W,S]"),
        Course("CS 251", "Computer Organization and Design", 0.6612022f,
            "Overview of computer organization and performance. Basics of digital logic design. Combinational and sequential elements. Data representation and manipulation. Basics of processor design. Pipelining. Memory hierarchies. Multiprocessors. [Note: Students enrolled in Digital Hardware Specialization should enrol in ECE 222. Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("EMLS 103R", "Effective English Pronunciation", 0.47368422f,
            "Designed specifically for students for whom English is not the first language, this course has an intensive focus on the structure of the English sound system with a view to improving pronunciation. Vowel and consonant sounds are practised as well as the phonetic elements above those sounds, such as stress, intonation, and rhythm. The goal is for students to identify their problem areas and to develop strategies to improve their comprehensibility.  [Note: Not open to fluent speakers of English.]"),
        Course("PHYS 112", "Physics 2", 0.58f,
            "A continuation of PHYS 111; includes simple harmonic motion, electrostatic force and potential, electric current and power, DC circuits, magnetic field and induction, wave motion, sound and optics. [Offered: W,S; also offered online: S]")
    )
    val WT2 = listOf<Course>()
    val courseList3A = listOf(
        Course("CS 341", "Algorithms", 0.88206786f,
            "The study of efficient algorithms and effective algorithm design techniques. Program design with emphasis on pragmatic and mathematical aspects of program efficiency. Topics include divide and conquer algorithms, recurrences, greedy algorithms, dynamic programming, graph search and backtrack, problems without algorithms, NP-completeness and its implications. [Note: Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CS 348", "Introduction to Database Management", 0.36900368f,
            "The main objective of this course is to introduce students to fundamentals of database technology by studying databases from three viewpoints: those of the database user, the database designer, and the database administrator. It teaches the use of a database management system (DBMS) by treating it as a black box, focusing only on its functionality and its interfaces. Topics include introduction to database systems, relational database systems, database design methodology, SQL and interfaces, database application development, concept of transactions, ODBC, JDBC, database tuning, database administration, and current topics (distributed databases, data warehouses, data mining). [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CS 350", "Operating Systems", 0.81609195f,
            "An introduction to the fundamentals of operating system function, design, and implementation. Topics include concurrency, synchronization, processes, threads, scheduling, memory management, file systems, device management, and security. [Note: Enrolment is restricted; see Note 1 above. Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CHEM 120", "General Chemistry 1", 0.7064364f,
            "The stoichiometry of compounds and chemical reactions. Properties of gases. Periodicity and chemical bonding. Energy changes in chemical systems. Electronic structure of atoms and molecules; correlation with the chemical reactivity of common elements, inorganic and organic compounds. [Note: Science students must also take CHEM 120L. Successful completion of Grade 12 U Calculus and Vectors and Grade 12 U Chemistry or equivalent courses is recommended; Offered: F, W]"),
        Course("CLAS 104", "Classical Mythology", 0.8558282f,
            "A study of Greco-Roman mythology and legend, with special emphasis on the Olympian gods and the figure of the hero. Topics may include myths of creation, the rise of the gods, divine myths, the tales surrounding the cities of Troy, Mycenae, and Thebes and the heroes Herakles, Perseus, and Theseus.")
    )
    val WT3 = listOf<Course>()
    val courseList3B = listOf(
        Course("CS 370", "Numerical Computation", 0.78640777f,
            "Principles and practices of basic numerical computation as a key aspect of scientific computation. Visualization of results. Approximation by splines, fast Fourier transforms, solution of linear and nonlinear equations, differential equations, floating point number systems, error, stability. Presented in the context of specific applications to image processing, analysis of data, scientific modeling. [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CS 446", "Software Design and Architectures", 0.625f,
            "Introduces students to the design, implementation, and evolution phases of software development. Software design processes, methods, and notation. Implementation of designs. Evolution of designs and implementations. Management of design activities. [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: W,S]"),
        Course("CS 486", "Introduction to Artificial Intelligence", 0.9130435f,
            "Goals and methods of artificial intelligence. Methods of general problem solving. Knowledge representation and reasoning. Planning. Reasoning about uncertainty. Machine learning. Multi-agent systems. Natural language processing. [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("ECON 371", "Business Finance 1", 0.62222224f,
            "The course explores decisions faced by managers of firms. In particular, decision-makers must determine which long-term real investment opportunities to exploit. Once undertaken, managers must decide how to finance the projects, for example, by debt or equity. The course develops both the conceptual framework and the tools required for these decisions. The course assumes prior familiarity with probability, expected values, and variance."),
        Course("JAPAN 101R", "First-Year Japanese 1", 0.9160305f,
        "An introductory course for students who have little or no knowledge of Japanese to develop basic listening, speaking, reading, and writing skills. Practical oral and written exercises incorporating the Hiragana Writing System provide a firm grammatical foundation for further study. [Note: JAPAN 101R is not open to students with native, near-native, or similar advanced ability.]")

    )
    val WT4 = listOf<Course>()
    val courseList4A = listOf(
        Course("CS 349", "User Interfaces", 0.76679844f,
            "An introduction to contemporary user interfaces, including the basics of human-computer interaction, the user interface design/evaluation process, the event abstraction, user interface components, specification of user interfaces, and the architectures within which user interfaces are developed. Implementation and evaluation of a typical user interface is considered. [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CS 480", "Introduction to Machine Learning", 0.6923077f,
            "Introduction to modeling and algorithmic techniques for machines to learn concepts from data. Generalization: underfitting, overfitting, cross-validation. Tasks: classification, regression, clustering. Optimization-based learning: loss minimization. regularization. Statistical learning: maximum likelihood, Bayesian learning. Algorithms: nearest neighbour, (generalized) linear regression, mixtures of Gaussians, Gaussian processes, kernel methods, support vector machines, deep learning, sequence learning, ensemble techniques. Large scale learning: distributed learning and stream learning. Applications: Natural language processing, computer vision, data mining, human computer interaction, information retrieval. [Note: Lab is not scheduled and students are expected to find time in open hours to complete their work. Offered: F,W,S]"),
        Course("CO 487", "Applied Cryptography", 0.88461536f,
            "A broad introduction to modern cryptography, highlighting the tools and techniques used to secure internet and messaging applications. Symmetric-key encryption, hash functions, message authentication, authenticated encryption, public-key encryption and digital signatures, key establishment, key management. [Offered: F,W]"),
        Course("AFM 101", "Introduction to Financial Accounting", 0.53266335f,
            "This course is an introduction to financial accounting. The preparation and use of financial statements is examined. The accounting cycle and assets and liabilities reporting, is discussed.")
    )
    val WT5 = listOf<Course>()
    val WT6 = listOf<Course>()
//    val courseList4B = listOf(
//        Course("4B 1"), Course("4B 2"), Course("4B 3"),
//        Course("4B 4"), Course("4B 5")
//    )

    val test = Schedule(mapOf("1A" to courseList1A, "1B" to courseList1B,
        "2A" to courseList2A, "2B" to courseList2B,
        "3A" to courseList3A, "3B" to courseList3B,
        "4A" to courseList4A))

    val test2 = Schedule(mapOf("1A" to courseList1A, "1B" to courseList1B,
        "2A" to courseList2A, "2B" to courseList2B,
        "3A" to courseList3A, "3B" to courseList3B,
        "4A" to courseList4A))


    var testList: List<Schedule> = listOf(test, test2)

}