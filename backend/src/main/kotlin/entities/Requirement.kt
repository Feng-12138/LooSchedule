package entities

import javax.persistence.*

enum class DegreeType {
    MAJOR,
    MINOR,
    SPECIALIZATION,
    Joint,
    DIPLOMA,
    OPTION
}
typealias Year = String             // Year which the degree plan is for
typealias CourseList = String       // List of required courses for the degree
typealias RequirementNotes = String // Any additional notes in degree requirements
typealias URL = String              // Link to undergrad calendar for accurate information

@Entity
@Table(name = "Requirement")
class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long? = null

    @Column(name = "type", nullable = false)
    var type: DegreeType? = null

    @Column(name = "year", nullable = false)
    var year: Year? = null

    @Column(name = "courses")
    var courses: CourseList? = null

    @Column(name = "additionalRequirements")
    var additionalRequirements: RequirementNotes? = null

    @Column(name = "link")
    var link: URL? = null
}
